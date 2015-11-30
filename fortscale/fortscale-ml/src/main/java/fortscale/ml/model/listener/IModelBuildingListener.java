package fortscale.ml.model.listener;

import java.util.Date;

public interface IModelBuildingListener {
	public void modelBuildingStatus(String modelConfName, String contextId, Date endTime, boolean success);
}
