package fortscale.ml.model;

import fortscale.ml.model.listener.IModelBuildingListener;

public interface IModelBuildingRegistrar {
	public void process(IModelBuildingListener listener);
}
