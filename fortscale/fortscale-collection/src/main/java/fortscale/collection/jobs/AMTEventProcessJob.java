package fortscale.collection.jobs;

import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import fortscale.collection.morphlines.RecordExtensions;
import fortscale.services.fe.Classifier;
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

	@Override
	protected void updateOrCreateUserWithClassifierUsername(Record record){
		Classifier classifier = getClassifier();
		if(classifier != null){
			String normalizedUsername = extractNormalizedUsernameFromRecord(record);
			String logUsername = extractUsernameFromRecord(record);
			userService.updateOrCreateUserWithClassifierUsername(classifier, normalizedUsername, logUsername, isOnlyUpdateUser(record), isUpdateAppUsername());
		}
	}



}
