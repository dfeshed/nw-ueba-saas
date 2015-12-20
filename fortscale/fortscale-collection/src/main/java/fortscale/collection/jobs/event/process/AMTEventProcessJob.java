package fortscale.collection.jobs.event.process;

import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import fortscale.collection.morphlines.RecordExtensions;
import fortscale.services.classifier.Classifier;
import org.kitesdk.morphline.api.Record;
import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Value;

@DisallowConcurrentExecution
public class AMTEventProcessJob extends EventProcessJob {

	protected MorphlinesItemsProcessor logicMorphline;
	@Value("${impala.data.amt.table.field.username}")
	private String userNameField;

	@Override
	protected Classifier getClassifier(){
		return Classifier.amt;
	}

	@Override
	protected boolean isOnlyUpdateUser(Record record){
		// mark false to also create users, not just update existing
		return false;
	}
	@Override
	protected String extractNormalizedUsernameFromRecord(Record record){
		return RecordExtensions.getStringValue(record,userNameField );
	}
}
