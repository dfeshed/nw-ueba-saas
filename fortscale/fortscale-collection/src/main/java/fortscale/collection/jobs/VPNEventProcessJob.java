package fortscale.collection.jobs;

import org.kitesdk.morphline.api.Record;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.services.impl.UsernameNormalizer;

public class VPNEventProcessJob extends EventProcessJob {
	
	@Autowired
	UsernameNormalizer vpnUsernameNormalizer;
	
	@Override
	protected String normalizeUsername(Record record){
		String username = extractUsernameFromRecord(record);
		return vpnUsernameNormalizer.normalize(username);
	}
}
