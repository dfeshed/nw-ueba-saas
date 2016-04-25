package fortscale.ml.model.listener;

import java.util.Date;

public interface IModelBuildingListener {
	void modelBuildingStatus(String modelConfName, String sessionId, String contextId, Date endTime, ModelBuildingStatus status);
	void modelBuildingSummary(String modelConfName, String sessionId, Date endTime, long numOfSuccesses, long numOfFailures);
}
