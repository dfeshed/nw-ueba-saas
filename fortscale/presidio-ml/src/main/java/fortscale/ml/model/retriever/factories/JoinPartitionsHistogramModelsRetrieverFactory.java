package fortscale.ml.model.retriever.factories;

import fortscale.ml.model.joiner.PartitionsDataModelJoiner;
import fortscale.ml.model.pagination.PriorModelPaginationService;
import fortscale.ml.model.retriever.*;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class JoinPartitionsHistogramModelsRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {

	@Autowired
	private ModelStore modelStore;


	@Override
	public String getFactoryName() {
		return JoinPartitionsHistogramModelsRetrieverConf.JOIN_PARTITIONS_HISTOGRAM_MODELS_RETRIEVER;
	}

	@Override
	public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
		JoinPartitionsHistogramModelsRetrieverConf config = (JoinPartitionsHistogramModelsRetrieverConf) factoryConfig;
		PartitionsDataModelJoiner partitionsDataModelJoiner = new PartitionsDataModelJoiner(config.getMinNumOfMaxValuesSamples(), config.getPartitionsResolutionInSeconds(), config.getResolutionStep(), config.getMinResolution());
		PriorModelPaginationService priorModelPaginationService = new PriorModelPaginationService(modelStore, config.getPageSize(), config.getMaxGroupSize());
		return new JoinPartitionsHistogramModelsRetriever(config, partitionsDataModelJoiner,  config.getNumOfMaxValuesSamples(), priorModelPaginationService, modelStore, factoryService);
	}



}
