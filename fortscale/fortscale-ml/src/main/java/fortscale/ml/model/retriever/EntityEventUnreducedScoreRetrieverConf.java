package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

public class EntityEventUnreducedScoreRetrieverConf extends AbstractDataRetrieverConf {
	public static final String ENTITY_EVENT_UNREDUCED_SCORE_RETRIEVER = "entity_event_unreduced_score_retriever";

	private String entityEventConfName;
	private int numOfDays;
	private double numOfAlertsPerDay;

	@JsonCreator
	public EntityEventUnreducedScoreRetrieverConf(
			@JsonProperty("functions") List<JSONObject> functions,
			@JsonProperty("entityEventConfName") String entityEventConfName,
			@JsonProperty("numOfDays") Integer numOfDays,
			@JsonProperty("numOfAlertsPerDay") Double numOfAlertsPerDay) {
		super(numOfDays * 60 * 60 * 24, functions);
		Assert.hasText(entityEventConfName);
		Assert.isTrue(numOfAlertsPerDay != null && numOfAlertsPerDay > 0);
		this.entityEventConfName = entityEventConfName;
		this.numOfDays = numOfDays;
		this.numOfAlertsPerDay = numOfAlertsPerDay;
	}

	@Override
	public String getFactoryName() {
		return ENTITY_EVENT_UNREDUCED_SCORE_RETRIEVER;
	}

	public String getEntityEventConfName() {
		return entityEventConfName;
	}

	public int getNumOfDays() {
		return numOfDays;
	}

	public double getNumOfAlertsPerDay() {
		return numOfAlertsPerDay;
	}
}
