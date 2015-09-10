package fortscale.streaming.serialization;

import fortscale.domain.core.EntityTags;

import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

/**
 * Created by galiar on 09/09/2015.
 */
public class EntityTagsSerdeFactory implements SerdeFactory<EntityTags> {

	@Override
	public Serde<EntityTags> getSerde(String name, Config config) {
		return new GenericJacksonSerde<EntityTags>(EntityTags.class);
	}
}
