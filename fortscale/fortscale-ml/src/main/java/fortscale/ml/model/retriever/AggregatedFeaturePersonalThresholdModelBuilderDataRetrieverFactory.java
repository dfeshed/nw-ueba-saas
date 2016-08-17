package fortscale.ml.model.retriever;

import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class AggregatedFeaturePersonalThresholdModelBuilderDataRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
	@Override
	public String getFactoryName() {
		return AggregatedFeaturePersonalThresholdModelBuilderDataRetrieverConf.AGGREGATED_FEATURE_PERSONAL_THRESHOLD_MODEL_BUILDER_DATA_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		return new AggregatedFeaturePersonalThresholdModelBuilderDataRetriever((AggregatedFeaturePersonalThresholdModelBuilderDataRetrieverConf) factoryConfig);
	}
}
