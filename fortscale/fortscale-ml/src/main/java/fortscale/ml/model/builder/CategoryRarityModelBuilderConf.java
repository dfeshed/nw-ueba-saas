package fortscale.ml.model.builder;

public class CategoryRarityModelBuilderConf implements IModelBuilderConf {
	public static final String CATEGORY_RARITY_MODEL_BUILDER = "category_rarity_model_builder";

	@Override
	public String getFactoryName() {
		return CATEGORY_RARITY_MODEL_BUILDER;
	}
}
