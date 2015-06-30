package fortscale.streaming.serialization;

import fortscale.domain.core.Alert;
import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

/**
 * Define serialization for the Alert
 *
 * Date: 6/30/2015.
 */
public class AlertSerdeFactory implements SerdeFactory<Alert> {

	@Override
	public Serde<Alert> getSerde(String name, Config config) {
		return new GenericJacksonSerde<>(Alert.class);
	}
}
