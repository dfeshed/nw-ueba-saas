package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.Model;
import fortscale.ml.model.prevalance.calibration.FeatureCalibration;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class TimeModel implements Model {
	private static Logger logger = Logger.getLogger(TimeModel.class);
	private static final int MASK_SIZE = 10;

	private ArrayList<Double> buckets;
	private ArrayList<Integer> bucketsRealCount;
	private int total;

	private int timeResolution;
	private int bucketSize;
	private int numOfBuckets;

	private FeatureCalibration calibration;

	public TimeModel(int timeResolution, int bucketSize) {
		this.timeResolution = timeResolution;
		this.bucketSize = bucketSize;
		this.numOfBuckets = (int) Math.ceil(timeResolution/(double)bucketSize);
		init();
	}

	public void init() {
		buckets = new ArrayList<>(numOfBuckets);
		for (int i = 0; i < numOfBuckets; i++) {
			buckets.add(0D);
		}
		bucketsRealCount = new ArrayList<>(numOfBuckets);
		for (int i = 0; i < numOfBuckets; i++) {
			bucketsRealCount.add(0);
		}
		total = 0;
	}

	private double score(long epochSeconds) {
		String bucketName = Integer.toString(getBucketIndex(epochSeconds));

		return calibration != null ? calibration.score(bucketName) : 0;
	}

	public int getBucketIndex(long epochSeconds) {
		return (int) ( (epochSeconds % timeResolution) / bucketSize );
	}

	public void update(long epochSeconds) throws Exception {
		int pivot = getBucketIndex(epochSeconds);
		bucketsRealCount.set(pivot, bucketsRealCount.get(pivot) + 1);
		double val = buckets.get(pivot)+1;
		buckets.set(pivot, val);
		updateCalibration(pivot, val);
		int upIndex = pivot + 1;
		int downIndex = pivot - 1 + numOfBuckets;
		for (int i = 0; i < MASK_SIZE; i++,upIndex++,downIndex--) {
			double addVal = 1 - i/((double)MASK_SIZE);
			int index = upIndex % numOfBuckets;
			val = buckets.get(index) + addVal;
			buckets.set(index, val);
			if (bucketsRealCount.get(index) > 0) {
				updateCalibration(index, val);
			}

			index = downIndex % numOfBuckets;
			val = buckets.get(index) + addVal;
			buckets.set(index, val);
			if (bucketsRealCount.get(index) > 0) {
				updateCalibration(index, val);
			}
		}

		total++;


	}

	private void updateCalibration(int bucketIndex, double val) throws Exception {
		if (calibration == null) {
			initCalibration();
		} else{
			calibration.updateFeatureValueCount(Integer.toString(bucketIndex), val);
		}
	}

	private void initCalibration() throws Exception {
		calibration = new FeatureCalibration();
		Map<String, Double> tmp = new HashMap<>();

		for (int i = 0; i < numOfBuckets; i++) {
			Double val = buckets.get(i);
			if (val < 1) {
				continue;
			}

			tmp.put(Integer.toString(i), val);
		}

		calibration.init(tmp);
	}

	public int getTotal() {
		return total;
	}

	public void add(Object value) {
		try {
			Long epoch = convertToSeconds(value);
			if (epoch != null) {
				update(epoch);
			}
		} catch (Exception e) {
			logger.warn("got an exception while trying to add {} to the DailyTimeModel", value);
			logger.warn("got an exception while trying to add value to the DailyTimeModel", e);
		}
	}
	
	private Long convertToSeconds(Object value) {
		if (value == null) {
			return null;
		}
		
		Long ret = null;
		if (value instanceof Long) {
			ret = (Long)value;
		} else if (value instanceof Long || value instanceof Integer) {
			ret = ((Integer) value).longValue();
		} else if (value instanceof String) {
			try{
				if (StringUtils.isNotEmpty((String) value)) {
					ret = Long.parseLong((String) value);
				}
			} catch(NumberFormatException nfe) {
				logger.warn("got the String value ({}) which is not a Long as expected.");
			}
		} else{
			logger.warn("got value {} of instance {} instead of Long or String", value, value.getClass());
		}
		
		if (ret != null) {
			ret = TimestampUtils.convertToSeconds(ret);
		}
		return ret;
	}

	@Override
	public double calculateScore(Object value) {
		double ret = 0;
		try {
			Long epoch = convertToSeconds(value);
			if (epoch != null) {
				ret = score(epoch);
			}
		} catch (Exception e) {
			logger.warn("got an exception while trying to add {} to the DailyTimeModel", value);
			logger.warn("got an exception while trying to add value to the DailyTimeModel", e);
		}

		return ret;
	}
}
