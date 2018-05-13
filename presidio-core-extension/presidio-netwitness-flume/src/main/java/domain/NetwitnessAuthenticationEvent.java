package domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.domain.core.AbstractAuditableDocument;

import java.time.Instant;
import java.util.List;

public class NetwitnessAuthenticationEvent extends AbstractAuditableDocument {

    public static final String EVENT_SOURCE_ID_FIELD_NAME = "event_source_id";
    public static final String EVENT_ALIAS_FIELD_NAME = "alias_host";
    public static final String EVENT_SOURCE_FIELD_NAME = "event_source";
    public static final String USER_DEST_FIELD_NAME = "user_dst";
    public static final String EVENT_TYPE_FIELD_NAME = "event_type";
    public static final String EVENT_TIME_FIELD_NAME = "event_time";


    @JsonProperty(EVENT_SOURCE_ID_FIELD_NAME)
    protected String eventId;

    @JsonProperty(EVENT_ALIAS_FIELD_NAME)
    protected String aliasHost;

    @JsonProperty(EVENT_SOURCE_FIELD_NAME)
    protected String eventSource;

    @JsonProperty(USER_DEST_FIELD_NAME)
    protected String userId;

    @JsonProperty(EVENT_TYPE_FIELD_NAME)
    protected String userType;

    @JsonProperty(EVENT_TIME_FIELD_NAME)
    protected Instant eventTime;


    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getAliasHost() {
        return aliasHost;
    }

    public void setAliasHost(String aliasHost) {
        this.aliasHost = aliasHost;
    }

    public String getEventSource() {
        return eventSource;
    }

    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "NetwitnessAuthenticationEvent{" +
                "eventId='" + eventId + '\'' +
                ", aliasHost=" + aliasHost +
                ", eventSource='" + eventSource + '\'' +
                ", userId='" + userId + '\'' +
                ", userType='" + userType + '\'' +
                ", eventTime=" + eventTime +
                '}';
    }
}
