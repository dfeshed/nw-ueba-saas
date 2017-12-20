package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.bucket.*;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.ModelBuilderData;
import fortscale.ml.model.ModelBuilderData.NoDataReason;
import fortscale.ml.model.exceptions.InvalidFeatureBucketConfNameException;
import fortscale.ml.model.exceptions.InvalidFeatureNameException;
import fortscale.ml.model.metrics.TimeModelRetrieverMetricsContainer;
import fortscale.ml.model.retriever.function.IDataRetrieverFunction;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.TimestampUtils;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContextHistogramRetriever extends AbstractDataRetriever {
	private final long partitionsResolutionInSeconds;
	protected BucketConfigurationService bucketConfigurationService;
	protected FeatureBucketReader featureBucketReader;

	protected FeatureBucketConf featureBucketConf;
	protected String featureName;
	protected TimeModelRetrieverMetricsContainer timeModelRetrieverMetricsContainer;
	//	TODO private ContextHistogramRetrieverMetrics metrics;

	public ContextHistogramRetriever(
			ContextHistogramRetrieverConf config,
			BucketConfigurationService bucketConfigurationService,
			FeatureBucketReader featureBucketReader,
			TimeModelRetrieverMetricsContainer timeModelRetrieverMetricsContainer) {

		super(config);
		this.bucketConfigurationService = bucketConfigurationService;
		this.featureBucketReader = featureBucketReader;
		String featureBucketConfName = config.getFeatureBucketConfName();
		featureBucketConf = this.bucketConfigurationService.getBucketConf(featureBucketConfName);
		featureName = config.getFeatureName();
		partitionsResolutionInSeconds = config.getPartitionsResolutionInSeconds();
		this.timeModelRetrieverMetricsContainer = timeModelRetrieverMetricsContainer;
//		metrics = new ContextHistogramRetrieverMetrics(statsService, featureBucketConfName, featureName);
		validate(config);
	}

	@Override
	public ModelBuilderData retrieve(String contextId, Date endTime) {
//		metrics.retrieveAllFeatureValues++;
		return doRetrieve(contextId, endTime, null);
	}

	@Override
	public ModelBuilderData retrieve(String contextId, Date endTime, Feature feature) {
//		metrics.retrieveSingleFeatureValue++;
		return doRetrieve(contextId, endTime, feature.getValue().toString());
	}

	@Override
	public Set<String> getEventFeatureNames() {
//		metrics.getEventFeatureNames++;
		return featureBucketConf.getAggregatedFeatureConf(featureName).getAllFeatureNames();
	}

	@Override
	public List<String> getContextFieldNames() {
//		metrics.getContextFieldNames++;
		return featureBucketConf.getContextFieldNames();
	}

	@Override
	public String getContextId(Map<String, String> context) {
//		metrics.getContextId++;
		Assert.notEmpty(context, "context cannot be empty.");
		return FeatureBucketUtils.buildContextId(context);
	}

	protected ModelBuilderData doRetrieve(String contextId, Date endTime, String featureValue) {
		long endTimeInSeconds = TimestampUtils.convertToSeconds(endTime.getTime());
		long startTimeInSeconds = endTimeInSeconds - timeRangeInSeconds;

		List<FeatureBucket> featureBuckets = featureBucketReader.getFeatureBuckets(
				featureBucketConf.getName(), contextId,
				new TimeRange(startTimeInSeconds, endTimeInSeconds));
		int featureBucketSize = featureBuckets.size();
		timeModelRetrieverMetricsContainer.updateMetric(featureBucketSize);

//		metrics.featureBuckets += featureBuckets.size();
		long numOfPartitionsOfFeatureBuckets = calcNumOfPartitionsOfFeatureBuckets(featureBuckets);

		if (featureBuckets.isEmpty()) return new ModelBuilderData(NoDataReason.NO_DATA_IN_DATABASE);
		GenericHistogram reductionHistogram = new GenericHistogram();
		createReductionHistogram(endTime, featureValue, featureBuckets, reductionHistogram);

		reductionHistogram.setNumberOfPartitions(numOfPartitionsOfFeatureBuckets);

		return getModelBuilderData(reductionHistogram);
	}

	long calcNumOfPartitionsOfFeatureBuckets(List<FeatureBucket> featureBuckets) {
		return featureBuckets.stream().map(x->((long)(x.getStartTime().getEpochSecond()/partitionsResolutionInSeconds))*partitionsResolutionInSeconds).distinct().count();
	}

	ModelBuilderData getModelBuilderData(GenericHistogram reductionHistogram) {
		if (reductionHistogram.getN() == 0) {
			return new ModelBuilderData(NoDataReason.ALL_DATA_FILTERED);
		} else {
			return new ModelBuilderData(reductionHistogram);
		}
	}

	void createReductionHistogram(Date endTime, String featureValue, List<FeatureBucket> featureBuckets, GenericHistogram reductionHistogram) {
		for (FeatureBucket featureBucket : featureBuckets) {
			Date dataTime = Date.from(featureBucket.getStartTime());
			Map<String, Feature> aggregatedFeatures = featureBucket.getAggregatedFeatures();

			if (aggregatedFeatures.containsKey(featureName)) {
				GenericHistogram histogram = (GenericHistogram)aggregatedFeatures.get(featureName).getValue();
				if (featureValue != null) histogram = doFilter(histogram, featureValue);

				for (IDataRetrieverFunction function : functions) {
					histogram = (GenericHistogram)function.execute(histogram, dataTime, endTime);
				}

				reductionHistogram.add(histogram);
			}
		}
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

	protected GenericHistogram doFilter(GenericHistogram original, String featureValue) {
		Double value = original.get(featureValue);
		GenericHistogram filtered = new GenericHistogram();
		if (value != null) filtered.add(featureValue, value);
		return filtered;
	}
}
