package fortscale.ml.model.listener;

import java.util.Map;

public interface IModelBuildingListener {
	public void modelBuildingStatus(String modelConfName, Map<String, String> context, boolean success);
}
