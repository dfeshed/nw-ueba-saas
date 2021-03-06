package fortscale.ml.model.builder.factories;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.PersonalThresholdModelBuilder;
import fortscale.ml.model.builder.PersonalThresholdModelBuilderConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class PersonalThresholdModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
	@Override
	public String getFactoryName() {
		return PersonalThresholdModelBuilderConf.PERSONAL_THRESHOLD_MODEL_BUILDER;
	}

	@Override
	public PersonalThresholdModelBuilder getProduct(FactoryConfig factoryConfig) {
		return new PersonalThresholdModelBuilder();
	}
}
