package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection=AnalyticEvent.collectionName)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class AnalyticEvent extends AbstractDocument {
    public static final String collectionName = "analytic_event";
    public static final String localIdField = "localId";
    public static final String localIdJSON = "id";
    public static final String eventTypeField = "eventType";
    public static final String computerIdField = "computerId";
    public static final String tabIdField = "tabId";
    public static final String stateNameField = "stateName";
    public static final String timeStampField = "timeStamp";

    @Field(localIdField)
    private long localId;
    @Field(eventTypeField)
    private String eventType;
    @Field(computerIdField)
    private String computerId;
    @Field(tabIdField)
    private String tabId;
    @Field(stateNameField)
    private String stateName;
    @Indexed(expireAfterSeconds = 60 * 60 * 24 * 30 * 6) // 6 Months retention
    @Field(timeStampField)
    private long timeStamp;

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getComputerId() {
        return computerId;
    }

    public void setComputerId(String computerId) {
        this.computerId = computerId;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    protected AnalyticEvent() {}

    protected AnalyticEvent(AnalyticEvent analyticEvent) {
        this.localId = analyticEvent.localId;
        this.eventType = analyticEvent.eventType;
        this.computerId = analyticEvent.computerId;
        this.tabId = analyticEvent.tabId;
        this.stateName = analyticEvent.stateName;
        this.timeStamp = analyticEvent.timeStamp;
    }
    protected AnalyticEvent(
            @JsonProperty(AnalyticEvent.localIdJSON) long localId,
            @JsonProperty(AnalyticEvent.eventTypeField) String eventType,
            @JsonProperty(AnalyticEvent.computerIdField) String computerId,
            @JsonProperty(AnalyticEvent.tabIdField) String tabId,
            @JsonProperty(AnalyticEvent.stateNameField) String stateName,
            @JsonProperty(AnalyticEvent.timeStampField) long timeStamp) {
        this.localId = localId;
        this.eventType = eventType;
        this.computerId = computerId;
        this.tabId = tabId;
        this.stateName = stateName;
        this.timeStamp = timeStamp;
    }

}
