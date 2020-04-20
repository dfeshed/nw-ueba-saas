package fortscale.ml.model.retriever.factories;

import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.ModelRetriever;
import fortscale.ml.model.retriever.ModelRetrieverConf;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class ModelRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {

	@Autowired
	private ModelStore modelStore;

	@Override
	public String getFactoryName() {
		return ModelRetrieverConf.MODEL_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		return new ModelRetriever((ModelRetrieverConf) factoryConfig, modelStore);
	}
}
