package fortscale.ml.model.retriever.factories;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.ContextHistogramRetriever;
import fortscale.ml.model.retriever.ContextHistogramRetrieverConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class ContextHistogramRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private FeatureBucketsReaderService featureBucketsReaderService;

	@Override
	public String getFactoryName() {
		return ContextHistogramRetrieverConf.CONTEXT_HISTOGRAM_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		ContextHistogramRetrieverConf config = (ContextHistogramRetrieverConf)factoryConfig;
		return new ContextHistogramRetriever(config,bucketConfigurationService,featureBucketsReaderService);
	}
}
