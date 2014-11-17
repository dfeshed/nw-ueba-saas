package fortscale.services.ipresolving;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import fortscale.domain.events.DhcpEvent;
import fortscale.domain.events.dao.DhcpEventRepository;
import fortscale.utils.TimestampUtils;


@Service("dhcpResolver")
public class DhcpResolver implements InitializingBean {

	@Autowired
	private DhcpEventRepository dhcpEventRepository;
	
	@Value("${dhcp.resolver.leaseTimeInMins:1}")
	private int graceTimeInMins;
	
	@Value("${dhcp.resolver.cache.max.items:30000}") 
	private int cacheMaxSize;
	
	private Cache<String, DhcpEvent> cache;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// create a cache of ip login events
		cache = CacheBuilder.newBuilder().maximumSize(cacheMaxSize).build();
	}
	
	
	public void setCache(Cache<String, DhcpEvent> cache) {
		this.cache = cache;
	}
	
	
	/**
	 * Handle the dhcp event and update repository when required.
	 * Dhcp event can contain assign, release to expired action codes. 
	 */
	public void addDhcpEvent(DhcpEvent event) {
		// add assigned events to repository
		if (DhcpEvent.ASSIGN_ACTION.equals(event.getAction())) {
			dhcpEventRepository.save(event);
			
			// put in cache if the cache is empty or older than the new item
			DhcpEvent cached = cache.getIfPresent(event.getIpaddress());
			if (cached==null || cached.getTimestampepoch() < event.getTimestampepoch())
				cache.put(event.getIpaddress(), event);
		}
		// end previous assignment in case of expiration or release
		if (DhcpEvent.RELEASE_ACTION.equals(event.getAction()) || DhcpEvent.EXPIRED_ACTION.equals(event.getAction())) {
			// check if we have an existing dhcp event than need to be updated with expiration time
			DhcpEvent cached = cache.getIfPresent(event.getIpaddress());
			if (cached!=null && cached.getHostname().equals(event.getHostname()) && cached.getExpiration() > event.getTimestampepoch()) {
				cached.setExpiration(event.getExpiration());
				cache.put(cached.getIpaddress(), cached);
			}
			
			// update saved event in repository as well
			DhcpEvent existing = dhcpEventRepository.findLatestEventForComputerBeforeTimestamp(event.getIpaddress(), event.getHostname(), event.getTimestampepoch());
			if (existing!=null && existing.getExpiration() > event.getTimestampepoch()) {
				// mark previous event as expired once the ip is released
				existing.setExpiration(event.getTimestampepoch());
				dhcpEventRepository.save(existing);
			}
		}
	}
	
	public DhcpEvent getLatestDhcpEventBeforeTimestamp(String ip, long ts) {
		if(dhcpEventRepository == null){
			return null;
		}
		
		ts = TimestampUtils.convertToMilliSeconds(ts);
		long upperTsLimit = (graceTimeInMins > 0)? ts + graceTimeInMins * 60 * 1000 : ts;
		
		// see if we have a matching event in cache
		DhcpEvent cached = cache.getIfPresent(ip);
		if (cached!=null && cached.getTimestampepoch()<=upperTsLimit && cached.getExpiration() >= ts) {
			// return cached event
			return cached;
		}
		
		// if the event was not in cache than look for it in the repository
		List<DhcpEvent> dhcpEvents = dhcpEventRepository.findByIpaddressAndTimestampepochLessThan(ip, upperTsLimit,
				new PageRequest(0, 1, Direction.DESC, DhcpEvent.TIMESTAMP_EPOCH_FIELD_NAME));
		if(!dhcpEvents.isEmpty()){
			// check if the ip assignment is not expired
			DhcpEvent saved = dhcpEvents.get(0);
			if (saved.getExpiration() >= ts)
				return saved;
		}
		
		return null;
	}
	
	public String getHostname(String ip, long ts) {
		DhcpEvent event = getLatestDhcpEventBeforeTimestamp(ip, ts);
		return (event!=null)? event.getHostname() : null;
	}
}
