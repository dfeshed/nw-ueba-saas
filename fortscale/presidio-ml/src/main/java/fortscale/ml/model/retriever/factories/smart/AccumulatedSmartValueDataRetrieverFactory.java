package fortscale.ml.model.retriever.factories.smart;

import fortscale.ml.model.retriever.*;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.store.ModelStore;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithm;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataReader;

import java.time.Duration;


@Component
public class AccumulatedSmartValueDataRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {

    @Autowired
    private SmartAccumulationDataReader accumulationDataReader;
    @Autowired
    private SmartRecordConfService smartRecordConfService;
    @Autowired
    private FactoryService<IContextSelector> contextSelectorFactoryService;
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
        return new AccumulatedContextSmartValueRetriever(config, accumulationDataReader, smartRecordConfService, contextSelectorFactoryService, modelStore, oldestAllowedModelDurationDiff, smartWeightsScorerAlgorithm);
    }
}
