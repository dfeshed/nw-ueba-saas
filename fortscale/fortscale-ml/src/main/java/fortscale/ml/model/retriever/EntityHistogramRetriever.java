package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
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

import java.util.List;
import java.util.Map;

@Configurable(preConstruction = true)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class EntityHistogramRetriever extends IDataRetriever {
	public static final String DATA_RETRIEVER_TYPE = "entity_histogram_retriever";

	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private FeatureBucketsReaderService featureBucketsReaderService;

	private String featureBucketConfName;
	private String featureName;

	@Override
	public Object retrieve(String contextId) {
		FeatureBucketConf featureBucketConf = bucketConfigurationService.getBucketConf(featureBucketConfName);
		long endTimeInSeconds = TimestampUtils.convertToSeconds(System.currentTimeMillis());
		long startTimeInSeconds = endTimeInSeconds - timeRangeInSeconds;

		List<FeatureBucket> featureBuckets = featureBucketsReaderService.getFeatureBucketsByContextIdAndTimeRange(featureBucketConf, contextId, startTimeInSeconds, endTimeInSeconds);
		ContinuousDataHistogram reductionHistogram = new ContinuousDataHistogram();

		for (FeatureBucket featureBucket : featureBuckets) {
			Map<String, Feature> aggregatedFeatures = featureBucket.getAggregatedFeatures();

			if (aggregatedFeatures.containsKey(featureName)) {
				FeatureValue featureValue = aggregatedFeatures.get(featureName).getValue();
				ContinuousDataHistogram histogram = new ContinuousDataHistogram();
				histogram.add(((GenericHistogram)featureValue).getHistogramMap());

				for (IDataRetrieverFunction function : functions) {
					histogram = (ContinuousDataHistogram)function.execute(histogram);
				}

				reductionHistogram.add(histogram.getMap());
			}
		}

		return reductionHistogram.getMap();
	}
}
