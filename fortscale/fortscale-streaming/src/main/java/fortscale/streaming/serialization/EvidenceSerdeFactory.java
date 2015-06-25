package fortscale.streaming.serialization;

import fortscale.domain.core.Evidence;
import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

/**
 * Define serialization for the Evidence
 *
 * Date: 6/25/2015.
 */
public class EvidenceSerdeFactory implements SerdeFactory<Evidence> {

	@Override
	public Serde<Evidence> getSerde(String name, Config config) {
		return new GenericJacksonSerde<>(Evidence.class);
	}
}
