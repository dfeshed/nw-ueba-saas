package fortscale.streaming.serialization;

import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

import fortscale.domain.events.IseEvent;

/**
 * Created by idanp on 7/7/2014.
 */
public class ISEEventSerdeFactory implements SerdeFactory<IseEvent> {

    @Override
    public Serde<IseEvent> getSerde (String name, Config config)
    {
        return new GenericJacksonSerde<IseEvent>(IseEvent.class);
    }
}
