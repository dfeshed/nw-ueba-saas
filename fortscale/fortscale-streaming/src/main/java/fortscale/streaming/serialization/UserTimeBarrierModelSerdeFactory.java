package fortscale.streaming.serialization;

import fortscale.streaming.model.UserTimeBarrierModel;
import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

public class UserTimeBarrierModelSerdeFactory implements SerdeFactory<UserTimeBarrierModel> {

    @Override
    public Serde<UserTimeBarrierModel> getSerde(String s, Config config) {
        return new UserTimeBarrierModelSerde();
    }
}
