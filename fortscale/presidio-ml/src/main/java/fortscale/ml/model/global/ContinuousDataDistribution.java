//package fortscale.ml.model.prevalance.field;
//
//import com.fasterxml.jackson.annotation.JsonAutoDetect;
//import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
//import fortscale.ml.model.prevalance.FieldModel;
//import fortscale.ml.scorer.algorithms.ContinuousValuesModelScorerAlgorithm;
//import fortscale.utils.ConversionUtils;
//import org.apache.samza.config.Config;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
//public class ContinuousDataDistribution implements FieldModel {
//	private static final int DEFAULT_MIN_DISTINCT_VALUES = 100;
//	private static final int DEFAULT_MAX_DISTINCT_VALUES = 10000;
//	private static final double DEFAULT_MIN_BUCKET_SIZE = 0.01;
//	private static final double DEFAULT_MAX_BUCKET_SIZE = 1.0;
//
//	private double bucketSize;
//	private int minDistinctValues;
//	private int maxDistinctValues;
//	private double minBucketSize;
//	private double maxBucketSize;
//
//	private Map<Double, Long> distribution;
//	private Long totalCount; // number of non-unique values
//	private ContinuousDataModel continuousDataModel;
//
//	@Override
//	public void init(String prefix, String fieldName, Config config) {
//		bucketSize = 0;
//		String configKey = String.format("%s.%s.continuous.data.distribution.min.distinct.values", prefix, fieldName);
//		minDistinctValues = config.getInt(configKey, DEFAULT_MIN_DISTINCT_VALUES);
//		configKey = String.format("%s.%s.continuous.data.distribution.max.distinct.values", prefix, fieldName);
//		maxDistinctValues = config.getInt(configKey, DEFAULT_MAX_DISTINCT_VALUES);
//		configKey = String.format("%s.%s.continuous.data.distribution.min.bucket.size", prefix, fieldName);
//		minBucketSize = config.getDouble(configKey, DEFAULT_MIN_BUCKET_SIZE);
//		configKey = String.format("%s.%s.continuous.data.distribution.max.bucket.size", prefix, fieldName);
//		maxBucketSize = config.getDouble(configKey, DEFAULT_MAX_BUCKET_SIZE);
//
//		distribution = new HashMap<>();
//		totalCount = 0L;
//		continuousDataModel = new ContinuousDataModel();
//	}
//
//	@Override
//	public long getNumOfSamples() {
//		return totalCount;
//	}
//
//	@Override
//	public void add(Object value, long timestamp) {
//		// Convert the value to double
//		Double doubleValue = ConversionUtils.convertToDouble(value);
//		if (doubleValue == null || doubleValue.isNaN())
//			return;
//
//		// Round number and add to distribution
//		doubleValue = roundValue(doubleValue);
//		Long oldCount = distribution.get(doubleValue);
//		Long newCount = oldCount == null ? 1 : oldCount + 1;
//		distribution.put(doubleValue, newCount);
//		totalCount++;
//
//		updateDistribution();
//		updateModel();
//	}
//
//	@Override
//	public double calculateScore(Object value) {
//		Double doubleValue = ConversionUtils.convertToDouble(value);
//
//		// Validate and calculate score according to model
//		if (doubleValue == null || doubleValue.isNaN())
//			return 0;
//		else
//			return ContinuousValuesModelScorerAlgorithm.calculate(continuousDataModel, doubleValue);
//	}
//
//	private double roundValue(double value) {
//		return bucketSize > 0 ? bucketSize * Math.round(value / bucketSize) : value;
//	}
//
//	private void updateDistribution() {
//		while ((distribution.size() > minDistinctValues && bucketSize < maxBucketSize) || distribution.size() > maxDistinctValues) {
//			bucketSize = bucketSize > 0 ? bucketSize * 2 : minBucketSize;
//			Map<Double, Long> newDistribution = new HashMap<>();
//
//			for (Map.Entry<Double, Long> entry : distribution.entrySet()) {
//				Double value = roundValue(entry.getKey());
//				Long oldCount = newDistribution.get(value);
//				Long newCount = oldCount == null ? entry.getValue() : oldCount + entry.getValue();
//				newDistribution.put(value, newCount);
//			}
//
//			distribution = newDistribution;
//		}
//	}
//
//	private void updateModel() {
//		// Calculate mean
//		double sum = 0;
//		for (Map.Entry<Double, Long> entry : distribution.entrySet())
//			sum += entry.getKey() * entry.getValue();
//		double mean = sum / totalCount;
//
//		// Calculate standard deviation
//		sum = 0;
//		for (Map.Entry<Double, Long> entry : distribution.entrySet())
//			sum += Math.pow(entry.getKey() - mean, 2) * entry.getValue();
//		double sd = Math.sqrt(sum / totalCount);
//
//		continuousDataModel.setParameters(totalCount, mean, sd);
//	}
//
//	/**
//	 * In case global models in the same package need the actual distribution.
//	 *
//	 * @return the map representation of the distribution.
//	 */
//	protected Map<Double, Long> getDistribution() {
//		return distribution;
//	}
//
//	/**
//	 * In case global models in the same package need the total count.
//	 *
//	 * @return the total count of non-unique values.
//	 */
//	protected Long getTotalCount() {
//		return totalCount;
//	}
//}
