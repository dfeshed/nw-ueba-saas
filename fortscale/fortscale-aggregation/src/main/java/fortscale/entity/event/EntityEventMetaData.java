package fortscale.entity.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.entity.event.EntityEventData;
import fortscale.utils.time.TimestampUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;




@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class EntityEventMetaData {
    public static final String ENTITY_EVENT_NAME_FIELD = "entityEventName";
    public static final String CONTEXT_FIELD = "context";
    public static final String CONTEXT_ID_FIELD = "contextId";
    public static final String START_TIME_FIELD = "startTime";
    public static final String END_TIME_FIELD = "endTime";
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
    @Field(CONTEXT_ID_FIELD)
    private String contextId;
    @Field(START_TIME_FIELD)
    private long startTime;
    @Field(END_TIME_FIELD)
    private long endTime;
    @Field(CREATED_AT_EPOCHTIME_FIELD)
    private long createdAtEpochtime;
    @Field(MODIFIED_AT_EPOCHTIME_FIELD)
    private long modifiedAtEpochtime;
    @Field(TRANSMISSION_EPOCHTIME_FIELD)
    private long transmissionEpochtime;
    @Field(TRANSMITTED_FIELD)
    private boolean transmitted;

    public EntityEventMetaData() {
        long currentTimeMillis = System.currentTimeMillis();
        this.createdAtEpochtime = TimestampUtils.convertToSeconds(currentTimeMillis);
        this.modifiedAtEpochtime = this.createdAtEpochtime;

        this.transmissionEpochtime = -1;
        this.transmitted = false;
    }

    public EntityEventMetaData(EntityEventData entityEventData) {
        this();

        this.entityEventName = entityEventData.getEntityEventName();
        this.contextId = entityEventData.getContextId();
        this.startTime = entityEventData.getStartTime();
        this.endTime = entityEventData.getEndTime();
        this.modifiedAtEpochtime = entityEventData.getModifiedAtEpochtime();
        this.transmissionEpochtime = entityEventData.getTransmissionEpochtime();
        this.transmitted = entityEventData.isTransmitted();
    }

    public String getEntityEventName() {
        return entityEventName;
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
