package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@Type(value = CategoryRarityModel.class, name = "category-rarity-model"),
		@Type(value = ContinuousDataModel.class, name = "continuous-data-model"),
		@Type(value = TimeModel.class, name = "time-model"),
		@Type(value = SMARTValuesModel.class, name = "smart-values-model"),
		@Type(value = ScoreMappingModel.class, name = "score-mapping-model"),
		@Type(value = SMARTScoreMappingModel.class, name = "smart-score-mapping-model")
})
public interface Model {
	/**
	 * @return the number of samples from which this model was built.
	 */
	long getNumOfSamples();
}
