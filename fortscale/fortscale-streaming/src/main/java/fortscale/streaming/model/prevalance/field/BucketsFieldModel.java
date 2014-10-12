package fortscale.streaming.model.prevalance.field;

import java.util.LinkedList;
import java.util.List;

import org.apache.samza.config.Config;
import org.apache.samza.config.ConfigException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * Abstract base class for buckets field model. Deriving implementations are used
 * to adjust the values according to value type. 
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public abstract class BucketsFieldModel extends DiscreetValuesCalibratedModel {

	private static final String BUCKET_RANGES_CONFIG = "fortscale.fields.%s.buckets";
	
	protected abstract Object convertValueToNumber(Object value);
	protected abstract boolean isValueLessThanBucketLimit(Object value, Object bucketLimit);
	
	@JsonIgnore
	private List<Object> bucketRanges = new LinkedList<Object>(); 
	
	@Override
	public void init(String fieldName, Config config) {
		// prepare bucket ranges from configuration
		List<String> bucketsConfig = config.getList(String.format(BUCKET_RANGES_CONFIG, fieldName));
		for (String bucketLimit : bucketsConfig) {
			Object limit = convertValueToNumber(bucketLimit);
			if (limit!=null) {
				bucketRanges.add(limit);
			} else {
				throw new ConfigException(String.format("bucket limit %s is not of valid type for field %s", bucketLimit, fieldName));
			}
		}
	}
	
	protected String convertValueToBucket(Object value) {
		if (value==null)
			return null;
		
		int bucketNum = 1;
		for (Object limit : bucketRanges) {
			if (isValueLessThanBucketLimit(value, limit))
				break;
			bucketNum++;
		}
		// return the bucket name for the matched bucket
		return String.format("bucket_%d", bucketNum);
	}	
	
	@Override
	public void add(Object value, long timestamp) {
		super.add(convertValueToBucket(value), timestamp);
	}

	@Override
	public double calculateScore(Object value) {
		return super.calculateScore(convertValueToBucket(value));
	}

}
