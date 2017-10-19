package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.IContinuousDataModel;

import java.util.Iterator;
import java.util.List;

/**
 * Full documentation can be found here: https://fortscale.atlassian.net/wiki/display/FSC/Gaussian+model
 */
public interface SegmentCenters {
	Iterator<Double> iterate(List<IContinuousDataModel> models);
}
