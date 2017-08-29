package fortscale.ml.model.retriever.factories;

import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AccumulatedSmartValueRetriever;
import fortscale.ml.model.retriever.AccumulatedSmartValueRetrieverConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.store.ModelStore;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataReader;

import java.time.Duration;

/**
 * Created by barak_schuster on 24/08/2017.
 */
@Component
public class AccumulatedSmartValueRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {

    @Autowired
    private SmartAccumulationDataReader accumulationDataReader;
    @Autowired
    private SmartRecordConfService smartRecordConfService;
    @Autowired
    private FactoryService<IContextSelector> contextSelectorFactoryService;
    @Autowired
    private ModelStore modelStore;
    @Value("#{T(java.time.Duration).parse('${fortscale.model.retriever.smart.oldestAllowedModelDurationDiff}')}")
    private Duration oldestAllowedModelDurationDiff;

    @Override
    public String getFactoryName() {
        return AccumulatedSmartValueRetrieverConf.ACCUMULATED_SMART_VALUE_RETRIEVER_FACTORY_NAME;
    }

    @Override
    public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
        AccumulatedSmartValueRetrieverConf config = (AccumulatedSmartValueRetrieverConf)factoryConfig;
        return new AccumulatedSmartValueRetriever(config, accumulationDataReader, smartRecordConfService, contextSelectorFactoryService, modelStore, oldestAllowedModelDurationDiff);
    }
}
