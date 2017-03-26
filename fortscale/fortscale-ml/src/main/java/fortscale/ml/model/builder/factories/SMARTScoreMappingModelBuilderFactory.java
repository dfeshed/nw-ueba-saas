package fortscale.ml.model.builder.factories;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.SMARTScoreMappingModelBuilder;
import fortscale.ml.model.builder.SMARTScoreMappingModelBuilderConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class SMARTScoreMappingModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
	@Override
	public String getFactoryName() {
		return SMARTScoreMappingModelBuilderConf.SMART_SCORE_MAPPING_MODEL_BUILDER;
	}

	@Override
	public IModelBuilder getProduct(FactoryConfig factoryConfig) {
		SMARTScoreMappingModelBuilderConf config = (SMARTScoreMappingModelBuilderConf) factoryConfig;
		return new SMARTScoreMappingModelBuilder(config);
	}
}
