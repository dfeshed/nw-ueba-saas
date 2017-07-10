package fortscale.ml.model.selector;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.ml.model.exceptions.InvalidFeatureBucketConfNameException;
import fortscale.utils.time.TimeRange;

import java.util.Set;

public class FeatureBucketContextSelector implements IContextSelector {
	private FeatureBucketConf featureBucketConf;
	private FeatureBucketReader featureBucketReader;

	public FeatureBucketContextSelector(
			FeatureBucketContextSelectorConf conf,
			BucketConfigurationService bucketConfigurationService,
			FeatureBucketReader featureBucketReader) {

		this.featureBucketConf = bucketConfigurationService.getBucketConf(conf.getFeatureBucketConfName());
		this.featureBucketReader = featureBucketReader;
		validate(conf);
	}

	@Override
	public Set<String> getContexts(TimeRange timeRange) {
		return featureBucketReader.getDistinctContextIds(featureBucketConf, timeRange);
	}

	private void validate(FeatureBucketContextSelectorConf conf) {
		if (featureBucketConf == null) {
			throw new InvalidFeatureBucketConfNameException(conf.getFeatureBucketConfName());
		}
	}
}
