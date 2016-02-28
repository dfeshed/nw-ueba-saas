package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection=AnalyticStateChangeEvent.collectionName)
public class AnalyticStateChangeEvent extends AnalyticEvent {

    public static final String toStateField = "toState";

    @Field(toStateField)
    private String toState;

    public String getToState() {
        return toState;
    }

    public void setToState(String toState) {
        this.toState = toState;
    }


    public AnalyticStateChangeEvent() {
    }

    public AnalyticStateChangeEvent(AnalyticStateChangeEvent analyticStateChangeEvent) {
        super(analyticStateChangeEvent);
        this.toState = analyticStateChangeEvent.toState;

    }

    public AnalyticStateChangeEvent(
            @JsonProperty(AnalyticStateChangeEvent.eventTypeField) String eventType,
            @JsonProperty(AnalyticStateChangeEvent.computerIdField) String computerId,
            @JsonProperty(AnalyticStateChangeEvent.tabIdField) String tabId,
            @JsonProperty(AnalyticStateChangeEvent.stateNameField) String stateName,
            @JsonProperty(AnalyticStateChangeEvent.timeStampField) long timeStamp,
            @JsonProperty(AnalyticStateChangeEvent.toStateField) String toState) {
        super(eventType, computerId, tabId, stateName, timeStamp);
        this.toState = toState;
    }
}
