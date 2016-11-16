package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;

import java.util.Iterator;
import java.util.List;

public interface SegmentCenters {
	Iterator<Double> iterate(List<ContinuousDataModel> models);
}
