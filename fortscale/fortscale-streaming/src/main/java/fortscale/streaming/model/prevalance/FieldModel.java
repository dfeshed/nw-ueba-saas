package fortscale.streaming.model.prevalance;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Counts field values occurrences and builds an historical 
 * model of values 
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public interface FieldModel {
	
	/**
	 * Count the field value in the model, set at the given time stamp
	 */
	void add(Object value, long timestamp);
	
	
	/**
	 * calculate the score of the feature value.
	 */
	double calculateScore(Object value);
	
	/**
	 * Determines if the field model should participate in event score
	 */
	boolean shouldAffectEventScore();
}
