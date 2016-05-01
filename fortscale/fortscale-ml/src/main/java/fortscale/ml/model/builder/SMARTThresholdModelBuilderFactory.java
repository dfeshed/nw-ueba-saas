package fortscale.ml.model.builder;

import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class SMARTThresholdModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
	@Override
	public String getFactoryName() {
		return SMARTThresholdModelBuilderConf.SMART_THRESHOLD_MODEL_BUILDER;
	}

	@Override
	public IModelBuilder getProduct(FactoryConfig factoryConfig) {
		SMARTThresholdModelBuilderConf config = (SMARTThresholdModelBuilderConf) factoryConfig;
		return new SMARTThresholdModelBuilder(config);
	}
}
