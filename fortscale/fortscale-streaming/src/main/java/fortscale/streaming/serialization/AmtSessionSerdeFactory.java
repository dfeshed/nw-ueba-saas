package fortscale.streaming.serialization;

import fortscale.streaming.model.AmtSession;
import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

public class AmtSessionSerdeFactory implements SerdeFactory<AmtSession> {
	@Override
	public Serde<AmtSession> getSerde(String name, Config config) {
		return new GenericJacksonSerde<AmtSession>(AmtSession.class);
	}
}
