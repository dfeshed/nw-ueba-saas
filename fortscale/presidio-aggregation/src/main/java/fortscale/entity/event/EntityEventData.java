package fortscale.entity.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.utils.time.TimestampUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
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
	public static final String MODIFIED_AT_DATE_FIELD = "modifiedAtDate";
	public static final String TRANSMISSION_EPOCHTIME_FIELD = "transmissionEpochtime";
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
	@SuppressWarnings("unused")
	@Field(MODIFIED_AT_DATE_FIELD)
	private Date modifiedAtDate;
	@Field(TRANSMISSION_EPOCHTIME_FIELD)
	private long transmissionEpochtime;
	@Field(TRANSMITTED_FIELD)
	private boolean transmitted;

	public EntityEventData() {
		this.notIncludedAggrFeatureEvents = new HashSet<>();
		this.includedAggrFeatureEvents = new HashSet<>();

		long currentTimeMillis = System.currentTimeMillis();
		this.createdAtEpochtime = TimestampUtils.convertToSeconds(currentTimeMillis);
		this.modifiedAtEpochtime = this.createdAtEpochtime;
		this.modifiedAtDate = new Date(currentTimeMillis);

		this.transmissionEpochtime = -1;
		this.transmitted = false;
	}

	public EntityEventData(String entityEventName, Map<String, String> context, String contextId, long startTime, long endTime) {
		this();

		Assert.hasText(entityEventName);
		Assert.notEmpty(context);
		Assert.hasText(contextId);
		Assert.isTrue(startTime >= 0);
		Assert.isTrue(endTime >= startTime);

		this.entityEventName = entityEventName;
		this.context = context;
		this.contextId = contextId;
		this.startTime = startTime;
		this.endTime = endTime;
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

		long currentTimeMillis = System.currentTimeMillis();
		modifiedAtEpochtime = TimestampUtils.convertToSeconds(currentTimeMillis);
		modifiedAtDate = new Date(currentTimeMillis);
	}

	public Set<AggrEvent> getNotIncludedAggrFeatureEvents() {
		return notIncludedAggrFeatureEvents;
	}

	public Set<AggrEvent> getIncludedAggrFeatureEvents() {
		return includedAggrFeatureEvents;
	}

	public long getModifiedAtEpochtime() {
		return modifiedAtEpochtime;
	}

	public long getTransmissionEpochtime() {
		return transmissionEpochtime;
	}

	public void setTransmissionEpochtime(long transmissionEpochtime) {
		this.transmissionEpochtime = transmissionEpochtime;
	}

	public boolean isTransmitted() {
		return transmitted;
	}

	public void setTransmitted(boolean transmitted) {
		this.transmitted = transmitted;
	}

	public String getId() {
		return id;
	}
}
