package fortscale.streaming.serialization;

import fortscale.ml.model.cache.ModelsCacheInfo;
import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;


public class ModelsCacheInfoSerdeFactory implements SerdeFactory<ModelsCacheInfo> {
    @Override
    public Serde<ModelsCacheInfo> getSerde(String name, Config config) {
        return new GenericJacksonSerde<>(ModelsCacheInfo.class);
    }
}

