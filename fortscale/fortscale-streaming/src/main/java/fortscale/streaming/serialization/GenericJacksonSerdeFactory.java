package fortscale.streaming.serialization;

import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

/**
 * Generic serde factory that receives the concrete class name to serialize using jackson json framework
 */
public class GenericJacksonSerdeFactory implements SerdeFactory {

    @Override
    public Serde getSerde(String name, Config config) {
        // get the the class name to serialize
        Class underlying = config.getClass(String.format("serializers.registry.%s.underlying.class", name));
        return new GenericJacksonSerde(underlying);
    }
}
