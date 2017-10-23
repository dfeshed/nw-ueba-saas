package fortscale.ml.model.retriever.factories;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.CategoricalFeatureValueRetriever;
import fortscale.ml.model.retriever.CategoricalFeatureValueRetrieverConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoricalFeatureValueRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private FeatureBucketReader featureBucketReader;

	@Override
	public String getFactoryName() {
		return CategoricalFeatureValueRetrieverConf.FACTORY_NAME;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		CategoricalFeatureValueRetrieverConf config = (CategoricalFeatureValueRetrieverConf)factoryConfig;
		return new CategoricalFeatureValueRetriever(config, bucketConfigurationService, featureBucketReader);
	}
}
