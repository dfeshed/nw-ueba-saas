package fortscale.ml.model;

import fortscale.ml.model.listener.IModelBuildingListener;
import org.joda.time.DateTime;

public interface IModelBuildingRegistrar {
	void process(IModelBuildingListener listener, DateTime sessionStartTime, DateTime sessionEndTime);
}
