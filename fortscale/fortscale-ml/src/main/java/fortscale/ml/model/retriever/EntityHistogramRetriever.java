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
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;
import java.util.Map;

@Configurable(preConstruction = true)
public class EntityHistogramRetriever extends IDataRetriever {
	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private FeatureBucketsReaderService featureBucketsReaderService;

	private FeatureBucketConf featureBucketConf;
	private String featureName;

	public EntityHistogramRetriever(IDataRetrieverConf dataRetrieverConf) {
		super(dataRetrieverConf);
		EntityHistogramRetrieverConf entityHistogramRetrieverConf = (EntityHistogramRetrieverConf)dataRetrieverConf;
		String featureBucketConfName = entityHistogramRetrieverConf.getFeatureBucketConfName();
		featureBucketConf = bucketConfigurationService.getBucketConf(featureBucketConfName);
		featureName = entityHistogramRetrieverConf.getFeatureName();
	}

	@Override
	public Object retrieve(String contextId, DateTime endTime) {
		long endTimeInSeconds = TimestampUtils.convertToSeconds(endTime.getMillis());
		long startTimeInSeconds = endTimeInSeconds - timeRangeInSeconds;

		List<FeatureBucket> featureBuckets = featureBucketsReaderService.getFeatureBucketsByContextIdAndTimeRange(
				featureBucketConf, contextId, startTimeInSeconds, endTimeInSeconds);
		ContinuousDataHistogram reductionHistogram = new ContinuousDataHistogram();

		for (FeatureBucket featureBucket : featureBuckets) {
			Map<String, Feature> aggregatedFeatures = featureBucket.getAggregatedFeatures();

			if (aggregatedFeatures.containsKey(featureName)) {
				FeatureValue featureValue = aggregatedFeatures.get(featureName).getValue();
				ContinuousDataHistogram histogram = new ContinuousDataHistogram();
				histogram.add(((GenericHistogram)featureValue).getHistogramMap());

				for (IDataRetrieverFunction function : functions) {
					histogram = (ContinuousDataHistogram)function.execute(histogram, endTimeInSeconds - featureBucket.getStartTime());
				}

				reductionHistogram.add(histogram.getMap());
			}
		}

		return reductionHistogram.getMap();
	}
}
