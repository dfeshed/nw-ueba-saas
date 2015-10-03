package fortscale.streaming.serialization;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.streaming.service.aggregation.entity.event.EntityEventData;
import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

/**
 * Created by amira on 01/10/2015.
 */
public class EntityEventDataSerdeFactory implements  SerdeFactory<EntityEventData> {

        @Override
        public Serde<EntityEventData> getSerde(String name, Config config) {
            return new GenericJacksonSerde<EntityEventData>(EntityEventData.class);
        }

    }
