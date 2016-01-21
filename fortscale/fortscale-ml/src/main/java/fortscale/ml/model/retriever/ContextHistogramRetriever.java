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

				for (IDataRetrieverFunction function : functions) {
					histogram = (GenericHistogram)function.execute(histogram, dataTime, endTime);
				}

				reductionHistogram.add(histogram);
			}
		}

		return reductionHistogram.getN() > 0 ? reductionHistogram : null;
	}

	/**
	 *
	 * @return a list of names as they appear in the events, of the features which are the base for the data that this
	 * retriever retrieves.
     */
	@Override
	public Set<String> getEventFeatureNames() {
		AggregatedFeatureConf aggregatedFeatureConf = featureBucketConf.getAggregatedFeatureConf(featureName);
		return aggregatedFeatureConf.getAllFeatureNames();
	}

	@Override
	public List<String> getContextFieldNames() {
		return featureBucketConf.getContextFieldNames();
	}
}
