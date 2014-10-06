package fortscale.streaming.model.prevalance;

import org.apache.samza.config.Config;

/**
 * Adjust a given score for a field value according to custom logic 
 */
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
