package fortscale.services.ipresolving;

import fortscale.domain.events.DhcpEvent;
import fortscale.domain.events.dao.DhcpEventRepository;
import fortscale.services.CachingServiceWithBlackList;
import fortscale.services.cache.CacheHandler;
import fortscale.utils.TimeRange;
import fortscale.utils.TimestampUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;


public class DhcpResolver extends CachingServiceWithBlackList<String,DhcpEvent> {

	private static Logger logger = LoggerFactory.getLogger(DhcpResolver.class);
	@Autowired
	private DhcpEventRepository dhcpEventRepository;
	
	@Value("${dhcp.resolver.leaseTimeInMins:1}")
	private int graceTimeInMins;

	@Value("${dhcp.resolver.shouldUseBlackList:true}")
	private boolean shouldUseBlackList;
	
	@Autowired
	@Qualifier("dhcpResolverCache")
	private CacheHandler<String,DhcpEvent> cache;

	// blackIpHashSetCache is used to keep track of ip addresses that couldn't not be resolved into hostname using
	// the dhcp events. We keep track of those ip addresses to prevent us from looking them up over and over again
	// for each ip we keep the latest know time range in which there isn't resolving
	@Autowired
	@Qualifier("dhcpBlacklistCache")
	private CacheHandler<String,TimeRange> ipBlackListCache;

	public DhcpResolver() {
		super(DhcpEvent.class);
	}


	@Override public CacheHandler<String, DhcpEvent> getCache() {
		return cache;
	}

	public void setCache(CacheHandler<String,DhcpEvent> cache) {
		this.cache = cache;
	}

	public void setIpBlackListCache(CacheHandler<String, TimeRange> ipBlackListCache) {
		this.ipBlackListCache = ipBlackListCache;
	}

	// for unit testing
	public void setShouldUseBlackList(boolean shouldUseBlackList) {
		this.shouldUseBlackList = shouldUseBlackList;
	}

	/**
	 * Handle the dhcp event and update repository when required.
	 * Dhcp event can contain assign, release to expired action codes.
	 * Note: this implementation assumes all events are received in chronological order for each ip address, there
	 * are some cases where we will not handle events as expected if they are received out of order
	 */
	public void addDhcpEvent(DhcpEvent event) {
		// add assigned events to repository
		if (DhcpEvent.ASSIGN_ACTION.equals(event.getAction())) {
			// see that we don't already have such an event in cache with the same 
			// expiration time and hostname
			DhcpEvent cached = cache.get(event.getIpaddress());
			if (cached!=null && cached.getHostname().equals(event.getHostname()) && cached.getExpiration()>= event.getExpiration())
				return;

			// get the latest assignment from the repository if cache is empty
			if (cached==null) {
				List<DhcpEvent> dhcpEvents = dhcpEventRepository.findByIpaddressAndTimestampepochLessThan(event.getIpaddress(), event.getTimestampepoch(),
						new PageRequest(0, 1, Direction.DESC, DhcpEvent.TIMESTAMP_EPOCH_FIELD_NAME));
				if (!dhcpEvents.isEmpty()) {
					cached = dhcpEvents.get(0);
				}
			}


			// put in cache if the cache is empty
			if (cached==null) {
				cache.put(event.getIpaddress(), event);
				dhcpEventRepository.save(event);
			} else {
				// if we got assign to a new hostname update cache and mongo
				if (!cached.getHostname().equals(event.getHostname())) {
					// check if we need to update the expiration time of the existing record, and there
					// is time overlap between the events
					if (cached.getExpiration() > event.getTimestampepoch() && cached.getTimestampepoch() < event.getExpiration()) {
						cached.setExpiration(event.getTimestampepoch());
						// update the existing event in mongodb and in the cache
						cache.put(cached.getIpaddress(), cached);
						dhcpEventRepository.save(cached);
						removeFromBlackList(cached);
					}

					// update cache with the new event only if it is not older event than the existing one
					if (cached.getTimestampepoch() < event.getTimestampepoch()) {
						cache.put(event.getIpaddress(), event);
						removeFromBlackList(event);
					}
					dhcpEventRepository.save(event);
				} else {
					// for the same hostname as cached event, check if we need to update the 
					// expiration date on the cached event
					if (event.getExpiration() > cached.getExpiration()) {
						cached.setExpiration(event.getExpiration());
						cache.put(event.getIpaddress(), event);
						dhcpEventRepository.save(event);
						removeFromBlackList(event);
					}
				}
			}
		}
		// end previous assignment in case of expiration or release
		if (DhcpEvent.RELEASE_ACTION.equals(event.getAction()) || DhcpEvent.EXPIRED_ACTION.equals(event.getAction())) {
			// check if we have an existing dhcp event than need to be updated with expiration time
			DhcpEvent cached = cache.get(event.getIpaddress());
			if (cached!=null && cached.getHostname().equals(event.getHostname()) && cached.getExpiration() > event.getTimestampepoch()) {
				cached.setExpiration(event.getExpiration());
				cache.put(cached.getIpaddress(), cached);
				removeFromBlackList(cached);
			}
			
			// update saved event in repository as well
			List<DhcpEvent> dhcpEvents = dhcpEventRepository.findByIpaddressAndTimestampepochLessThan(event.getIpaddress(), event.getTimestampepoch(),
					new PageRequest(0, 1, Direction.DESC, DhcpEvent.TIMESTAMP_EPOCH_FIELD_NAME));
			if (!dhcpEvents.isEmpty()) {
				DhcpEvent existing = dhcpEvents.get(0);
				if (existing.getHostname().equals(event.getHostname()) && existing.getExpiration() > event.getTimestampepoch()) {
					// mark previous event as expired once the ip is released
					existing.setExpiration(event.getTimestampepoch());
					dhcpEventRepository.save(existing);
					removeFromBlackList(existing);

					// update cache
					if (cached==null) {
						removeFromBlackList(existing);
						cache.put(existing.getIpaddress(), existing);
					}
				}
			}
		}
	}
	
	public DhcpEvent getLatestDhcpEventBeforeTimestamp(String ip, long ts) {
		if(dhcpEventRepository == null){
			return null;
		}

		ts = TimestampUtils.convertToMilliSeconds(ts);

		// if
		// 1. Need to use the blacklist and
		// 2. The IP is in the blacklist
		// 3. The ts is included in the time range.
		//	Than the ip is not in the cache or MongoDB and we should skip it.
		TimeRange timeRange = ipBlackListCache.get(ip);
		if (shouldUseBlackList && timeRange != null && timeRange.include(ts)) {
			logger.debug(String.format("IP %s is in the black list and the ts %s is between time range %s - %s. Skipping it.", ip, ts, timeRange.getStartTimestamp(), timeRange.getEndTimestamp()));
			return null;
		}

		long upperTsLimit = (graceTimeInMins > 0)? ts + graceTimeInMins * 60 * 1000 : ts;

		// see if we have a matching event in cache
		DhcpEvent dhcpEvent = cache.get(ip);
		if (dhcpEvent!=null && dhcpEvent.getTimestampepoch()<=upperTsLimit && dhcpEvent.getExpiration() >= ts) {
			// return cached event
			return dhcpEvent;
		}

		// if the event was not in cache than look for it in the repository
		List<DhcpEvent> dhcpEvents = dhcpEventRepository.findByIpaddressAndTimestampepochLessThan(ip, upperTsLimit,
				new PageRequest(0, 1, Direction.DESC, DhcpEvent.TIMESTAMP_EPOCH_FIELD_NAME));
		if(!dhcpEvents.isEmpty()){
			// check if the ip assignment is not expired
			dhcpEvent = dhcpEvents.get(0);
			if (dhcpEvent.getExpiration() >= ts) {
				// also add the event to the cache for next time
				cache.put(ip, dhcpEvent);
				return dhcpEvent;
			}
		}
		addToBlackList(ip, ts, dhcpEvent, upperTsLimit);
		return null;
	}
	
	public String getHostname(String ip, long ts) {
		DhcpEvent event = getLatestDhcpEventBeforeTimestamp(ip, ts);
		return (event!=null)? event.getHostname() : null;
	}


	// we only save in the blacklist the last time range we know there is no resolving for.
	// since every time we add a time range to the blacklist we get the biggest time range possible from mongo, according to the queried ts
	// the only scenarios in which we won't update the blacklist is in which the existing value is regarding a newer time period.
	protected void addToBlackList(String ip, long ts, DhcpEvent dhcpEvent, long upperTsLimit){
		if (shouldUseBlackList) {
			List<DhcpEvent> nextDhcpEvents = dhcpEventRepository.findByIpaddressAndTimestampepochGreaterThanEqual(ip, upperTsLimit, new PageRequest(0, 1, Direction.ASC, DhcpEvent.TIMESTAMP_EPOCH_FIELD_NAME));
			Long startTimestamp = null;
			Long endTimestamp = null;
			if (dhcpEvent != null) {
				startTimestamp = dhcpEvent.getExpiration();
			}
			if (!nextDhcpEvents.isEmpty()) {
				DhcpEvent nextDhcpEvent = nextDhcpEvents.get(0);
				endTimestamp = nextDhcpEvent.getTimestampepoch();
			}
			TimeRange newBlacklistTimeRange = new TimeRange(startTimestamp,endTimestamp);
			if (ipBlackListCache.containsKey(ip)){
				TimeRange blacklistTimeRange = ipBlackListCache.get(ip);
				if (!newBlacklistTimeRange.before(blacklistTimeRange)) {
					ipBlackListCache.put(ip, newBlacklistTimeRange);
				}
			}
			else {
				ipBlackListCache.put(ip, newBlacklistTimeRange);
			}
		}
	}

	//TODO: improve the logic of the blacklist time range
	// currently we support only 2 scenarios
	// 1. the new resolving event limits the end of the current time range saved in the black list.
	//    in this case we keep the blacklist record but limits its end time to the time epoch of the new resolving event
	// 2. the current time range saved in the black list and the new resolving event intersect with it in some manner
	//    in this case we remove the blacklist record completely
	public void removeFromBlackList(DhcpEvent dhcpEvent){
		if (shouldUseBlackList) {
			if (ipBlackListCache.containsKey(dhcpEvent.getIpaddress())){
				TimeRange blacklistTimeRange = ipBlackListCache.get(dhcpEvent.getIpaddress());
				TimeRange resolvedTimeRange = new TimeRange(dhcpEvent.getTimestampepoch(),dhcpEvent.getExpiration());
				//if the current resolving limits the time range in the black list, update the end timestamp saved in the black list
				if (dhcpEvent.getTimestampepoch() != null && blacklistTimeRange.include(dhcpEvent.getTimestampepoch())){
					blacklistTimeRange.setEndTimestamp(dhcpEvent.getTimestampepoch());
					ipBlackListCache.put(dhcpEvent.getIpaddress(), blacklistTimeRange);
				}
				//if there is an intersection between the new resolving timestamp and the know black list range remove the black list entry
				else if(blacklistTimeRange.intersect(resolvedTimeRange)){
					ipBlackListCache.remove(dhcpEvent.getIpaddress());
				}
			}
		}
	}
}
