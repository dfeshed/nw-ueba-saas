package fortscale.ml.model.retriever.factories;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.ContextHistogramRetriever;
import fortscale.ml.model.retriever.ContextHistogramRetrieverConf;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class ContextHistogramRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
	@Autowired
	@Qualifier("modelBucketConfigService")
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private FeatureBucketReader featureBucketReader;

	@Override
	public String getFactoryName() {
		return ContextHistogramRetrieverConf.CONTEXT_HISTOGRAM_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		ContextHistogramRetrieverConf config = (ContextHistogramRetrieverConf)factoryConfig;
		return new ContextHistogramRetriever(config, bucketConfigurationService, featureBucketReader);
	}
}
