package fortscale.ml.model.selector;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.List;

@Configurable(preConstruction = true)
public class FeatureBucketContextSelector implements ContextSelector {
	@Autowired
	private BucketConfigurationService bucketConfigurationService;

	@Autowired
	private FeatureBucketsReaderService featureBucketsReaderService;

	private FeatureBucketConf featureBucketConf;

	public FeatureBucketContextSelector(ContextSelectorConf conf) {
		FeatureBucketContextSelectorConf featureBucketContextSelectorConf = (FeatureBucketContextSelectorConf)conf;
		this.featureBucketConf = bucketConfigurationService.getBucketConf(featureBucketContextSelectorConf.getFeatureBucketConfName());
		Assert.notNull(featureBucketConf);
	}

	@Override
	public List<String> getContexts(DateTime startTime, DateTime endTime) {
		return featureBucketsReaderService.findDistinctContextByTimeRange(featureBucketConf, startTime.getMillis(), endTime.getMillis());
	}
}
