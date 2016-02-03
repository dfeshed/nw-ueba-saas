package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoryRarityModelWithFeatureOccurrencesDataBuilderConf extends CategoryRarityModelBuilderConf {
	public static final String CATEGORY_RARITY_MODEL_WITH_FEATURE_OCCURRENCES_DATA_BUILDER =
			"category_rarity_model_with_feature_occurrences_data_builder";

	@JsonCreator
	public CategoryRarityModelWithFeatureOccurrencesDataBuilderConf(@JsonProperty("numOfBuckets") int numOfBuckets) {
		super(numOfBuckets);
	}

	@Override
	public String getFactoryName() {
		return CATEGORY_RARITY_MODEL_WITH_FEATURE_OCCURRENCES_DATA_BUILDER;
	}
}
