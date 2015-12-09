package fortscale.ml.model.selector;

import fortscale.utils.factory.Factory;
import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class FeatureBucketContextSelectorFactory implements InitializingBean, Factory<IContextSelector> {
	@Autowired
	private FactoryService<IContextSelector> contextSelectorFactoryService;

	@Override
	public void afterPropertiesSet() throws Exception {
		contextSelectorFactoryService.register(FeatureBucketContextSelectorConf.FEATURE_BUCKET_CONTEXT_SELECTOR, this);
	}

	@Override
	public IContextSelector getProduct(FactoryConfig factoryConfig) {
		FeatureBucketContextSelectorConf config = (FeatureBucketContextSelectorConf)factoryConfig;
		return new FeatureBucketContextSelector(config);
	}
}
