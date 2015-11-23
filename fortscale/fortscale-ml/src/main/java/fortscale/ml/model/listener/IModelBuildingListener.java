package fortscale.ml.model.listener;

import org.joda.time.DateTime;

public interface IModelBuildingListener {
	public void modelBuildingStatus(String modelConfName, String contextId, DateTime endTime, boolean success);
}
