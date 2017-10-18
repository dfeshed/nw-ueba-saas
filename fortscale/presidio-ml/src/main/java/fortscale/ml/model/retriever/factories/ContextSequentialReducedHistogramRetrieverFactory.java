package fortscale.ml.model.retriever.factories;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.ContextSequentialReducedHistogramRetriever;
import fortscale.ml.model.retriever.ContextSequentialReducedHistogramRetrieverConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContextSequentialReducedHistogramRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private FeatureBucketReader featureBucketReader;

	@Override
	public String getFactoryName() {
		return ContextSequentialReducedHistogramRetrieverConf.CONTEXT_HISTOGRAM_SEQUENTIAL_REDUCED_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		ContextSequentialReducedHistogramRetrieverConf config = (ContextSequentialReducedHistogramRetrieverConf)factoryConfig;
		return new ContextSequentialReducedHistogramRetriever(config, bucketConfigurationService, featureBucketReader);
	}
}
