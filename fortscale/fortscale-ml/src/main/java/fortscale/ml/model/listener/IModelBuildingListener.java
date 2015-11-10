package fortscale.ml.model.listener;

public interface IModelBuildingListener {
	public void modelBuildingStatus(String modelConfName, String contextId, boolean success);
}
