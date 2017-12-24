package fortscale.ml.model.builder.factories;

import fortscale.ml.model.builder.CategoryRarityModelBuilder;
import fortscale.ml.model.builder.CategoryRarityModelBuilderConf;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.metrics.CategoryRarityModelBuilderMetricsContainer;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class CategoryRarityModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
	@Override
	public String getFactoryName() {
		return CategoryRarityModelBuilderConf.CATEGORY_RARITY_MODEL_BUILDER;
	}

	@Autowired
	private CategoryRarityModelBuilderMetricsContainer categoryRarityModelBuilderMetricsContainer;


	@Override
	public IModelBuilder getProduct(FactoryConfig factoryConfig) {
		CategoryRarityModelBuilderConf config = (CategoryRarityModelBuilderConf) factoryConfig;
		return new CategoryRarityModelBuilder(config, categoryRarityModelBuilderMetricsContainer);
	}


}
