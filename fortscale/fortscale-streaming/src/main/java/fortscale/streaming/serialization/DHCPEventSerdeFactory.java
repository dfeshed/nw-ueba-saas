package fortscale.streaming.serialization;

import fortscale.domain.events.DhcpEvent;
import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

/**
 * Created by idanp on 7/7/2014.
 */
public class DHCPEventSerdeFactory implements SerdeFactory<DhcpEvent> {

    @Override
    public Serde<DhcpEvent> getSerde (String name, Config config)
    {
        return new GenericJacksonSerde<DhcpEvent>(DhcpEvent.class);
    }
}
