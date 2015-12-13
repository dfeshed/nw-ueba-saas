package fortscale.ml.model.retriever;

import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class ContextHistogramRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {
	@Override
	public String getFactoryName() {
		return ContextHistogramRetrieverConf.CONTEXT_HISTOGRAM_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		ContextHistogramRetrieverConf config = (ContextHistogramRetrieverConf)factoryConfig;
		return new ContextHistogramRetriever(config);
	}
}
