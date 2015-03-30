package fortscale.collection.jobs;

import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import fortscale.services.fe.Classifier;
import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;

@DisallowConcurrentExecution
public class AMTEventProcessJob extends EventProcessJob {

	protected MorphlinesItemsProcessor logicMorphline;

	@Override
	protected Classifier getClassifier(){
		return Classifier.amt;
	}

	@Override
	protected boolean isOnlyUpdateUser(Record record){
		// mark false to also create users, not just update existing
		return false;
	}



}
