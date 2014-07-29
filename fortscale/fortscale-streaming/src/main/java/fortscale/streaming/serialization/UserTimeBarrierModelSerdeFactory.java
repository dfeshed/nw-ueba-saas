package fortscale.streaming.serialization;

import fortscale.streaming.model.prevalance.UserTimeBarrier;
import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

public class UserTimeBarrierModelSerdeFactory implements SerdeFactory<UserTimeBarrier> {

    @Override
    public Serde<UserTimeBarrier> getSerde(String s, Config config) {
        return new GenericJacksonSerde<UserTimeBarrier>(UserTimeBarrier.class);
    }
}
