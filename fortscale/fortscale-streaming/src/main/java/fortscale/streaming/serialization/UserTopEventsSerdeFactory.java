package fortscale.streaming.serialization;

import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

import fortscale.streaming.model.UserTopEvents;

public class UserTopEventsSerdeFactory implements SerdeFactory<UserTopEvents>{

	@Override
	public Serde<UserTopEvents> getSerde(String arg0, Config arg1) {
		return new GenericJacksonSerde<UserTopEvents>(UserTopEvents.class);
	}

}
