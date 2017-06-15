package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;

import java.util.List;

public class EntityEventValueRetrieverConf extends AbstractEntityEventValueRetrieverConf {
	public static final String ENTITY_EVENT_VALUE_RETRIEVER = "entity_event_value_retriever";

	@JsonCreator
	public EntityEventValueRetrieverConf(
			@JsonProperty("timeRangeInSeconds") long timeRangeInSeconds,
			@JsonProperty("functions") List<JSONObject> functions,
			@JsonProperty("entityEventConfName") String entityEventConfName) {
		super(timeRangeInSeconds, functions, entityEventConfName);
	}

	@Override
	public String getFactoryName() {
		return ENTITY_EVENT_VALUE_RETRIEVER;
	}
}
