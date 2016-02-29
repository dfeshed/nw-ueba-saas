package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.bucket.*;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.retriever.function.IDataRetrieverFunction;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configurable(preConstruction = true)
public class ContextHistogramRetriever extends AbstractDataRetriever {
	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private FeatureBucketsReaderService featureBucketsReaderService;

	private FeatureBucketConf featureBucketConf;
	private String featureName;

	public ContextHistogramRetriever(ContextHistogramRetrieverConf config) {
		super(config);

		String featureBucketConfName = config.getFeatureBucketConfName();
		featureBucketConf = bucketConfigurationService.getBucketConf(featureBucketConfName);
		Assert.notNull(featureBucketConf);

		featureName = config.getFeatureName();
		Assert.hasText(featureName);
	}

	@Override
	public Object retrieve(String contextId, Date endTime) {
		return doRetrieve(contextId, endTime, null);
	}

	@Override
	public Object retrieve(String contextId, Date endTime, Feature feature) {
		return doRetrieve(contextId, endTime, feature.getValue().toString());
	}

	@Override
	public String getContextId(Map<String, String> context) {
		Assert.notEmpty(context);
		return FeatureBucketUtils.buildContextId(context);
	}

	@Override
	public Set<String> getEventFeatureNames() {
		return featureBucketConf.getAggregatedFeatureConf(featureName).getAllFeatureNames();
	}

	@Override
	public List<String> getContextFieldNames() {
		return featureBucketConf.getContextFieldNames();
	}

	private GenericHistogram doRetrieve(String contextId, Date endTime, String featureValue) {
		long endTimeInSeconds = TimestampUtils.convertToSeconds(endTime.getTime());
		long startTimeInSeconds = endTimeInSeconds - timeRangeInSeconds;

		List<FeatureBucket> featureBuckets = featureBucketsReaderService.getFeatureBucketsByContextIdAndTimeRange(
				featureBucketConf, contextId, startTimeInSeconds, endTimeInSeconds);
		GenericHistogram reductionHistogram = new GenericHistogram();

		for (FeatureBucket featureBucket : featureBuckets) {
			Date dataTime = new Date(TimestampUtils.convertToMilliSeconds(featureBucket.getStartTime()));
			Map<String, Feature> aggregatedFeatures = featureBucket.getAggregatedFeatures();

			if (aggregatedFeatures.containsKey(featureName)) {
				GenericHistogram histogram = (GenericHistogram)aggregatedFeatures.get(featureName).getValue();
				if (patternReplacement != null) histogram = doReplacePattern(histogram);
				if (featureValue != null) histogram = doFilter(histogram, featureValue);

				for (IDataRetrieverFunction function : functions) {
					histogram = (GenericHistogram)function.execute(histogram, dataTime, endTime);
				}

				reductionHistogram.add(histogram);
			}
		}

		return reductionHistogram.getN() > 0 ? reductionHistogram : null;
	}

	private GenericHistogram doReplacePattern(GenericHistogram original) {
		GenericHistogram result = new GenericHistogram();
		for (Map.Entry<String, Double> entry : original.getHistogramMap().entrySet())
			result.add(patternReplacement.replacePattern(entry.getKey()), entry.getValue());
		return result;
	}

	private GenericHistogram doFilter(GenericHistogram original, String featureValue) {
		Double value = original.get(featureValue);
		GenericHistogram filtered = new GenericHistogram();
		if (value != null) filtered.add(featureValue, value);
		return filtered;
	}
}
