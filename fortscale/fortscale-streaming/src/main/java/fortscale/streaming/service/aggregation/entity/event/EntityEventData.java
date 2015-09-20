package fortscale.streaming.service.aggregation.entity.event;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.utils.time.TimestampUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityEventData {
	public static final String ENTITY_EVENT_NAME_FIELD = "entityEventName";
	public static final String CONTEXT_FIELD = "context";
	public static final String CONTEXT_ID_FIELD = "contextId";
	public static final String START_TIME_FIELD = "startTime";
	public static final String END_TIME_FIELD = "endTime";
	public static final String NOT_INCLUDED_AGGR_FEATURE_EVENTS_FIELD = "notIncludedAggrFeatureEvents";
	public static final String INCLUDED_AGGR_FEATURE_EVENTS_FIELD = "includedAggrFeatureEvents";
	public static final String CREATED_AT_EPOCHTIME_FIELD = "createdAtEpochtime";
	public static final String MODIFIED_AT_EPOCHTIME_FIELD = "modifiedAtEpochtime";
	public static final String SECONDS_TO_WAIT_BEFORE_TRANSMISSION_FIELD = "secondsToWaitBeforeTransmission";
	public static final String TRANSMISSION_EPOCHTIME_FIELD = "transmissionEpochtime";
	public static final String TRANSMISSION_DATE_FIELD = "transmissionDate";
	public static final String TRANSMITTED_FIELD = "transmitted";

	@SuppressWarnings("UnusedDeclaration")
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
	@Field(NOT_INCLUDED_AGGR_FEATURE_EVENTS_FIELD)
	private Set<AggrEvent> notIncludedAggrFeatureEvents;
	@Field(INCLUDED_AGGR_FEATURE_EVENTS_FIELD)
	private Set<AggrEvent> includedAggrFeatureEvents;
	@Field(CREATED_AT_EPOCHTIME_FIELD)
	private long createdAtEpochtime;
	@Field(MODIFIED_AT_EPOCHTIME_FIELD)
	private long modifiedAtEpochtime;
	@Field(SECONDS_TO_WAIT_BEFORE_TRANSMISSION_FIELD)
	private long secondsToWaitBeforeTransmission;
	@Field(TRANSMISSION_EPOCHTIME_FIELD)
	private long transmissionEpochtime;
	// 365 * 24 * 60 * 60 = 31536000 = 1 year
	@Indexed(unique = false, expireAfterSeconds = 31536000)
	@Field(TRANSMISSION_DATE_FIELD)
	private Date transmissionDate;
	@Field(TRANSMITTED_FIELD)
	private boolean transmitted;

	public EntityEventData(String entityEventName, Map<String, String> context, String contextId, long startTime, long endTime, long secondsToWaitBeforeTransmission) {
		Assert.hasText(entityEventName);
		Assert.notEmpty(context);
		Assert.hasText(contextId);
		Assert.isTrue(startTime >= 0);
		Assert.isTrue(endTime >= startTime);
		Assert.isTrue(secondsToWaitBeforeTransmission >= 0);

		this.entityEventName = entityEventName;
		this.context = context;
		this.contextId = contextId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.notIncludedAggrFeatureEvents = new HashSet<>();
		this.includedAggrFeatureEvents = new HashSet<>();
		this.secondsToWaitBeforeTransmission = secondsToWaitBeforeTransmission;
		this.transmitted = false;

		long currentTimeMillis = System.currentTimeMillis();
		this.createdAtEpochtime = TimestampUtils.convertToSeconds(currentTimeMillis);
		afterModification(currentTimeMillis);
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

	public void addAggrFeatureEvent(AggrEvent aggrFeatureEvent) {
		if (isTransmitted()) {
			notIncludedAggrFeatureEvents.add(aggrFeatureEvent);
		} else {
			includedAggrFeatureEvents.add(aggrFeatureEvent);
		}

		afterModification(System.currentTimeMillis());
	}

	public Set<AggrEvent> getNotIncludedAggrFeatureEvents() {
		return notIncludedAggrFeatureEvents;
	}

	public Set<AggrEvent> getIncludedAggrFeatureEvents() {
		return includedAggrFeatureEvents;
	}

	public long getCreatedAtEpochtime() {
		return createdAtEpochtime;
	}

	public long getModifiedAtEpochtime() {
		return modifiedAtEpochtime;
	}

	public long getTransmissionEpochtime() {
		return transmissionEpochtime;
	}

	public Date getTransmissionDate() {
		return transmissionDate;
	}

	public boolean isTransmitted() {
		return transmitted;
	}

	public void setTransmitted(boolean transmitted) {
		this.transmitted = transmitted;
	}

	private void afterModification(long currentTimeMillis) {
		modifiedAtEpochtime = TimestampUtils.convertToSeconds(currentTimeMillis);

		if (!isTransmitted()) {
			transmissionEpochtime = modifiedAtEpochtime + secondsToWaitBeforeTransmission;
			transmissionDate = new Date(TimestampUtils.convertToMilliSeconds(transmissionEpochtime));
		}
	}
}
