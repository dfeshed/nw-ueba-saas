package fortscale.services.ipresolving;

import fortscale.domain.events.ComputerLoginEvent;
import fortscale.domain.events.DhcpEvent;
import fortscale.domain.events.dao.ComputerLoginEventRepository;
import fortscale.services.cache.CacheHandler;
import fortscale.services.ipresolving.metrics.ComputerLoginResolverMetrics;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang3.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


public class ComputerLoginResolver extends GeneralIpResolver<ComputerLoginEvent> implements InitializingBean{

	private static final Logger logger = LoggerFactory.getLogger(ComputerLoginResolver.class);
	
	@Autowired
	private ComputerLoginEventRepository computerLoginEventRepository;
	
	@Value("${computer.login.resolver.leaseTimeInMins:600}") // TGT lease time is default to 10 hours
	private int leaseTimeInMins;
	@Value("${computer.login.resolver.ipToHostNameUpdateResolutionInMins:60}")
	private int ipToHostNameUpdateResolutionInMins;
	@Value("${computer.login.resolver.graceTimeInSec:5}")
	private int graceTimeInSec;
	@Value("${computer.login.resolver.is.use.cache.for.resolving:true}")
	private boolean isUseCacheForResolving;

	@Autowired
	@Qualifier("loginResolverCache")
	private CacheHandler<String,ComputerLoginEvent> cache;

	@Autowired
	protected StatsService statsService;
	private ComputerLoginResolverMetrics metrics;

	public ComputerLoginResolver(boolean shouldUseBlackList, CacheHandler<String,Range<Long>> ipBlackListCache) {
		super(shouldUseBlackList, ipBlackListCache, ComputerLoginEvent.class);
	}

	//for testing
	protected ComputerLoginResolver()
	{

	}

	@Override public CacheHandler<String, ComputerLoginEvent> getCache() {
		return cache;
	}

	public void setCache(CacheHandler<String,ComputerLoginEvent> cache) {
		this.cache = cache;
	}

	public void setUseCacheForResolving(boolean isUseCacheForResolving) {
		this.isUseCacheForResolving = isUseCacheForResolving;
	}

	//for testing
	protected void setIpToHostNameUpdateResolutionInMins(int ipToHostNameUpdateResolutionInMins) {
		this.ipToHostNameUpdateResolutionInMins = ipToHostNameUpdateResolutionInMins;
	}
	//for testing
	protected void setLeaseTimeInMins(int leaseTimeInMins) {
		this.leaseTimeInMins = leaseTimeInMins;
	}

	public String getHostname(String ip, long ts) {
		ComputerLoginEvent event = getComputerLoginEvent(ip, ts);
		return (event!=null)? event.getHostname() : null;
	}
	
	public ComputerLoginEvent getComputerLoginEvent(String ip, long ts) {
		if(computerLoginEventRepository == null){
			metrics.computerLoginEventRepositoryNull++;
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
			metrics.ipInBlackListAndTsInTimeRange++;
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("IP %s is in the black list and the ts %s is between time range %s - %s. Skipping it.", ip, ts, timeRange.getMinimum(), timeRange.getMaximum()));
			}
			return null;
		}

		long upperLimitTs = (graceTimeInSec > 0)? ts + TimestampUtils.convertToMilliSeconds(graceTimeInSec) : ts;
		long lowerLimitTs = ts - leaseTimeInMins * 60 * 1000;
		ComputerLoginEvent loginEvent = null;
		// check if we have a matching event in the cache
		if(isUseCacheForResolving){
			loginEvent = cache.get(ip);
			if (loginEvent!=null &&
					loginEvent.getTimestampepoch() >= lowerLimitTs &&
					loginEvent.getTimestampepoch() <= upperLimitTs) {

				metrics.foundComputerLoginEventInCache++;
				return loginEvent;
			}
		}
		
		// if cache not found resort to the repository check
		PageRequest pageRequest = new PageRequest(0, 1, Direction.DESC, ComputerLoginEvent.TIMESTAMP_EPOCH_FIELD_NAME);
		List<ComputerLoginEvent> computerLoginEvents = computerLoginEventRepository.findByIpaddressAndTimestampepochBetween(ip, lowerLimitTs, upperLimitTs, pageRequest);
		if(!computerLoginEvents.isEmpty()) {
			// we do not update the cache here as the next ip resolving might have a slightly newer timestamp with an that was resolved to a different hostname
			// so we rely on the cache to hold only the newest timestamp for resolving, thus we can make sure there is not other hostname for that ip

			ComputerLoginEvent resolving = computerLoginEvents.get(0);
			//return the resolving only if
			// 1. The resolving data is not part of vpn session
			// or
			// 2.the event time stamp was before the relevant vpn session was closed
			long tsMiliSec = TimestampUtils.convertToMilliSeconds(ts) + TimestampUtils.convertToMilliSeconds(graceTimeInSec);

			if (!resolving.isPartOfVpn() ||( tsMiliSec <= TimestampUtils.convertToMilliSeconds(resolving.getExpirationVpnSessiondt()) && tsMiliSec >= TimestampUtils.convertToMilliSeconds(resolving.getTimestampepoch())) ) {

				metrics.computerLoginEventFoundInRepository++;
				return resolving;
			}
		}
		addToBlackList(ip, ts, upperLimitTs);
		metrics.computerLoginIpAddedToBlackList++;
		return null;
	}
	
	public void addComputerLogins(Iterable<ComputerLoginEvent> events) {
	    // save all events in the 
		List<ComputerLoginEvent> eventsToSaveInDB = new ArrayList<>();
	    for (ComputerLoginEvent event : events){
			metrics.checkingIfComputerLoginNeedsUpdate++;
	    	if(isToUpdate(event)){
	    		eventsToSaveInDB.add(event);
	    		cache.put(event.getIpaddress(), event);
				removeFromBlackList(event);
			}
	    }

	    computerLoginEventRepository.save(eventsToSaveInDB);
	}
	
	public void addComputerLogin(ComputerLoginEvent event) {
		metrics.checkingIfComputerLoginNeedsUpdate++;
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
			metrics.computerLoginUpdated++;
			return true;
		} else {
			// if the event is in the cache, check if the new event has a different hostname
			// if the event is in the cache and has the same hostname, update it only if the ticket expiration time passed
			if ((!event.getHostname().equals(cachedEvent.getHostname())) || (event.getTimestampepoch() > cachedEvent.getTimestampepoch() +  (ipToHostNameUpdateResolutionInMins * 60 * 1000))) {
				metrics.computerLoginUpdated++;
				return true;
			}
		}

		metrics.computerLoginNotUpdated++;
		return false;
	}

	protected void removeFromCache(String ip)
	{
		this.cache.remove(ip);
	}


	protected List<ComputerLoginEvent> getNextEvents(String ip, Long upperTsLimit) {
		return computerLoginEventRepository.findByIpaddressAndTimestampepochGreaterThanEqual(ip, upperTsLimit, new PageRequest(0, 1, Sort.Direction.ASC, DhcpEvent.TIMESTAMP_EPOCH_FIELD_NAME));
	}

	@Override
	protected void removeFromBlackList(ComputerLoginEvent event) {
		removeFromBlackList(event.getIpaddress(), event.getTimestampepoch() - (TimestampUtils.convertToMilliSeconds(graceTimeInSec)), event.getTimestampepoch() +  (leaseTimeInMins * 60 * 1000));
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		metrics = new ComputerLoginResolverMetrics(statsService, "main");

	}
	
}
