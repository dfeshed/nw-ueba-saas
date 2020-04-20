package fortscale.ml.model.selector.factories;


import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.selector.ModelsSubContextSelector;
import fortscale.ml.model.selector.ModelsSubContextSelectorConf;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class ModelsSubContextSelectorFactory extends AbstractServiceAutowiringFactory<IContextSelector> {
    @Autowired
    private ModelStore modelStore;

    @Override
    public String getFactoryName() {
        return ModelsSubContextSelectorConf.MODELS_SUB_CONTEXT_SELECTOR;
    }

    @Override
    public IContextSelector getProduct(FactoryConfig factoryConfig) {
        ModelsSubContextSelectorConf conf = (ModelsSubContextSelectorConf)factoryConfig;
        return new ModelsSubContextSelector(modelStore,
                conf.getModelConfName(),
                conf.getContextFieldName());
    }
}
