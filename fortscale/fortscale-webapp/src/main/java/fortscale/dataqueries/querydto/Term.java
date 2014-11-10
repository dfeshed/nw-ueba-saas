package fortscale.dataqueries.querydto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
* Created by Yossi on 10/11/2014.
*/
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConditionTerm.class, name = "term"),
        @JsonSubTypes.Type(value = ConditionField.class, name = "field") })
public abstract class Term {
    @JsonProperty("operator")
    Enum operator;
}
