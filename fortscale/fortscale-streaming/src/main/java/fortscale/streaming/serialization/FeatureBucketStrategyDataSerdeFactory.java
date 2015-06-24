package fortscale.streaming.serialization;

import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

import fortscale.streaming.service.aggregation.bucket.strategy.FeatureBucketStrategyData;

public class FeatureBucketStrategyDataSerdeFactory  implements SerdeFactory<FeatureBucketStrategyData>{

	@Override
	public Serde<FeatureBucketStrategyData> getSerde(String name, Config config) {
		return new GenericJacksonSerde<FeatureBucketStrategyData>(FeatureBucketStrategyData.class);
	}

}
