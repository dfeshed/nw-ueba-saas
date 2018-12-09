package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.ml.model.builder.gaussian.ContinuousHistogramModelBuilderConf;
import fortscale.ml.model.builder.gaussian.ContinuousMaxHistogramModelBuilderConf;
import fortscale.ml.model.builder.gaussian.prior.GaussianPriorModelBuilderConf;
import fortscale.ml.model.builder.smart_weights.WeightsModelBuilderConf;
import fortscale.utils.factory.FactoryConfig;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ContinuousHistogramModelBuilderConf.class, name = ContinuousHistogramModelBuilderConf.CONTINUOUS_HISTOGRAM_MODEL_BUILDER),
		@JsonSubTypes.Type(value = ContinuousMaxHistogramModelBuilderConf.class, name = ContinuousMaxHistogramModelBuilderConf.CONTINUOUS_MAX_HISTOGRAM_MODEL_BUILDER),
		@JsonSubTypes.Type(value = CategoryRarityModelBuilderConf.class, name = CategoryRarityModelBuilderConf.CATEGORY_RARITY_MODEL_BUILDER),
		@JsonSubTypes.Type(value = TimeModelBuilderConf.class, name = TimeModelBuilderConf.TIME_MODEL_BUILDER),
		@JsonSubTypes.Type(value = SMARTValuesModelBuilderConf.class, name = SMARTValuesModelBuilderConf.SMART_VALUES_MODEL_BUILDER),
		@JsonSubTypes.Type(value = SMARTMaxValuesModelBuilderConf.class, name = SMARTMaxValuesModelBuilderConf.SMART_MAX_VALUES_MODEL_BUILDER),
		@JsonSubTypes.Type(value = SMARTValuesPriorModelBuilderConf.class, name = SMARTValuesPriorModelBuilderConf.SMART_VALUES_PRIOR_MODEL_BUILDER),
		@JsonSubTypes.Type(value = SMARTScoreMappingModelBuilderConf.class, name = SMARTScoreMappingModelBuilderConf.SMART_SCORE_MAPPING_MODEL_BUILDER),
		@JsonSubTypes.Type(value = IndicatorScoreMappingModelBuilderConf.class, name = IndicatorScoreMappingModelBuilderConf.INDICATOR_SCORE_MAPPING_MODEL_BUILDER),
		@JsonSubTypes.Type(value = GaussianPriorModelBuilderConf.class, name = GaussianPriorModelBuilderConf.GAUSSIAN_PRIOR_MODEL_BUILDER),
		@JsonSubTypes.Type(value = PersonalThresholdModelBuilderConf.class, name = PersonalThresholdModelBuilderConf.PERSONAL_THRESHOLD_MODEL_BUILDER),
		@JsonSubTypes.Type(value = WeightsModelBuilderConf.class,name = WeightsModelBuilderConf.WEIGHTS_MODEL_BUILDER),
		@JsonSubTypes.Type(value = PartitionsHistogramModelBuilderConf.class,name = PartitionsHistogramModelBuilderConf.INSTANT_TO_VALUE_HISTOGRAM_MODEL_BUILDER)
})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public interface IModelBuilderConf extends FactoryConfig {}
