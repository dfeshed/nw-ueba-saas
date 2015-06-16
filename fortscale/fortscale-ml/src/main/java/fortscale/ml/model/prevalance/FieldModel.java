package fortscale.ml.model.prevalance;

import org.apache.samza.config.Config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Counts field values occurrences and builds an historical 
 * model of values 
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public interface FieldModel {
	
	/**
	 * Initialize the field model with configuration values
	 * The fieldModel only states the suffix of the configration key. The prefix should be given by the caller.
	 */
	void init(String prefix, String fieldName, Config config);
		
	/**
	 * Count the field value in the model, set at the given time stamp
	 */
	void add(Object value, long timestamp);
	
	
	/**
	 * calculate the score of the feature value.
	 */
	double calculateScore(Object value);	
	
	long getNumOfSamples();
}
