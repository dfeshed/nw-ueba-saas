package fortscale.ml.model.selector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

public class EntityEventContextSelectorConf implements IContextSelectorConf {
	public static final String ENTITY_EVENT_CONTEXT_SELECTOR = "entity_event_context_selector";

	private String entityEventConfName;

	@JsonCreator
	public EntityEventContextSelectorConf(
			@JsonProperty("entityEventConfName") String entityEventConfName) {

		Assert.hasText(entityEventConfName);
		this.entityEventConfName = entityEventConfName;
	}

	@Override
	public String getFactoryName() {
		return ENTITY_EVENT_CONTEXT_SELECTOR;
	}

	public String getEntityEventConfName() {
		return entityEventConfName;
	}
}
