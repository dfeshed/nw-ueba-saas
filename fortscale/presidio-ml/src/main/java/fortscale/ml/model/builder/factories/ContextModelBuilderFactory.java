package fortscale.ml.model.builder.factories;

import fortscale.ml.model.builder.*;
import fortscale.ml.model.metrics.CategoryRarityModelBuilderMetricsContainer;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class ContextModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {

    @Override
    public String getFactoryName() {
        return ContextModelBuilderConf.CONTEXT_MODEL_BUILDER;
    }

    @Override
    public IModelBuilder getProduct(FactoryConfig factoryConfig) {
        ContextModelBuilderConf config = (ContextModelBuilderConf) factoryConfig;
        return new ContextModelBuilder(config);
    }


}
