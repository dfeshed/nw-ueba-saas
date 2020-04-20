package fortscale.ml.model.retriever.factories.smart;

import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AccumulatedContextSmartValueRetriever;
import fortscale.ml.model.retriever.AccumulatedContextSmartValueRetrieverConf;
import fortscale.ml.model.store.ModelStore;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithm;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataReader;

import java.time.Duration;

@SuppressWarnings("unused")
@Component
public class AccumulatedContextSmartValueRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {

    @Autowired
    private SmartAccumulationDataReader accumulationDataReader;
    @Autowired
    private SmartRecordConfService smartRecordConfService;
    @Autowired
    private ModelStore modelStore;
    @Autowired
    private SmartWeightsScorerAlgorithm smartWeightsScorerAlgorithm;

    @Value("#{T(java.time.Duration).parse('${fortscale.model.retriever.smart.oldestAllowedModelDurationDiff}')}")
    private Duration oldestAllowedModelDurationDiff;


    @Override
    public String getFactoryName() {
        return AccumulatedContextSmartValueRetrieverConf.ACCUMULATED_CONTEXT_SMART_VALUE_RETRIEVER_FACTORY_NAME;
    }

    @Override
    public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
        AccumulatedContextSmartValueRetrieverConf config = (AccumulatedContextSmartValueRetrieverConf)factoryConfig;
        return new AccumulatedContextSmartValueRetriever(config, accumulationDataReader, smartRecordConfService, modelStore, oldestAllowedModelDurationDiff, smartWeightsScorerAlgorithm);
    }
}
