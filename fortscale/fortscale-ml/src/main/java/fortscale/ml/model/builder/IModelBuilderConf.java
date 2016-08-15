package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.ml.model.builder.gaussian.ContinuousHistogramModelBuilderConf;
import fortscale.ml.model.builder.gaussian.prior.GaussianPriorModelBuilderConf;
import fortscale.utils.factory.FactoryConfig;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ContinuousHistogramModelBuilderConf.class, name = ContinuousHistogramModelBuilderConf.CONTINUOUS_HISTOGRAM_MODEL_BUILDER),
		@JsonSubTypes.Type(value = CategoryRarityModelBuilderConf.class, name = CategoryRarityModelBuilderConf.CATEGORY_RARITY_MODEL_BUILDER),
		@JsonSubTypes.Type(value = TimeModelBuilderConf.class, name = TimeModelBuilderConf.TIME_MODEL_BUILDER),
		@JsonSubTypes.Type(value = SMARTValuesModelBuilderConf.class, name = SMARTValuesModelBuilderConf.SMART_VALUES_MODEL_BUILDER),
		@JsonSubTypes.Type(value = SMARTScoreMappingModelBuilderConf.class, name = SMARTScoreMappingModelBuilderConf.SMART_SCORE_MAPPING_MODEL_BUILDER),
		@JsonSubTypes.Type(value = GaussianPriorModelBuilderConf.class, name = GaussianPriorModelBuilderConf.GAUSSIAN_PRIOR_MODEL_BUILDER),
		@JsonSubTypes.Type(value = PersonalThresholdModelBuilderConf.class, name = PersonalThresholdModelBuilderConf.PERSONAL_THRESHOLD_MODEL_BUILDER)
})
public interface IModelBuilderConf extends FactoryConfig {}
