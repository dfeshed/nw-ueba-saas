package fortscale.streaming.model;

/**
 * Counts field values occurrences and builds an historical 
 * model of values 
 */
public interface FieldModel {
	
	/**
	 * Count the field value in the model, set at the given time stamp
	 */
	void add(Object value, long timestamp);
}
