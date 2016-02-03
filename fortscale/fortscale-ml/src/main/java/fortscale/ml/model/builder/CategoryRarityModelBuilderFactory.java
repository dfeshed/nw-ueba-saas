package fortscale.ml.model.builder;

import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class CategoryRarityModelBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
	private CategoryRarityModelBuilder categoryRarityModelBuilder;

	@Override
	public String getFactoryName() {
		return CategoryRarityModelBuilderConf.CATEGORY_RARITY_MODEL_BUILDER;
	}

	@Override
	public IModelBuilder getProduct(FactoryConfig factoryConfig) {
		if (categoryRarityModelBuilder == null) {
			categoryRarityModelBuilder = new CategoryRarityModelBuilder();
		}

		return categoryRarityModelBuilder;
	}
}
