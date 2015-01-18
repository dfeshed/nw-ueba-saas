package fortscale.ml.model.prevalance;

import org.apache.samza.config.Config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Adjust a given score for a field value according to custom logic 
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public interface FieldScoreBooster {

	/**
	 * Initialize the field score booster with configuration values
	 */
	void init(String fieldName, Config config);
	
	/**
	 * adjust the given score for the field value.
	 */
	double adjustScore(Object value, double score);
}
