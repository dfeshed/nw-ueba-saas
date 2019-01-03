package fortscale.ml.model.builder.factories;

import fortscale.ml.model.builder.CategoryRarityGlobalModelBuilder;
import fortscale.ml.model.builder.CategoryRarityGlobalModelBuilderConf;
import fortscale.ml.model.builder.CategoryRarityModelBuilderConf;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;


@SuppressWarnings("unused")
@Component
public class CategoryRarityModelGlobalBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {

    @Override
    public String getFactoryName() {
        return CategoryRarityGlobalModelBuilderConf.CATEGORY_RARITY_GLOBAL_MODEL_BUILDER;
    }


    @Override
    public IModelBuilder getProduct(FactoryConfig factoryConfig) {
        CategoryRarityGlobalModelBuilderConf config = (CategoryRarityGlobalModelBuilderConf) factoryConfig;
        return new CategoryRarityGlobalModelBuilder(config.getMinNumOfPartitionsToLearnFrom());
    }
}