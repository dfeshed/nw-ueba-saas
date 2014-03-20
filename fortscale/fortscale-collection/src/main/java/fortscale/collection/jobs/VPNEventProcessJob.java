package fortscale.collection.jobs;

import org.kitesdk.morphline.api.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import fortscale.collection.morphlines.RecordExtensions;
import fortscale.services.fe.Classifier;
import fortscale.services.impl.UsernameNormalizer;

public class VPNEventProcessJob extends EventProcessJob {
	
	@Autowired
	UsernameNormalizer vpnUsernameNormalizer;
	
	@Value("${impala.vpn.table.field.status}")
	private String statusFieldName;
	
	@Value("${vpn.status.success.value:SUCCESS}")
	private String vpnStatusSuccessValue;
	
	@Override
	protected String normalizeUsername(Record record){
		String username = extractUsernameFromRecord(record);
		String ret = vpnUsernameNormalizer.normalize(username);
		if(ret == null){
			ret = username;
		}
		
		return ret;
	}
	
	@Override
	protected Classifier getClassifier(){
		return Classifier.vpn;
	}
	
	@Override
	protected boolean isOnlyUpdateUser(Record record){
		boolean ret = true;
		String status = RecordExtensions.getStringValue(record, statusFieldName);
		if(status != null && status.equalsIgnoreCase(vpnStatusSuccessValue)){
			ret = false;
		}
		
		return ret;
	}
}
