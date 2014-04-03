package fortscale.collection.morphlines.commands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import fortscale.domain.events.DhcpEvent;
import fortscale.domain.events.dao.DhcpEventRepository;


@Configurable(preConstruction=true)
public class DhcpResolver {

	@Autowired
	private DhcpEventRepository dhcpEventRepository;
	
	private int leaseTimeInMins = 2880;
	private int graceTimeInMins = 1;
	
	
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
			ret = dhcpEvents.get(0).getHostname();
		}
		
		return ret;
	}
}
