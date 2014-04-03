package fortscale.collection.morphlines.commands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import fortscale.domain.events.ComputerLoginEvent;
import fortscale.domain.events.dao.ComputerLoginEventRepository;


@Configurable(preConstruction=true)
public class ComputerLoginResolver {

	@Autowired
	private ComputerLoginEventRepository computerLoginEventRepository;
	
	private int leaseTimeInMins = 2880;
	private int graceTimeInMins = 1;
	
	
	public String getHostname(String ip, long ts) {
		String ret = null;
		if(computerLoginEventRepository == null){
			return null;
		}
		
		long upperLimitTs = (graceTimeInMins > 0)? ts + graceTimeInMins * 60 : ts;
		long lowerLimitTs = ts - leaseTimeInMins * 60;
		PageRequest pageRequest = new PageRequest(0, 1, Direction.DESC, ComputerLoginEvent.TIMESTAMP_EPOCH_FIELD_NAME);
		List<ComputerLoginEvent> computerLoginEvents = computerLoginEventRepository.findByIpaddressAndTimestampepochBetween(ip, lowerLimitTs, upperLimitTs, pageRequest);
		if(!computerLoginEvents.isEmpty()){
			ret = computerLoginEvents.get(0).getHostname();
		}
		
		return ret;
	}
}
