package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.utils.factory.FactoryConfig;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ContinuousHistogramModelBuilderConf.class, name = ContinuousHistogramModelBuilderConf.CONTINUOUS_HISTOGRAM_MODEL_BUILDER),
		@JsonSubTypes.Type(value = CategoryRarityModelBuilderConf.class, name = CategoryRarityModelBuilderConf.CATEGORY_RARITY_MODEL_BUILDER),
		@JsonSubTypes.Type(value = CategoryRarityModelWithFeatureOccurrencesDataBuilderConf.class,
				name = CategoryRarityModelWithFeatureOccurrencesDataBuilderConf.CATEGORY_RARITY_MODEL_WITH_FEATURE_OCCURRENCES_DATA_BUILDER),
		@JsonSubTypes.Type(value = TimeModelBuilderConf.class, name = TimeModelBuilderConf.TIME_MODEL_BUILDER)
})
public interface IModelBuilderConf extends FactoryConfig {}
