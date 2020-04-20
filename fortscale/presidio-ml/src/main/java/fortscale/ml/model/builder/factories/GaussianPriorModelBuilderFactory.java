package fortscale.ml.model.builder.factories;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.gaussian.prior.*;
import fortscale.ml.model.metrics.GaussianPriorModelBuilderMetricsContainer;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class GaussianPriorModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {

	@Autowired
	private GaussianPriorModelBuilderMetricsContainer gaussianPriorModelBuilderMetricsContainer;

	@Override
	public String getFactoryName() {
		return GaussianPriorModelBuilderConf.GAUSSIAN_PRIOR_MODEL_BUILDER;
	}

	@Override
	public IModelBuilder getProduct(FactoryConfig factoryConfig) {
		GaussianPriorModelBuilderConf config = (GaussianPriorModelBuilderConf) factoryConfig;
		UniformSegmentCenters segmentCenters = new UniformSegmentCenters(config.getDistanceBetweenSegmentCenters());
		NeighboursSegmentor segmentor = new NeighboursSegmentor(
				config.getNumberOfNeighbours(),
				config.getMaxRatioBetweenSegmentSizeToCenter(),
				config.getMaxSegmentWidthToNotDiscardBecauseOfBadRatio(),
				config.getPadding()
		);
		PriorBuilderMaxAllowedValue priorBuilder = new PriorBuilderMaxAllowedValue(
				config.getQuantile(),
				config.getMinQuantileComplementSize(),
				config.getMinAllowedDistFromMean()
		);
		return new GaussianPriorModelBuilder(
				segmentCenters,
				segmentor,
				priorBuilder,
				config.getMinNumOfSamplesToLearnFrom(),
				gaussianPriorModelBuilderMetricsContainer
		);
	}
}
