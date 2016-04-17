package fortscale.services.ipresolving;

import fortscale.domain.events.DhcpEvent;
import fortscale.domain.events.dao.DhcpEventRepository;
import fortscale.services.cache.CacheHandler;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang3.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.List;


public class DhcpResolver extends GeneralIpResolver<DhcpEvent> {

	private static Logger logger = LoggerFactory.getLogger(DhcpResolver.class);
	@Autowired
	private DhcpEventRepository dhcpEventRepository;
	
	@Value("${dhcp.resolver.leaseTimeInSec:5}")
	private int graceTimeInSec;

	@Autowired
	@Qualifier("dhcpResolverCache")
	private CacheHandler<String,DhcpEvent> cache;



	public DhcpResolver(boolean shouldUseBlackList, CacheHandler<String,Range<Long>> ipBlackListCache) {
		super(shouldUseBlackList, ipBlackListCache, DhcpEvent.class);
	}

	public DhcpResolver(){

	}

	@Override public CacheHandler<String, DhcpEvent> getCache() {
		return cache;
	}

	public void setCache(CacheHandler<String,DhcpEvent> cache) {
		this.cache = cache;
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
		Range<Long> timeRange = ipBlackListCache.get(ip);
		if (shouldUseBlackList && timeRange != null && timeRange.contains(ts)) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("IP %s is in the black list and the ts %s is between time range %s - %s. Skipping it.", ip, ts, timeRange.getMinimum(), timeRange.getMaximum()));
			}
			return null;
		}

		long upperTsLimit = (graceTimeInSec > 0)? ts + TimestampUtils.convertToMilliSeconds(graceTimeInSec)  : ts;

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
		//take the expiration date as the lower limit of the black list period only if the given event is previous to the given event ts
		long lowerLimitTs = dhcpEvent != null && (dhcpEvent.getExpiration() < ts) ? dhcpEvent.getExpiration() : 0;
		addToBlackList(ip, lowerLimitTs, upperTsLimit);
		return null;
	}
	
	public String getHostname(String ip, long ts) {
		DhcpEvent event = getLatestDhcpEventBeforeTimestamp(ip, ts);
		return (event!=null)? event.getHostname() : null;
	}

	protected List<DhcpEvent> getNextEvents(String ip, Long upperTsLimit) {
		return dhcpEventRepository.findByIpaddressAndTimestampepochGreaterThanEqual(ip, upperTsLimit, new PageRequest(0, 1, Sort.Direction.ASC, DhcpEvent.TIMESTAMP_EPOCH_FIELD_NAME));
	}

	@Override
	protected void removeFromBlackList(DhcpEvent event) {
		removeFromBlackList(event.getIpaddress(), event.getTimestampepoch(), event.getExpiration());
	}

}
