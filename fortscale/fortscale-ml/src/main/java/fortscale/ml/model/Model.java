package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.ml.model.prevalance.field.ContinuousDataModel;
import fortscale.ml.model.prevalance.field.CategoryRarityModel;
import fortscale.ml.model.prevalance.field.TimeModel;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ContinuousDataModel.class, name = ContinuousDataModel.MODEL_TYPE),
		@JsonSubTypes.Type(value = CategoryRarityModel.class, name = CategoryRarityModel.MODEL_TYPE),
		@JsonSubTypes.Type(value = TimeModel.class, name = TimeModel.MODEL_TYPE)
})
public interface Model extends Serializable {
	/**
	 * Scores a given value according to the model.
	 *
	 * @param value the value to score.
	 * @return the score, or null if unable to give a score (e.g. - not enough data was given in build phase).
	 */
	Double calculateScore(Object value);
}
