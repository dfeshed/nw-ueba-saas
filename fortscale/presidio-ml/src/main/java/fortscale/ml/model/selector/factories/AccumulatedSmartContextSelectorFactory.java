package fortscale.ml.model.selector.factories;

import fortscale.ml.model.selector.AccumulatedSmartContextSelector;
import fortscale.ml.model.selector.AccumulatedSmartContextSelectorConf;
import fortscale.ml.model.selector.IContextSelector;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataReader;

import static fortscale.ml.model.selector.AccumulatedSmartContextSelectorConf.ACCUMULATED_SMART_CONTEXT_SELECTOR_FACTORY_NAME;

/**
 * Created by barak_schuster on 24/08/2017.
 */
@Component
public class AccumulatedSmartContextSelectorFactory extends AbstractServiceAutowiringFactory<IContextSelector> {
    @Override
    public String getFactoryName() {
        return ACCUMULATED_SMART_CONTEXT_SELECTOR_FACTORY_NAME;
    }
    @Autowired
    private SmartAccumulationDataReader smartAccumulationDataReader;

    @Override
    public IContextSelector getProduct(FactoryConfig factoryConfig) {
        return new AccumulatedSmartContextSelector((AccumulatedSmartContextSelectorConf)factoryConfig, smartAccumulationDataReader);
    }
}
