package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class ContinuousHistogramModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
	private ContinuousHistogramModelBuilder continuousHistogramModelBuilder;

	@Override
	public String getFactoryName() {
		return ContinuousHistogramModelBuilderConf.CONTINUOUS_HISTOGRAM_MODEL_BUILDER;
	}

	@Override
	public IModelBuilder getProduct(FactoryConfig factoryConfig) {
		if (continuousHistogramModelBuilder == null) {
			continuousHistogramModelBuilder = new ContinuousHistogramModelBuilder();
		}

		return continuousHistogramModelBuilder;
	}
}
