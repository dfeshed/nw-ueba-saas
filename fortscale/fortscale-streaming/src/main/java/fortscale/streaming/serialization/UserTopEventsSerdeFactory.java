package fortscale.streaming.serialization;

import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

import fortscale.streaming.service.UserTopEvents;

public class UserTopEventsSerdeFactory implements SerdeFactory<UserTopEvents>{

	@Override
	public Serde<UserTopEvents> getSerde(String arg0, Config arg1) {
		return new UserTopEventsSerde();
	}

}
