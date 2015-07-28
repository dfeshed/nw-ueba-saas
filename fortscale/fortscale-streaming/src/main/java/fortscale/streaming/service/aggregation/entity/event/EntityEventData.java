package fortscale.streaming.service.aggregation.entity.event;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityEventData {
	public static final String ENTITY_EVENT_NAME_FIELD = "entityEventName";
	public static final String CONTEXT_FIELD = "context";
	public static final String CONTEXT_ID_FIELD = "contextId";
	public static final String START_TIME_FIELD = "startTime";
	public static final String END_TIME_FIELD = "endTime";
	public static final String AGGR_FEATURE_EVENTS_FIELD = "aggrFeatureEvents";
	public static final String FIRING_TIME_IN_SECONDS_FIELD = "firingTimeInSeconds";
	public static final String FIRED_FIELD = "fired";

	@Id
	private String id;

	@Field(ENTITY_EVENT_NAME_FIELD)
	private String entityEventName;
	@Field(CONTEXT_FIELD)
	private Map<String, String> context;
	@Field(CONTEXT_ID_FIELD)
	private String contextId;
	@Field(START_TIME_FIELD)
	private long startTime;
	@Field(END_TIME_FIELD)
	private long endTime;
	@Field(AGGR_FEATURE_EVENTS_FIELD)
	private Set<AggrFeatureEventWrapper> aggrFeatureEvents;
	@Field(FIRING_TIME_IN_SECONDS_FIELD)
	private long firingTimeInSeconds;
	@Field(FIRED_FIELD)
	private boolean fired;

	public EntityEventData(long firingTimeInSeconds, String entityEventName, Map<String, String> context, String contextId, long startTime, long endTime) {
		Assert.isTrue(firingTimeInSeconds >= 0);
		Assert.isTrue(StringUtils.isNotBlank(entityEventName));
		Assert.notEmpty(context);
		Assert.isTrue(StringUtils.isNotBlank(contextId));
		Assert.isTrue(startTime >= 0);
		Assert.isTrue(endTime >= startTime);

		this.entityEventName = entityEventName;
		this.context = context;
		this.contextId = contextId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.aggrFeatureEvents = new HashSet<>();
		this.firingTimeInSeconds = firingTimeInSeconds;
		this.fired = false;
	}

	public String getEntityEventName() {
		return entityEventName;
	}

	public Map<String, String> getContext() {
		return context;
	}

	public String getContextId() {
		return contextId;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void addAggrFeatureEvent(AggrFeatureEventWrapper aggrFeatureEvent) {
		aggrFeatureEvents.add(aggrFeatureEvent);
	}

	public Set<AggrFeatureEventWrapper> getAggrFeatureEvents() {
		return aggrFeatureEvents;
	}

	public long getFiringTimeInSeconds() {
		return firingTimeInSeconds;
	}

	public boolean isFired() {
		return fired;
	}

	public void setFired(boolean fired) {
		this.fired = fired;
	}
}
