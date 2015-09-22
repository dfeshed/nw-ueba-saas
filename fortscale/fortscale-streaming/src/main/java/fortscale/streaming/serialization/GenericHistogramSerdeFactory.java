package fortscale.streaming.serialization;

import fortscale.aggregation.feature.util.GenericHistogram;
import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

/**
 * Created by amira on 22/09/2015.
 */
public class GenericHistogramSerdeFactory  implements SerdeFactory<GenericHistogram> {

@Override
public Serde<GenericHistogram> getSerde(String name, Config config) {
        return new GenericJacksonSerde<GenericHistogram>(GenericHistogram.class);
        }
}
