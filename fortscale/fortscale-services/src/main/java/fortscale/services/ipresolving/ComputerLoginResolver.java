package fortscale.services.ipresolving;

import fortscale.domain.events.ComputerLoginEvent;
import fortscale.domain.events.DhcpEvent;
import fortscale.domain.events.dao.ComputerLoginEventRepository;
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

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


public class ComputerLoginResolver extends CachingServiceWithBlackList<String,ComputerLoginEvent> {

	private static final Logger logger = LoggerFactory.getLogger(ComputerLoginResolver.class);
	
	@Autowired
	private ComputerLoginEventRepository computerLoginEventRepository;
	
	@Value("${computer.login.resolver.leaseTimeInMins:600}") // TGT lease time is default to 10 hours
	private int leaseTimeInMins;
	@Value("${computer.login.resolver.ipToHostNameUpdateResolutionInMins:60}") 
	private int ipToHostNameUpdateResolutionInMins;
	@Value("${computer.login.resolver.graceTimeInMins:1}")
	private int graceTimeInMins;
	@Value("${computer.login.resolver.is.use.cache.for.resolving:true}")
	private boolean isUseCacheForResolving;

	@Value("${computer.login.resolver.shouldUseBlackList:true}")
	private boolean shouldUseBlackList;

	@Autowired
	@Qualifier("loginResolverCache")
	private CacheHandler<String,ComputerLoginEvent> cache;

	// blackIpHashSetCache is used to keep track of ip addresses that couldn't not be resolved into hostname using
	// the login events. We keep track of those ip addresses to prevent us from looking them up over and over again
	// for each ip we keep the latest know time range in which there isn't resolving
	@Autowired
	@Qualifier("loginBlacklistCache")
	private CacheHandler<String,TimeRange> ipBlackListCache;

	public ComputerLoginResolver() {
		super(ComputerLoginEvent.class);
	}


	@Override public CacheHandler<String, ComputerLoginEvent> getCache() {
		return cache;
	}

	public void setCache(CacheHandler<String,ComputerLoginEvent> cache) {
		this.cache = cache;
	}

	public void setIpBlackListCache(CacheHandler<String, TimeRange> ipBlackListCache) {
		this.ipBlackListCache = ipBlackListCache;
	}

	// for unit testing
	public void setShouldUseBlackList(boolean shouldUseBlackList) {
		this.shouldUseBlackList = shouldUseBlackList;
	}

	public void setUseCacheForResolving(boolean isUseCacheForResolving) {
		this.isUseCacheForResolving = isUseCacheForResolving;
	}

	public String getHostname(String ip, long ts) {
		ComputerLoginEvent event = getComputerLoginEvent(ip, ts);
		return (event!=null)? event.getHostname() : null;
	}
	
	public ComputerLoginEvent getComputerLoginEvent(String ip, long ts) {
		if(computerLoginEventRepository == null){
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

		long upperLimitTs = (graceTimeInMins > 0)? ts + graceTimeInMins * 60 * 1000: ts;
		long lowerLimitTs = ts - leaseTimeInMins * 60 * 1000;
		ComputerLoginEvent loginEvent = null;
		// check if we have a matching event in the cache
		if(isUseCacheForResolving){
			loginEvent = cache.get(ip);
			if (loginEvent!=null &&
					loginEvent.getTimestampepoch() >= lowerLimitTs &&
					loginEvent.getTimestampepoch() <= upperLimitTs) {
	
				return loginEvent;
			}
		}
		
		// if cache not found resort to the repository check
		PageRequest pageRequest = new PageRequest(0, 1, Direction.DESC, ComputerLoginEvent.TIMESTAMP_EPOCH_FIELD_NAME);
		List<ComputerLoginEvent> computerLoginEvents = computerLoginEventRepository.findByIpaddressAndTimestampepochBetween(ip, lowerLimitTs, upperLimitTs, pageRequest);
		if(!computerLoginEvents.isEmpty()) {
			// we do not update the cache here as the next ip resolving might have a slightly newer timestamp with an that was resolved to a different hostname
			// so we rely on the cache to hold only the newest timestamp for resolving, thus we can make sure there is not other hostname for that ip
			return computerLoginEvents.get(0);
		}
		addToBlackList(ip, ts, loginEvent, lowerLimitTs, upperLimitTs);
		return null;
	}
	
	public void addComputerLogins(Iterable<ComputerLoginEvent> events) {
	    // save all events in the 
		List<ComputerLoginEvent> eventsToSaveInDB = new ArrayList<>();
	    for (ComputerLoginEvent event : events){
	    	if(isToUpdate(event)){
	    		eventsToSaveInDB.add(event);
	    		cache.put(event.getIpaddress(), event);
				removeFromBlackList(event);
			}
	    }

	    computerLoginEventRepository.save(eventsToSaveInDB);
	}
	
	public void addComputerLogin(ComputerLoginEvent event) {
		checkNotNull(event);
		String ip = event.getIpaddress();
		checkNotNull(ip);

		if (isToUpdate(event)) {
			computerLoginEventRepository.save(event);
			cache.put(ip, event);
			removeFromBlackList(event);
		} else{
			logger.debug("skipping ip to hostname login event with hostname={}, ip={}, timestamp={}", event.getHostname(), event.getIpaddress(), event.getTimestampepoch());
		}
	}
	
	protected boolean isToUpdate(ComputerLoginEvent event){
		String ip = event.getIpaddress();

		// check if the event is in the cache
		ComputerLoginEvent cachedEvent =  cache.get(ip);
		if (cachedEvent==null) {
			return true;
		} else {
			// if the event is in the cache, check if the new event has a different hostname
			// if the event is in the cache and has the same hostname, update it only if the ticket expiration time passed
			if ((!event.getHostname().equals(cachedEvent.getHostname())) || (event.getTimestampepoch() > cachedEvent.getTimestampepoch() +  (ipToHostNameUpdateResolutionInMins * 60 * 1000))) {
				return true;
			}
		}

		return false;
	}


	// we only save in the blacklist the last time range we know there is no resolving for.
	// since every time we add a time range to the blacklist we get the biggest time range possible from mongo, according to the queried ts
	// the only scenarios in which we won't update the blacklist is in which the existing value is regarding a newer time period.
	protected void addToBlackList(String ip, long ts, ComputerLoginEvent loginEvent, long lowerLimitTs, long upperLimitTs){
		if (shouldUseBlackList) {
			List<ComputerLoginEvent> nextComputerLoginEvents = computerLoginEventRepository.findByIpaddressAndTimestampepochGreaterThanEqual(ip, upperLimitTs, new PageRequest(0, 1, Direction.ASC, DhcpEvent.TIMESTAMP_EPOCH_FIELD_NAME));
			Long startTimestamp = lowerLimitTs;
			Long endTimestamp = null;
			if (!nextComputerLoginEvents.isEmpty()) {
				ComputerLoginEvent computerLoginEvent = nextComputerLoginEvents.get(0);
				endTimestamp = computerLoginEvent.getTimestampepoch();
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
	public void removeFromBlackList(ComputerLoginEvent loginEvent){
		if (shouldUseBlackList) {
			if (ipBlackListCache.containsKey(loginEvent.getIpaddress())){
				TimeRange blacklistTimeRange = ipBlackListCache.get(loginEvent.getIpaddress());
				//if the current resolving limits the time range in the black list, update the end timestamp saved in the black list
				if (loginEvent.getTimestampepoch() != null && blacklistTimeRange.include(loginEvent.getTimestampepoch() - graceTimeInMins * 60 * 1000)){
					blacklistTimeRange.setEndTimestamp(loginEvent.getTimestampepoch() - graceTimeInMins * 60 * 1000);
					ipBlackListCache.put(loginEvent.getIpaddress(), blacklistTimeRange);
				}
				//in any other case we remove the black list entry
				else{
					ipBlackListCache.remove(loginEvent.getIpaddress());
				}
			}
		}
	}
	
}
