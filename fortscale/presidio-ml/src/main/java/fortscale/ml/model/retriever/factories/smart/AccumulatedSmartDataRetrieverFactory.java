package fortscale.ml.model.retriever.factories.smart;

import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AccumulatedSmartDataRetriever;
import fortscale.ml.model.retriever.AccumulatedSmartDataRetrieverConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataReader;

/**
 * Created by barak_schuster on 30/08/2017.
 */
@Component
public class AccumulatedSmartDataRetrieverFactory extends AbstractServiceAutowiringFactory<AbstractDataRetriever> {

    @Autowired
    private FactoryService<IContextSelector> contextSelectorFactoryService;
    @Autowired
    private SmartAccumulationDataReader accumulationDataReader;
    @Autowired
    private SmartRecordConfService smartRecordConfService;

    @Override
    public String getFactoryName() {
        return AccumulatedSmartDataRetrieverConf.ACCUMULATED_SMART_DATA_RETRIEVER_FACTORY_NAME;
    }

    @Override
    public AbstractDataRetriever getProduct(FactoryConfig factoryConfig) {
        AccumulatedSmartDataRetrieverConf config = (AccumulatedSmartDataRetrieverConf) factoryConfig;
        return new AccumulatedSmartDataRetriever(config, contextSelectorFactoryService,accumulationDataReader,smartRecordConfService);
    }
}
