package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.IContinuousDataModel;

import java.util.List;

/**
 * Full documentation can be found here: https://fortscale.atlassian.net/wiki/display/FSC/Gaussian+model
 */
public interface PriorBuilder {
	Double calcPrior(List<IContinuousDataModel> models, double mean);
	Double getMinAllowedPrior();
}
