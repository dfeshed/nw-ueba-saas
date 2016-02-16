package fortscale.ml.model.selector;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import fortscale.ml.model.InvalidFeatureBucketConfNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

@Configurable(preConstruction = true)
public class FeatureBucketContextSelector implements IContextSelector {
	@Autowired
	private BucketConfigurationService bucketConfigurationService;
	@Autowired
	private FeatureBucketsReaderService featureBucketsReaderService;

	private FeatureBucketConf featureBucketConf;

	public FeatureBucketContextSelector(FeatureBucketContextSelectorConf config) {
		validate(config);
		String featureBucketConfName = config.getFeatureBucketConfName();
		featureBucketConf = bucketConfigurationService.getBucketConf(featureBucketConfName);
	}
	@Override
	public List<String> getContexts(Date startTime, Date endTime) {
		return featureBucketsReaderService.findDistinctContextByTimeRange(
				featureBucketConf, startTime.getTime(), endTime.getTime());
	}
	private void validate(FeatureBucketContextSelectorConf config)
	{
		String featureBucketConfName = config.getFeatureBucketConfName();
		featureBucketConf = bucketConfigurationService.getBucketConf(featureBucketConfName);
		if (featureBucketConf==null)
			throw new InvalidFeatureBucketConfNameException(featureBucketConfName);
	}
}
