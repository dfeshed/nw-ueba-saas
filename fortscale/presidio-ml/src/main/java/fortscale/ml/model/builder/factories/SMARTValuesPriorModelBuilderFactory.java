package fortscale.ml.model.builder.factories;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.SMARTValuesPriorModelBuilder;
import fortscale.ml.model.builder.SMARTValuesPriorModelBuilderConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class SMARTValuesPriorModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
	@Override
	public String getFactoryName() {
		return SMARTValuesPriorModelBuilderConf.SMART_VALUES_PRIOR_MODEL_BUILDER;
	}

	@Override
	public IModelBuilder getProduct(FactoryConfig factoryConfig) {
		return new SMARTValuesPriorModelBuilder((SMARTValuesPriorModelBuilderConf) factoryConfig);
	}
}
