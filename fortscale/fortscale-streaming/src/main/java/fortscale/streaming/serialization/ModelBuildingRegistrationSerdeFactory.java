package fortscale.streaming.serialization;

import fortscale.streaming.service.model.ModelBuildingRegistration;
import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

public class ModelBuildingRegistrationSerdeFactory implements SerdeFactory<ModelBuildingRegistration> {
	@Override
	public Serde<ModelBuildingRegistration> getSerde(String name, Config config) {
		return new GenericJacksonSerde<>(ModelBuildingRegistration.class);
	}
}
