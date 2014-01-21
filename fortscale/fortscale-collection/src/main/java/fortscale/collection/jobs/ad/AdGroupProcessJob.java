package fortscale.collection.jobs.ad;

import java.util.Date;

import org.kitesdk.morphline.api.Record;

public class AdGroupProcessJob extends AdProcessJob {

	@Override
	protected boolean isTimestampAlreadyProcessed(Date runtime) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void updateDb(Record record) {
		// TODO Auto-generated method stub
		
	}

}
