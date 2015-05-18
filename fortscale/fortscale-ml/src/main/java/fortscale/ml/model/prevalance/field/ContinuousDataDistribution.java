package fortscale.ml.model.prevalance.field;

import fortscale.ml.model.prevalance.FieldModel;
import fortscale.utils.ConversionUtils;
import org.apache.samza.config.Config;

import java.util.HashMap;
import java.util.Map;

public class ContinuousDataDistribution implements FieldModel {
	private static final double DEFAULT_BUCKET_SIZE = 1.0;

	protected double bucketSize;
	protected Map<Double, Long> distribution;
	private Long totalCount; // number of non-unique values
	private ContinuousDataModel continuousDataModel;

	@Override
	public void init(String prefix, String fieldName, Config config) {
		this.bucketSize = DEFAULT_BUCKET_SIZE;

		// Override default bucket size with configuration bucket size (if a valid one exists)
		String bucketSizeFieldname = String.format("%s.%s.continuous.data.distribution.bucket.size", prefix, fieldName);
		if (config.containsKey(bucketSizeFieldname)) {
			double bucketSize = config.getDouble(bucketSizeFieldname);
			if (bucketSize > 0) {
				this.bucketSize = bucketSize;
			}
		}

		distribution = new HashMap<>();
		totalCount = 0L;
		continuousDataModel = new ContinuousDataModel();
	}

	@Override
	public void add(Object value, long timestamp) {
		// Convert the value to double
		Double doubleValue = ConversionUtils.convertToDouble(value);
		if (doubleValue == null || doubleValue.isNaN())
			return;

		// Round number and add to distribution
		doubleValue = roundValue(doubleValue);
		Long oldCount = distribution.get(doubleValue);
		Long newCount = oldCount == null ? 1 : oldCount + 1;
		distribution.put(doubleValue, newCount);
		totalCount++;

		updateDistribution();
		updateModel();
	}

	@Override
	public double calculateScore(Object value) {
		Double doubleValue = ConversionUtils.convertToDouble(value);

		// Validate and calculate score according to model
		if (doubleValue == null || doubleValue.isNaN())
			return 0;
		else
			return continuousDataModel.calculateScore(doubleValue);
	}

	protected double roundValue(double value) {
		return bucketSize * Math.round(value / bucketSize);
	}

	protected void updateDistribution() {
		// No need to update distribution
	}

	/**
	 * Updates the continuous data model.
	 */
	private void updateModel() {
		// Calculate mean
		double sum = 0;
		for (Map.Entry<Double, Long> entry : distribution.entrySet())
			sum += entry.getKey() * entry.getValue();
		double mean = sum / totalCount;

		// Calculate standard deviation
		sum = 0;
		for (Map.Entry<Double, Long> entry : distribution.entrySet())
			sum += Math.pow(entry.getKey() - mean, 2) * entry.getValue();
		double sd = Math.sqrt(sum / totalCount);

		continuousDataModel.setParameters(totalCount, mean, sd);
	}

	/**
	 * In case global models in the same package need the actual distribution.
	 *
	 * @return the map representation of the distribution.
	 */
	protected Map<Double, Long> getDistribution() {
		return distribution;
	}

	/**
	 * In case global models in the same package need the total count.
	 *
	 * @return the total count of non-unique values.
	 */
	protected Long getTotalCount() {
		return totalCount;
	}
}
