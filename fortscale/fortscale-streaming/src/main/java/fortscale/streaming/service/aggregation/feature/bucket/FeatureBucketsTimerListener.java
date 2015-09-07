package fortscale.streaming.service.aggregation.feature.bucket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import fortscale.aggregation.DataSourcesSyncTimerListener;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;


@Configurable(preConstruction=true)
public class FeatureBucketsTimerListener implements DataSourcesSyncTimerListener {

	
	@Autowired
	private FeatureBucketsStoreSamza featureBucketsStoreSamza;
	
	private FeatureBucketConf featureBucketConf;
	private String bucketId;
	
	public FeatureBucketsTimerListener(FeatureBucketConf featureBucketConf, String bucketId) {
		this.featureBucketConf = featureBucketConf;
		this.bucketId = bucketId;
	}
	
	
	
	@Override
	public void dataSourcesReachedTime() {
		featureBucketsStoreSamza.sync(featureBucketConf, bucketId);
	}

}
