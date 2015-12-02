package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.ml.model.prevalance.field.ContinuousDataModel;
import fortscale.ml.model.prevalance.field.DiscreteDataModel;
import fortscale.ml.model.prevalance.field.TimeModel;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ContinuousDataModel.class, name = ContinuousDataModel.MODEL_TYPE),
		@JsonSubTypes.Type(value = DiscreteDataModel.class, name = DiscreteDataModel.MODEL_TYPE),
		@JsonSubTypes.Type(value = TimeModel.class, name = TimeModel.MODEL_TYPE)
})
public interface Model extends Serializable {
	/**
	 * Scores a given value according to the model.
	 *
	 * @param value the value to score.
	 * @return the score.
	 */
	double calculateScore(Object value);
}
