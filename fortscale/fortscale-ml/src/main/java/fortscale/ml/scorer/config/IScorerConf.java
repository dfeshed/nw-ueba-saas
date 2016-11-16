package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.utils.factory.FactoryConfig;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ParetoScorerConf.class, name = ParetoScorerConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = FieldValueScoreReducerScorerConf.class, name = FieldValueScoreReducerScorerConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = LinearScoreReducerConf.class, name = LinearScoreReducerConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = LowValuesScoreReducerConf.class, name = LowValuesScoreReducerConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = ReductionScorerConf.class, name = ReductionScorerConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = CategoryRarityModelScorerConf.class, name = CategoryRarityModelScorerConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = RegexScorerConf.class, name = RegexScorerConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = ConstantRegexScorerConf.class, name = ConstantRegexScorerConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = MaxScorerContainerConf.class, name = MaxScorerContainerConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = PriorityScorerContainerConf.class, name = PriorityScorerContainerConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = ContinuousValuesModelScorerConf.class, name = ContinuousValuesModelScorerConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = TimeModelScorerConf.class, name = TimeModelScorerConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = SMARTValuesModelScorerConf.class, name = SMARTValuesModelScorerConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = ScoreMapperConf.class, name = ScoreMapperConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = ModelBasedScoreMapperConf.class, name = ModelBasedScoreMapperConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = ScoreAndCertaintyMultiplierScorerConf.class, name = ScoreAndCertaintyMultiplierScorerConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = GaussianModelScorerConf.class, name = GaussianModelScorerConf.SCORER_TYPE),
        @JsonSubTypes.Type(value = PersonalThresholdModelScorerConf.class, name = PersonalThresholdModelScorerConf.SCORER_TYPE)
})
public interface IScorerConf extends FactoryConfig {
    String getName();
}
