package fortscale.streaming.serialization;

import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

import fortscale.aggregation.feature.bucket.FeatureBucket;

public class FeatureBucketSerdeFactory implements SerdeFactory<FeatureBucket> {

	@Override
	public Serde<FeatureBucket> getSerde(String name, Config config) {
		return new GenericJacksonSerde<FeatureBucket>(FeatureBucket.class);
	}

}
