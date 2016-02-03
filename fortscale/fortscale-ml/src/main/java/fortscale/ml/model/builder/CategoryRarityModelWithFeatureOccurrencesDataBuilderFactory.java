package fortscale.ml.model.builder;

import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class CategoryRarityModelWithFeatureOccurrencesDataBuilderFactory extends AbstractServiceAutowiringFactory<IModelBuilder> {
	private CategoryRarityModelWithFeatureOccurrencesDataBuilder categoryRarityModelWithFeatureOccurrencesDataBuilder;

	@Override
	public String getFactoryName() {
		return CategoryRarityModelWithFeatureOccurrencesDataBuilderConf.CATEGORY_RARITY_MODEL_WITH_FEATURE_OCCURRENCES_DATA_BUILDER;
	}

	@Override
	public IModelBuilder getProduct(FactoryConfig factoryConfig) {
		if (categoryRarityModelWithFeatureOccurrencesDataBuilder == null) {
			categoryRarityModelWithFeatureOccurrencesDataBuilder = new CategoryRarityModelWithFeatureOccurrencesDataBuilder();
		}

		return categoryRarityModelWithFeatureOccurrencesDataBuilder;
	}
}
