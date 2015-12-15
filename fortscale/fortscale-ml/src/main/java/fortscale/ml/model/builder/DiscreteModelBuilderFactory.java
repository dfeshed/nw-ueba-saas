package fortscale.ml.model.builder;

import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class DiscreteModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
	private DiscreteModelBuilder discreteModelBuilder;

	@Override
	public String getFactoryName() {
		return DiscreteModelBuilderConf.DISCRETE_MODEL_BUILDER;
	}

	@Override
	public IModelBuilder getProduct(FactoryConfig factoryConfig) {
		if (discreteModelBuilder == null) {
			discreteModelBuilder = new DiscreteModelBuilder();
		}

		return discreteModelBuilder;
	}
}
