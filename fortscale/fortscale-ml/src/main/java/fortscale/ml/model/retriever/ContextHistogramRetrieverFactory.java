package fortscale.ml.model.retriever;

import fortscale.utils.factory.Factory;
import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class ContextHistogramRetrieverFactory implements InitializingBean, Factory<AbstractDataRetriever> {
	@Autowired
	private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;

	@Override
	public void afterPropertiesSet() throws Exception {
		dataRetrieverFactoryService.register(ContextHistogramRetrieverConf.CONTEXT_HISTOGRAM_RETRIEVER, this);
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		ContextHistogramRetrieverConf config = (ContextHistogramRetrieverConf)factoryConfig;
		return new ContextHistogramRetriever(config);
	}
}
