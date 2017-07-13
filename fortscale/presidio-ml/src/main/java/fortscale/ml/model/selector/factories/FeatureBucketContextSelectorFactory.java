package fortscale.ml.model.selector.factories;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.ml.model.selector.FeatureBucketContextSelector;
import fortscale.ml.model.selector.FeatureBucketContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class FeatureBucketContextSelectorFactory extends AbstractServiceAutowiringFactory<IContextSelector> {
	@Autowired
	@Qualifier("modelBucketConfigService")
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private FeatureBucketReader featureBucketReader;

	@Override
	public String getFactoryName() {
		return FeatureBucketContextSelectorConf.FEATURE_BUCKET_CONTEXT_SELECTOR;
	}

	@Override
	public IContextSelector getProduct(FactoryConfig factoryConfig) {
		FeatureBucketContextSelectorConf conf = (FeatureBucketContextSelectorConf)factoryConfig;
		return new FeatureBucketContextSelector(conf, bucketConfigurationService, featureBucketReader);
	}
}
