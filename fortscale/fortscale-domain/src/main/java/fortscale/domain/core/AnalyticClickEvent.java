package fortscale.domain.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection=AnalyticClickEvent.collectionName)
public class AnalyticClickEvent extends AnalyticEvent {

    public static final String elementSelectorField = "elementSelector";

    @Field(elementSelectorField)
    private String elementSelector;

    public String getElementSelector() {
        return elementSelector;
    }

    public void setElementSelector(String elementSelector) {
        this.elementSelector = elementSelector;
    }

    public AnalyticClickEvent () {}

    public AnalyticClickEvent (AnalyticClickEvent analyticClickEvent) {
        super(analyticClickEvent);
        this.elementSelector = analyticClickEvent.elementSelector;
    }

    public AnalyticClickEvent (
            @JsonProperty(AnalyticClickEvent.eventTypeField) String eventType,
            @JsonProperty(AnalyticClickEvent.computerIdField) String computerId,
            @JsonProperty(AnalyticClickEvent.tabIdField) String tabId,
            @JsonProperty(AnalyticClickEvent.stateNameField) String stateName,
            @JsonProperty(AnalyticClickEvent.timeStampField) long timeStamp,
            @JsonProperty(AnalyticClickEvent.elementSelectorField) String elementSelector) {
        super(eventType, computerId, tabId, stateName, timeStamp);
        this.elementSelector = elementSelector;
    }
}
