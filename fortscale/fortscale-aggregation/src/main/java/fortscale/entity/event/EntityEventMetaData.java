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
    public static final String CONTEXT_ID_FIELD = "contextId";
    public static final String START_TIME_FIELD = "startTime";
    public static final String END_TIME_FIELD = "endTime";
    public static final String CREATED_AT_EPOCHTIME_FIELD = "createdAtEpochtime";

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


    public EntityEventMetaData() {
        long currentTimeMillis = System.currentTimeMillis();
        this.createdAtEpochtime = TimestampUtils.convertToSeconds(currentTimeMillis);
    }

    public EntityEventMetaData(EntityEventData entityEventData) {
        this();

        this.entityEventName = entityEventData.getEntityEventName();
        this.contextId = entityEventData.getContextId();
        this.startTime = entityEventData.getStartTime();
        this.endTime = entityEventData.getEndTime();
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

    public long getCreatedAtEpochtime() { return createdAtEpochtime; }

    public String getId() {
        return id;
    }
}
