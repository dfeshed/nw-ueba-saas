package fortscale.ml.model.builder.factories;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.SMARTValuesModelBuilder;
import fortscale.ml.model.builder.SMARTValuesModelBuilderConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class SMARTValuesModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
	private SMARTValuesModelBuilder smartValuesModelBuilder;

	@Override
	public String getFactoryName() {
		return SMARTValuesModelBuilderConf.SMART_VALUES_MODEL_BUILDER;
	}

	@Override
	public IModelBuilder getProduct(FactoryConfig factoryConfig) {
		if (smartValuesModelBuilder == null) {
			smartValuesModelBuilder = new SMARTValuesModelBuilder();
		}

		return smartValuesModelBuilder;
	}
}
