package fortscale.ml.model.selector;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import fortscale.ml.model.exceptions.InvalidFeatureBucketConfNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

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
		String featureBucketConfName = config.getFeatureBucketConfName();
		featureBucketConf = bucketConfigurationService.getBucketConf(featureBucketConfName);
		validate(config);
	}
	@Override
	public List<String> getContexts(Date startTime, Date endTime) {
		return featureBucketsReaderService.findDistinctContextByTimeRange(
				featureBucketConf, startTime.getTime(), endTime.getTime());
	}
	private void validate(FeatureBucketContextSelectorConf config)
	{
		if (featureBucketConf==null)
			throw new InvalidFeatureBucketConfNameException(config.getFeatureBucketConfName());
	}
}
