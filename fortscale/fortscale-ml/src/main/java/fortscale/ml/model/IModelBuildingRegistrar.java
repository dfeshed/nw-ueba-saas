package fortscale.ml.model;

import fortscale.ml.model.listener.IModelBuildingListener;

public interface IModelBuildingRegistrar {
	void process(IModelBuildingListener listener);
}
