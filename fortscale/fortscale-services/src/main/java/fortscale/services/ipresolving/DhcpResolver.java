package fortscale.services.ipresolving;

import java.util.List;

import fortscale.services.ipresolving.cache.ResolvingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import fortscale.domain.events.DhcpEvent;
import fortscale.domain.events.dao.DhcpEventRepository;
import fortscale.utils.TimestampUtils;


public class DhcpResolver {

	@Autowired
	private DhcpEventRepository dhcpEventRepository;
	
	@Value("${dhcp.resolver.leaseTimeInMins:1}")
	private int graceTimeInMins;
	
	@Autowired
	@Qualifier("dhcpResolverCache")
	private ResolvingCache<DhcpEvent> cache;
	
	public void setCache(ResolvingCache<DhcpEvent> cache) {
		this.cache = cache;
	}
	
	
	/**
	 * Handle the dhcp event and update repository when required.
	 * Dhcp event can contain assign, release to expired action codes. 
	 */
	public void addDhcpEvent(DhcpEvent event) {
		// add assigned events to repository
		if (DhcpEvent.ASSIGN_ACTION.equals(event.getAction())) {
			// see that we don't already have such an event in cache with the same 
			// expiration time and hostname
			DhcpEvent cached = cache.get(event.getIpaddress());
			if (cached!=null && cached.getHostname().equals(event.getHostname()) && cached.getExpiration()>= event.getExpiration())
				return;
			 
			
			// put in cache if the cache is empty
			if (cached==null) {
				cache.put(event.getIpaddress(), event);
				dhcpEventRepository.save(event);
			} else {
				// if we got assign to a new hostname update cache and mongo
				if (!cached.getHostname().equals(event.getHostname())) {
					// update cache only if it is not older event than the existing one
					if (cached.getTimestampepoch() < event.getTimestampepoch())
						cache.put(event.getIpaddress(), event);
					dhcpEventRepository.save(event);
				} else {
					// for the same hostname as cached event, check if we need to update the 
					// expiration date on the cached event
					if (event.getTimestampepoch() > cached.getTimestampepoch()) { 
						cached.setExpiration(event.getExpiration());
						cache.put(event.getIpaddress(), event);
						dhcpEventRepository.save(event);
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
			}
			
			// update saved event in repository as well
			DhcpEvent existing = dhcpEventRepository.findLatestEventForComputerBeforeTimestamp(event.getIpaddress(), event.getHostname(), event.getTimestampepoch());
			if (existing!=null && existing.getExpiration() > event.getTimestampepoch()) {
				// mark previous event as expired once the ip is released
				existing.setExpiration(event.getTimestampepoch());
				dhcpEventRepository.save(existing);
				
				// update cache
				if (cached==null)
					cache.put(existing.getIpaddress(), existing);
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
		DhcpEvent cached = cache.get(ip);
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
			if (saved.getExpiration() >= ts) {
				// also add the event to the cache for next time
				cache.put(ip, saved);
				return saved;
			}
		}
		
		return null;
	}
	
	public String getHostname(String ip, long ts) {
		DhcpEvent event = getLatestDhcpEventBeforeTimestamp(ip, ts);
		return (event!=null)? event.getHostname() : null;
	}
}
