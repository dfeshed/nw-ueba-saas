package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.bucket.*;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.exceptions.InvalidFeatureBucketConfNameException;
import fortscale.ml.model.exceptions.InvalidFeatureNameException;
import fortscale.ml.model.retriever.function.IDataRetrieverFunction;
import fortscale.ml.model.retriever.metrics.ContextHistogramRetrieverMetrics;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.*;

@Configurable(preConstruction = true)
public class ContextHistogramRetriever extends AbstractDataRetriever {
	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private FeatureBucketsReaderService featureBucketsReaderService;
	@Autowired
	private StatsService statsService;

	private FeatureBucketConf featureBucketConf;
	private String featureName;
	private ContextHistogramRetrieverMetrics metrics;

    public ContextHistogramRetriever(ContextHistogramRetrieverConf config) {
        super(config);
        String featureBucketConfName = config.getFeatureBucketConfName();
        featureBucketConf = bucketConfigurationService.getBucketConf(featureBucketConfName);
        featureName = config.getFeatureName();
        metrics = new ContextHistogramRetrieverMetrics(statsService, featureBucketConfName, featureName);
        validate(config);
    }

	@Override
	public Object retrieve(String contextId, Date endTime) {
		metrics.retrieveAllFeatureValues++;
		return doRetrieve(contextId, endTime, null);
	}

	@Override
	public Object retrieve(String contextId, Date endTime, Feature feature) {
		metrics.retrieveSingleFeatureValue++;
		return doRetrieve(contextId, endTime, feature.getValue().toString());
	}

	@Override
	public Set<String> getEventFeatureNames() {
		metrics.getEventFeatureNames++;
		return featureBucketConf.getAggregatedFeatureConf(featureName).getAllFeatureNames();
	}

	@Override
	public List<String> getContextFieldNames() {
		metrics.getContextFieldNames++;
		return featureBucketConf.getContextFieldNames();
	}

	@Override
	public String getContextId(Map<String, String> context) {
		metrics.getContextId++;
		Assert.notEmpty(context);
		return FeatureBucketUtils.buildContextId(context);
	}

	private GenericHistogram doRetrieve(String contextId, Date endTime, String featureValue) {
		long endTimeInSeconds = TimestampUtils.convertToSeconds(endTime.getTime());
		long startTimeInSeconds = endTimeInSeconds - timeRangeInSeconds;

		String fieldPath = FeatureBucket.AGGREGATED_FEATURES_FIELD_NAME + "." + featureName;
		if (featureValue != null) fieldPath += ".value.histogram." + featureValue;

		List<String> additionalFieldsToInclude = new ArrayList<>();
		boolean fieldMustExist = false;

		if(featureValue != null) {
			additionalFieldsToInclude.add(fieldPath + ".value._class");
			fieldPath += ".value.histogram."+featureValue;
			fieldMustExist = true;
		}

		List<FeatureBucket> featureBuckets = featureBucketsReaderService.getFeatureBucketsByContextIdAndTimeRange(
				featureBucketConf, contextId, startTimeInSeconds, endTimeInSeconds, fieldPath, fieldMustExist, additionalFieldsToInclude);
		metrics.featureBuckets += featureBuckets.size();

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
		metrics.replacePattern++;
		GenericHistogram result = new GenericHistogram();
		for (Map.Entry<String, Double> entry : original.getHistogramMap().entrySet())
			result.add(patternReplacement.replacePattern(entry.getKey()), entry.getValue());
		return result;
	}

	private void validate(ContextHistogramRetrieverConf config) {
		if (featureBucketConf == null) {
			throw new InvalidFeatureBucketConfNameException(config.getFeatureBucketConfName());
		}

		for (AggregatedFeatureConf aggrFeatureConf : featureBucketConf.getAggrFeatureConfs()) {
			if (aggrFeatureConf.getName().equals(featureName)) return;
		}

		throw new InvalidFeatureNameException(featureName, config.getFeatureBucketConfName());
	}

	private GenericHistogram doFilter(GenericHistogram original, String featureValue) {
		Double value = original.get(featureValue);
		GenericHistogram filtered = new GenericHistogram();
		if (value != null) filtered.add(featureValue, value);
		return filtered;
	}
}
