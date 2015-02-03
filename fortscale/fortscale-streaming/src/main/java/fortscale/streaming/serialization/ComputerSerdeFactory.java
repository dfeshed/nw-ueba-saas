package fortscale.streaming.serialization;

import fortscale.domain.core.Computer;
import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

/**
 * Created by idanp on 7/7/2014.
 */
public class ComputerSerdeFactory implements SerdeFactory<Computer> {

    @Override
    public Serde<Computer> getSerde (String name, Config config)
    {
        return new GenericJacksonSerde<Computer>(Computer.class);
    }
}
