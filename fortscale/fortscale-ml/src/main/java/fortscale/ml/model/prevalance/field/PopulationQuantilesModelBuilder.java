package fortscale.ml.model.prevalance.field;

import fortscale.ml.model.prevalance.FieldModel;
import fortscale.ml.model.prevalance.FieldModelBuilder;
import fortscale.ml.model.prevalance.PrevalanceModel;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.TreeMap;

public class PopulationQuantilesModelBuilder implements FieldModelBuilder {
	private static final Logger logger = LoggerFactory.getLogger(PopulationQuantilesModelBuilder.class);

	private String localModelName;
	private String localFieldModelName;
	private Map<Double, Long> distribution;
	private long numOfValues;

	@Override
	public void initBuilder(Config config, String globalModelName, String fieldModelName) {
		Assert.notNull(config, "Missing configuration");
		Assert.isTrue(StringUtils.isNotBlank(globalModelName), "Missing valid global model name");
		Assert.isTrue(StringUtils.isNotBlank(fieldModelName), "Missing valid field model name");

		// Get field model configuration
		Config subset = config.subset(String.format("fortscale.model.%s.field.model.%s.", globalModelName, fieldModelName));

		// Get local prevalence model name
		String localModelName = subset.get("local.model.name");
		Assert.isTrue(StringUtils.isNotBlank(localModelName), "Missing local prevalence model name");
		this.localModelName = localModelName;

		// Get name of field model within the local prevalence model
		String localFieldModelName = subset.get("local.field.model.name");
		Assert.isTrue(StringUtils.isNotBlank(localFieldModelName), "Missing local field model name");
		this.localFieldModelName = localFieldModelName;

		// By default, a tree map keeps the entries sorted according to the natural ordering of the keys
		this.distribution = new TreeMap<>();

		// Non-unique values
		this.numOfValues = 0;
	}

	@Override
	public void feedBuilder(PrevalanceModel prevalanceModel) {
		// Filter out other prevalence models
		if (!prevalanceModel.getModelName().equals(localModelName))
			return;

		// Filter out other field models
		FieldModel localFieldModel = prevalanceModel.getFieldModel(localFieldModelName);
		if (localFieldModel == null)
			return;

		// Validate class type
		String error = "Local field model %s must be an instance of ContinuousDataDistribution";
		error = String.format(error, localFieldModel.getClass().getName());
		Assert.isTrue(ContinuousDataDistribution.class.isInstance(localFieldModel), error);

		feedBuilder((ContinuousDataDistribution)localFieldModel);
	}

	protected void feedBuilder(ContinuousDataDistribution localDistribution) {
		// Add local values to the global distribution
		for (Map.Entry<Double, Long> entry : localDistribution.getDistribution().entrySet())
			addValue(entry.getKey(), entry.getValue());
	}

	protected void addValue(Double value, Long count) {
		if (value == null || value.isNaN()) {
			logger.warn("Must accept a valid value");
		} else if (count == null || count <= 0) {
			logger.warn("Must accept a positive count");
		} else {
			Long oldCount = distribution.get(value);
			distribution.put(value, oldCount == null ? count : oldCount + count);
			numOfValues += count;
		}
	}

	@Override
	public FieldModel buildModel() {
		// Create a new quantiles model
		QuantilesModel model = new QuantilesModel();

		long upperIndex = 0;
		int quantile = 1;
		double index = toIndex(quantile);

		/* Iterate the values and increment the index accordingly.
		 * Check each iteration if the index reached the next quantile location.
		 * If so, set the quantile value to the model and advance to next quantile */
		for (Map.Entry<Double, Long> entry : distribution.entrySet()) {
			upperIndex += entry.getValue();
			if (index <= upperIndex) {
				model.setQuantile(quantile, entry.getKey());
				index = toIndex(++quantile);
			}
		}

		return model;
	}

	private double toIndex(int quantile) {
		return (quantile / 100d) * numOfValues;
	}
}
