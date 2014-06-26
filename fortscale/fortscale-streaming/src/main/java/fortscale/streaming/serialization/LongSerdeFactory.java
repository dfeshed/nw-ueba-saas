package fortscale.streaming.serialization;

import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

public class LongSerdeFactory implements SerdeFactory<Long> {

	@Override
	public Serde<Long> getSerde(String name, Config config) {
		return new LongSerde();
	}
}

