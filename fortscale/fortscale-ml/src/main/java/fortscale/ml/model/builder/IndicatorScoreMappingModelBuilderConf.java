package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonCreator;


public class IndicatorScoreMappingModelBuilderConf extends SMARTScoreMappingModelBuilderConf {
	public static final String INDICATOR_SCORE_MAPPING_MODEL_BUILDER = "indicator_score_mapping_model_builder";

	@JsonCreator
	public IndicatorScoreMappingModelBuilderConf() {
		super(50, 100);
	}

	@Override
	public String getFactoryName() {
		return INDICATOR_SCORE_MAPPING_MODEL_BUILDER;
	}
}
