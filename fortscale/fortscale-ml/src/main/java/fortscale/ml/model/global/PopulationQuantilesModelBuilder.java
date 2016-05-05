//package fortscale.ml.model.prevalance.field;
//
//import fortscale.ml.model.prevalance.FieldModel;
//import fortscale.ml.model.prevalance.FieldModelBuilder;
//import fortscale.ml.model.prevalance.PrevalanceModel;
//import org.apache.commons.lang.StringUtils;
//import org.apache.samza.config.Config;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.util.Assert;
//
//import java.util.*;
//
//public class PopulationQuantilesModelBuilder implements FieldModelBuilder {
//	private static final Logger logger = LoggerFactory.getLogger(PopulationQuantilesModelBuilder.class);
//
//	private String localModelName;
//	private String localFieldModelName;
//	private Map<Double, Long> distribution;
//	private long numOfValues;
//
//	@Override
//	public void initBuilder(Config config, String fieldModelName) {
//		Assert.notNull(config, "Missing configuration");
//		Assert.isTrue(StringUtils.isNotBlank(fieldModelName), "Missing valid global field model name");
//
//		// Get global field model configuration
//		Config subset = config.subset(String.format("fortscale.model.global.field.model.%s.", fieldModelName));
//
//		// Get local prevalence model name
//		String localModelName = subset.get("local.model.name");
//		Assert.isTrue(StringUtils.isNotBlank(localModelName), "Missing local prevalence model name");
//		this.localModelName = localModelName;
//
//		// Get name of field model within the local prevalence model
//		String localFieldModelName = subset.get("local.field.model.name");
//		Assert.isTrue(StringUtils.isNotBlank(localFieldModelName), "Missing local field model name");
//		this.localFieldModelName = localFieldModelName;
//
//		this.distribution = new HashMap<>();
//		this.numOfValues = 0; // non-unique values
//	}
//
//	@Override
//	public void feedBuilder(PrevalanceModel prevalanceModel) {
//		// Filter out other prevalence models
//		if (!prevalanceModel.getModelName().equals(localModelName))
//			return;
//
//		// Filter out other field models
//		FieldModel localFieldModel = prevalanceModel.getFieldModel(localFieldModelName);
//		if (localFieldModel == null)
//			return;
//
//		// Validate class type
//		String error = "Local field model %s must be an instance of ContinuousDataDistribution";
//		error = String.format(error, localFieldModel.getClass().getName());
//		Assert.isTrue(ContinuousDataDistribution.class.isInstance(localFieldModel), error);
//
//		feedBuilder((ContinuousDataDistribution)localFieldModel);
//	}
//
//	protected void feedBuilder(ContinuousDataDistribution localDistribution) {
//		// Add local values to the global distribution
//		for (Map.Entry<Double, Long> entry : localDistribution.getDistribution().entrySet())
//			addValue(entry.getKey(), entry.getValue());
//	}
//
//	protected void addValue(Double value, Long count) {
//		if (value == null || value.isNaN()) {
//			logger.warn("Must accept a valid value");
//		} else if (count == null || count <= 0) {
//			logger.warn("Must accept a positive count");
//		} else {
//			Long oldCount = distribution.get(value);
//			distribution.put(value, oldCount == null ? count : oldCount + count);
//			numOfValues += count;
//		}
//	}
//
//	@Override
//	public FieldModel buildModel() {
//		// Create a new quantiles model
//		QuantilesModel model = new QuantilesModel();
//
//		long currentIndex = 0;
//		int quantile = 1;
//		double quantileIndex = toIndex(quantile);
//
//		// Sort the entries of the distribution according to the keys
//		List<Map.Entry<Double, Long>> sortedEntries = getSortedEntries(distribution);
//
//		/* Iterate the entries and increment the index accordingly.
//		 * Check each iteration if the index reached the next quantile location.
//		 * If so, set the quantile value to the model and advance to next quantile */
//		for (Map.Entry<Double, Long> entry : sortedEntries) {
//			currentIndex += entry.getValue();
//			while (quantileIndex <= currentIndex) {
//				model.setQuantile(quantile, entry.getKey());
//				quantile++;
//				quantileIndex = toIndex(quantile);
//			}
//		}
//
//		return model;
//	}
//
//	private double toIndex(int quantile) {
//		return (quantile / 100d) * numOfValues;
//	}
//
//	protected static List<Map.Entry<Double, Long>> getSortedEntries(Map<Double, Long> map) {
//		List<Map.Entry<Double, Long>> sortedEntries = new ArrayList<>(map.entrySet());
//
//		Collections.sort(sortedEntries, new Comparator<Map.Entry<Double, Long>>() {
//			public int compare(Map.Entry<Double, Long> entry1, Map.Entry<Double, Long> entry2) {
//				return Double.compare(entry1.getKey(), entry2.getKey());
//			}
//		});
//
//		return sortedEntries;
//	}
//}
