package fortscale.domain.core;

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


}
