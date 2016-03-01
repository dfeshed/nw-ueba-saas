package fortscale.domain.core;

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

}
