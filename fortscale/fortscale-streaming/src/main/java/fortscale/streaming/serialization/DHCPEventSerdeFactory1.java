package fortscale.streaming.serialization;

import fortscale.domain.events.ComputerLoginEvent;
import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

/**
 * Created by idanp on 7/7/2014.
 */
public class DHCPEventSerdeFactory1 implements SerdeFactory<ComputerLoginEvent> {

    @Override
    public Serde<ComputerLoginEvent> getSerde (String name, Config config)
    {
        return new GenericJacksonSerde<ComputerLoginEvent>(ComputerLoginEvent.class);
    }
}
