package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

public class EntityEventValueRetrieverConf extends AbstractDataRetrieverConf {
	public static final String ENTITY_EVENT_VALUE_RETRIEVER = "entity_event_value_retriever";

	private String entityEventConfName;

	@JsonCreator
	public EntityEventValueRetrieverConf(
			@JsonProperty("timeRangeInSeconds") long timeRangeInSeconds,
			@JsonProperty("functions") List<JSONObject> functions,
			@JsonProperty("entityEventConfName") String entityEventConfName) {

		super(timeRangeInSeconds, functions);

		Assert.hasText(entityEventConfName);
		this.entityEventConfName = entityEventConfName;
	}

	@Override
	public String getFactoryName() {
		return ENTITY_EVENT_VALUE_RETRIEVER;
	}

	public String getEntityEventConfName() {
		return entityEventConfName;
	}
}
