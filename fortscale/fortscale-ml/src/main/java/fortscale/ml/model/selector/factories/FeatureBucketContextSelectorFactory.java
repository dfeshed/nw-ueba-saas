package fortscale.ml.model.selector.factories;

import fortscale.ml.model.selector.FeatureBucketContextSelector;
import fortscale.ml.model.selector.FeatureBucketContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class FeatureBucketContextSelectorFactory extends AbstractServiceAutowiringFactory<IContextSelector> {
	@Override
	public String getFactoryName() {
		return FeatureBucketContextSelectorConf.FEATURE_BUCKET_CONTEXT_SELECTOR;
	}

	@Override
	public IContextSelector getProduct(FactoryConfig factoryConfig) {
		FeatureBucketContextSelectorConf config = (FeatureBucketContextSelectorConf)factoryConfig;
		return new FeatureBucketContextSelector(config);
	}
}
