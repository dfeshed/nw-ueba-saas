package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.FeatureValue;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.ml.model.data.type.ContinuousDataHistogram;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Configurable(preConstruction = true)
public class ContextHistogramRetriever extends AbstractDataRetriever {
	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private FeatureBucketsReaderService featureBucketsReaderService;

	private FeatureBucketConf featureBucketConf;
	private String featureName;

	public ContextHistogramRetriever(AbstractDataRetrieverConf dataRetrieverConf) {
		super(dataRetrieverConf);
		ContextHistogramRetrieverConf contextHistogramRetrieverConf = (ContextHistogramRetrieverConf)dataRetrieverConf;
		String featureBucketConfName = contextHistogramRetrieverConf.getFeatureBucketConfName();
		featureBucketConf = bucketConfigurationService.getBucketConf(featureBucketConfName);
		featureName = contextHistogramRetrieverConf.getFeatureName();
	}

	@Override
	public Object retrieve(String contextId, Date endTime) {
		long endTimeInSeconds = TimestampUtils.convertToSeconds(endTime.getTime());
		long startTimeInSeconds = endTimeInSeconds - timeRangeInSeconds;
		Date startTime = new Date(TimestampUtils.convertToMilliSeconds(startTimeInSeconds));

		List<FeatureBucket> featureBuckets = featureBucketsReaderService.getFeatureBucketsByContextIdAndTimeRange(
				featureBucketConf, contextId, startTimeInSeconds, endTimeInSeconds);
		ContinuousDataHistogram reductionHistogram = new ContinuousDataHistogram(startTime, endTime);

		for (FeatureBucket featureBucket : featureBuckets) {
			Map<String, Feature> aggregatedFeatures = featureBucket.getAggregatedFeatures();
			Date featureBucketStartTime = new Date(TimestampUtils.convertToMilliSeconds(featureBucket.getStartTime()));
			Date featureBucketEndTime = new Date(TimestampUtils.convertToMilliSeconds(featureBucket.getEndTime()));

			if (aggregatedFeatures.containsKey(featureName)) {
				FeatureValue featureValue = aggregatedFeatures.get(featureName).getValue();
				ContinuousDataHistogram histogram = new ContinuousDataHistogram(featureBucketStartTime, featureBucketEndTime);
				histogram.add(((GenericHistogram)featureValue).getHistogramMap());

				for (IDataRetrieverFunction function : functions) {
					histogram = (ContinuousDataHistogram)function.execute(histogram, endTime);
				}

				reductionHistogram.add(histogram.getMap());
			}
		}

		return reductionHistogram.getMap();
	}
}
