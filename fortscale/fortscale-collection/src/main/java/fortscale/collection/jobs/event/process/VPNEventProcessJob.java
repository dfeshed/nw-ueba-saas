package fortscale.collection.jobs.event.process;

import fortscale.collection.morphlines.RecordExtensions;
import fortscale.services.fe.Classifier;
import org.kitesdk.morphline.api.Record;
import org.springframework.beans.factory.annotation.Value;

public class VPNEventProcessJob extends EventProcessJob {
		
	@Value("${impala.score.vpn.table.field.status}")
	private String statusFieldName;
	
	@Value("${vpn.status.success.value:SUCCESS}")
	private String vpnStatusSuccessValue;
	
	
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
