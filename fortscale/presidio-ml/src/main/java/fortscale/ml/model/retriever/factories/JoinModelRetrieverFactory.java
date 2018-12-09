package fortscale.ml.model.retriever.factories;

import fortscale.ml.model.joiner.MultiContextContinuousModelJoiner;
import fortscale.ml.model.pagination.PriorModelPaginationService;
import fortscale.ml.model.retriever.*;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class JoinModelRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {

	@Autowired
	private ModelStore modelStore;
	@Autowired
	private PriorModelPaginationService modelPaginationService;

	@Override
	public String getFactoryName() {
		return JoinModelRetrieverConf.JOIN_MODEL_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		JoinModelRetrieverConf config = (JoinModelRetrieverConf) factoryConfig;

		MultiContextContinuousModelJoiner multiContextModelJoiner = new MultiContextContinuousModelJoiner(config.getMinNumOfMaxValuesSamples(),
				config.getPartitionsResolutionInSeconds(), config.getResolutionStep(), config.getNumOfMaxValuesSamples());
		return new JoinModelRetriever((JoinModelRetrieverConf) factoryConfig, multiContextModelJoiner, modelPaginationService, modelStore);
	}
}
