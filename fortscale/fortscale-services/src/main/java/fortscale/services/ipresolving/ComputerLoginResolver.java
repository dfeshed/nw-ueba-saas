package fortscale.services.ipresolving;


import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import fortscale.domain.events.ComputerLoginEvent;
import fortscale.domain.events.dao.ComputerLoginEventRepository;
import fortscale.utils.TimestampUtils;


@Service("computerLoginResolver")
@Scope("singleton")
public class ComputerLoginResolver implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(ComputerLoginResolver.class);
	
	@Autowired
	private ComputerLoginEventRepository computerLoginEventRepository;
	
	@Value("${computer.login.resolver.leaseTimeInMins:600}") // TGT lease time is default to 10 hours
	private int leaseTimeInMins;
	@Value("${computer.login.resolver.graceTimeInMins:1}")
	private int graceTimeInMins;
	@Value("${computer.login.resolver.cache.max.items:30000}")
	private int cacheMaxSize;
	
	private Cache<String, ComputerLoginEvent> cache;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// create a cache of ip login events
		cache = CacheBuilder.newBuilder().maximumSize(cacheMaxSize).build();
	}
	

	public String getHostname(String ip, long ts) {
		if(computerLoginEventRepository == null){
			return null;
		}
		ts = TimestampUtils.convertToMilliSeconds(ts);

        logger.info("computerLoginEventRepository is not null timestampEphoc is:"+ts);
		
		// check if we have a matching event in the cache
		ComputerLoginEvent cachedEvent = cache.getIfPresent(ip);
		if (cachedEvent!=null && 
				cachedEvent.getTimestampepoch() >= ts - leaseTimeInMins*60*1000 && 
				cachedEvent.getTimestampepoch() <= ts + graceTimeInMins*60*1000) {
			return cachedEvent.getHostname();
		}
		
		// if cache not found resort to the repository check
		long upperLimitTs = (graceTimeInMins > 0)? ts + graceTimeInMins * 60 * 1000: ts;
		long lowerLimitTs = ts - leaseTimeInMins * 60 * 1000;
		PageRequest pageRequest = new PageRequest(0, 1, Direction.DESC, ComputerLoginEvent.TIMESTAMP_EPOCH_FIELD_NAME);
		List<ComputerLoginEvent> computerLoginEvents = computerLoginEventRepository.findByIpaddressAndTimestampepochBetween(ip, lowerLimitTs, upperLimitTs, pageRequest);
		if(!computerLoginEvents.isEmpty()) {
			// we do not update the cache here as the next ip resolving might have a slightly newer timestamp with an that was resolved to a different hostname
			// so we rely on the cache to hold only the newest timestamp for resolving, thus we can make sure there is not other hostname for that ip

			return computerLoginEvents.get(0).getHostname();
		}
		
		return null;
	}
	
	public void addComputerLogins(Iterable<ComputerLoginEvent> events) {
	    // save all events in the 
	    for (ComputerLoginEvent event : events)
	    	addComputerLogin(event);
	}
	
	public void addComputerLogin(ComputerLoginEvent event) {
		checkNotNull(event);
		String ip = event.getIpaddress();
		checkNotNull(ip);
		
		// check if the event is in the cache, if not add it and save to repository
		ComputerLoginEvent cachedEvent =  cache.getIfPresent(ip);
		if (cachedEvent==null) {
			computerLoginEventRepository.save(event);
			cache.put(ip, event);
		} else {
			// if the event is in the cache, check if the new event has a different hostname and save it
			// if the event is in the cache and has the same hostname, update it only if the ticket expiration time passed
			if ((!event.getHostname().equals(cachedEvent.getHostname())) || (event.getTimestampepoch() > cachedEvent.getTimestampepoch() +  (leaseTimeInMins * 60 * 1000))) {
				computerLoginEventRepository.save(event);
				cache.put(ip, event);
			} else {
				logger.debug("skipping ip to hostname login event with hostname={}, ip={}, timestamp={}", event.getHostname(), event.getIpaddress(), event.getTimestampepoch());
			}
		}
	}

}
