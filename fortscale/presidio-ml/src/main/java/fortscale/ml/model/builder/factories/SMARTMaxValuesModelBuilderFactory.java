package fortscale.ml.model.builder.factories;

import fortscale.ml.model.builder.*;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class SMARTMaxValuesModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
	private SMARTMaxValuesModelBuilder smartMaxValuesModelBuilder;

	@Override
	public String getFactoryName() {
		return SMARTMaxValuesModelBuilderConf.SMART_MAX_VALUES_MODEL_BUILDER;
	}

	@Override
	public IModelBuilder getProduct(FactoryConfig factoryConfig) {
		SMARTMaxValuesModelBuilderConf smartMaxValuesModelBuilderConf = (SMARTMaxValuesModelBuilderConf) factoryConfig;
		if (smartMaxValuesModelBuilder == null) {
			smartMaxValuesModelBuilder = new SMARTMaxValuesModelBuilder(smartMaxValuesModelBuilderConf);
		}

		return smartMaxValuesModelBuilder;
	}
}
