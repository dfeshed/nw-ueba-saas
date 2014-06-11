package fortscale.streaming.model;

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
	 * Get the latest time mark for field values accounted for in the model
	 */
	long getTimeMark();
	
	/**
	 * Checks if the fields time mark is after the given time 
	 */
	boolean isTimeMarkAfter(long timestamp);
}
