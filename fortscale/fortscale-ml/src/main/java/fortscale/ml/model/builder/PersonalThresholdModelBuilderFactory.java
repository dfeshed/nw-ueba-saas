package fortscale.ml.model.builder;

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
		PersonalThresholdModelBuilderConf config = (PersonalThresholdModelBuilderConf) factoryConfig;
		return new PersonalThresholdModelBuilder(config.getDesiredNumOfIndicators());
	}
}
