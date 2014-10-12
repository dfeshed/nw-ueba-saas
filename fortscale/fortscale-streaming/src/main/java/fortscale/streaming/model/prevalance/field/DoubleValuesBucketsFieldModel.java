package fortscale.streaming.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import fortscale.utils.ConversionUtils;

/**
 * Field model that places values into buckets of configurable ranges and give score to
 * the field value according to the bucket.
 * Field values and bucket ranges are assumed to be of type double (float included).
 * Configuration for buckets limits should be done by adding a to the streaming task configuration a key 
 * for the given field with the bucket ranges as list of values separated by comma. For example:
 * fortscale.fields.<field name>.buckets=0.01,3.5
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class DoubleValuesBucketsFieldModel extends BucketsFieldModel {

	protected Object convertValueToNumber(Object value) {
		return ConversionUtils.convertToDouble(value);
	}
	
	protected boolean isValueLessThanBucketLimit(Object value, Object bucketLimit) {
		Double longValue = ConversionUtils.convertToDouble(value);
		Double longBucketLimit = ConversionUtils.convertToDouble(bucketLimit);
		
		return longValue < longBucketLimit;
	}
}
