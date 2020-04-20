package fortscale.ml.model.retriever.factories;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.ml.model.retriever.*;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DistinctNumOfContextsRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private FeatureBucketReader featureBucketReader;

	@Override
	public String getFactoryName() {
		return DistinctNumOfContextsRetrieverConf.FACTORY_NAME;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		DistinctNumOfContextsRetrieverConf config = (DistinctNumOfContextsRetrieverConf)factoryConfig;
		return new DistinctNumOfContextsRetriever(config, bucketConfigurationService, featureBucketReader);
	}
}
