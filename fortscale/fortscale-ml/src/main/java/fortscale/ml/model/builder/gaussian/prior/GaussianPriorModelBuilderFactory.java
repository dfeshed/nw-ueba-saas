package fortscale.ml.model.builder.gaussian.prior;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class GaussianPriorModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
	@Override
	public String getFactoryName() {
		return GaussianPriorModelBuilderConf.GAUSSIAN_PRIOR_MODEL_BUILDER;
	}

	@Override
	public IModelBuilder getProduct(FactoryConfig factoryConfig) {
		GaussianPriorModelBuilderConf config = (GaussianPriorModelBuilderConf) factoryConfig;
		UniformSegmentCenters segmentsCenter = new UniformSegmentCenters(config.getDistanceBetweenSegmentsCenter());
		NeighboursSegmentor segmentor = new NeighboursSegmentor(
				config.getNumberOfNeighbours(),
				config.getMaxRatioBetweenSegmentSizeToCenter(),
				config.getMaxSegmentWidthToNotDiscardBecauseOfBadRatio(),
				config.getPadding()
		);
		PriorBuilderMaxAllowedValue gaussianPrior = new PriorBuilderMaxAllowedValue(config.getQuantile(), config.getMinMaxValue());
		return new GaussianPriorModelBuilder(
				segmentsCenter,
				segmentor,
				gaussianPrior,
				config.getMinNumOfSamplesToLearnFrom()
		);
	}
}
