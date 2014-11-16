package fortscale.services.ipresolving;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import fortscale.domain.events.DhcpEvent;
import fortscale.domain.events.dao.DhcpEventRepository;


@Service("dhcpResolver")
public class DhcpResolver {

	@Autowired
	private DhcpEventRepository dhcpEventRepository;
	
	@Value("${dhcp.resolver.leaseTimeInMins:2880}")
	private int leaseTimeInMins;
	@Value("${dhcp.resolver.leaseTimeInMins:1}")
	private int graceTimeInMins;
	
	/**
	 * Handle the dhcp event and update repository when required.
	 * Dhcp event can contain assign, release to expired action codes. 
	 */
	public void addDhcpEvent(DhcpEvent event) {
		// add assigned events to repository
		if (DhcpEvent.ASSIGN_ACTION.equals(event.getAction())) {
			dhcpEventRepository.save(event);
		}
		// end previous assignment in case of expiration or release
		if (DhcpEvent.RELEASE_ACTION.equals(event.getAction()) || DhcpEvent.EXPIRED_ACTION.equals(event.getAction())) {
			DhcpEvent existing = dhcpEventRepository.findLatestEventForComputerBeforeTimestamp(event.getIpaddress(), event.getHostname(), event.getTimestampepoch());
			if (existing!=null) {
				// mark previous event as expired once the ip is released
				existing.setExpiration(event.getTimestampepoch());
				dhcpEventRepository.save(existing);
			}
		}
	}
	
	public String getHostname(String ip, long ts) {
		String ret = null;
		if(dhcpEventRepository == null){
			return null;
		}
		
		long upperLimitTs = (graceTimeInMins > 0)? ts + graceTimeInMins * 60 : ts;
		long lowerLimitTs = ts - leaseTimeInMins * 60;
		PageRequest pageRequest = new PageRequest(0, 1, Direction.DESC, DhcpEvent.TIMESTAMP_EPOCH_FIELD_NAME);
		List<DhcpEvent> dhcpEvents = dhcpEventRepository.findByIpaddressAndTimestampepochBetween(ip, lowerLimitTs, upperLimitTs, pageRequest);
		if(!dhcpEvents.isEmpty()){
			// check if the ip assignment is not expired
			DhcpEvent assignment = dhcpEvents.get(0);
			if (assignment.getExpiration() >= ts)
				ret = dhcpEvents.get(0).getHostname();
		}
		
		return ret;
	}
}
