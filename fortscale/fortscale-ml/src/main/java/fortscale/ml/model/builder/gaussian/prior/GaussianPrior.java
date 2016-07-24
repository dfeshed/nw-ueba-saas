package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.ContinuousDataModel;

import java.util.List;

public interface GaussianPrior {
	Double calcPrior(List<ContinuousDataModel> models, double mean);
}
