package fortscale.streaming.serialization;

import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

import fortscale.ml.model.prevalance.PrevalanceModel;

public class PrevalanceModelSerdeFactory implements SerdeFactory<PrevalanceModel> {

	@Override
	public Serde<PrevalanceModel> getSerde(String name, Config config) {
		return new GenericJacksonSerde<PrevalanceModel>(PrevalanceModel.class);
	}

}
