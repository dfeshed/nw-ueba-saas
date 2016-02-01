package fortscale.ml.model.listener;

import java.util.Date;

public interface IModelBuildingListener {
	public void modelBuildingStatus(String modelConfName, String sessionId, String contextId, Date endTime, ModelBuildingStatus status);
}
