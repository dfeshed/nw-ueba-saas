package fortscale.ml.model.builder;

import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class IndicatorScoreMappingModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
	@Override
	public String getFactoryName() {
		return IndicatorScoreMappingModelBuilderConf.INDICATOR_SCORE_MAPPING_MODEL_BUILDER;
	}

	@Override
	public IModelBuilder getProduct(FactoryConfig factoryConfig) {
		IndicatorScoreMappingModelBuilderConf config = (IndicatorScoreMappingModelBuilderConf) factoryConfig;
		return new SMARTScoreMappingModelBuilder(config);
	}
}
