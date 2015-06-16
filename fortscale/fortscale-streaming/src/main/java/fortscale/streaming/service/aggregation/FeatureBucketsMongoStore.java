package fortscale.streaming.service.aggregation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.util.List;

public class FeatureBucketsMongoStore implements FeatureBucketsStore {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<FeatureBucket> getFeatureBuckets() {
		// TODO implement after updating API
		return null;
	}

	@Override
	public void saveFeatureBucket() {
		// TODO implement after updating API
	}
}
