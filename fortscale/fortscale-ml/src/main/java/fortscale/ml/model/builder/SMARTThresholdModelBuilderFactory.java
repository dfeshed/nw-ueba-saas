package fortscale.ml.model.builder;

import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class SMARTThresholdModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
	private SMARTThresholdModelBuilder smartThresholdModelBuilder;

	@Override
	public String getFactoryName() {
		return SMARTThresholdModelBuilderConf.SMART_THRESHOLD_MODEL_BUILDER;
	}

	@Override
	public IModelBuilder getProduct(FactoryConfig factoryConfig) {
		if (smartThresholdModelBuilder == null) {
			smartThresholdModelBuilder = new SMARTThresholdModelBuilder();
		}

		return smartThresholdModelBuilder;
	}
}
