package fortscale.streaming.service.aggregation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;


@Component
public class FeatureBucketsMongoStore implements FeatureBucketsStore {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<FeatureBucket> getFeatureBuckets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveFeatureBucket() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<FeatureBucket> updateFeatureBucketsEndTime(FeatureBucketConf featureBucketConf, String strategyId,
			long newCloseTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FeatureBucket getFeatureBucket(FeatureBucketConf featureBucketConf,String strategyId) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
