package presidio.output.processor.config;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "fieldName",
        "fieldValue"
})
public class AnomalyFiltersConfig {

    @JsonProperty("fieldName")
    private String fieldName;

    @JsonProperty("fieldValue")
    private String fieldValue;

    @JsonProperty("fieldName")
    public String getFieldName() {
        return fieldName;
    }

    @JsonProperty("fieldName")
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @JsonProperty("fieldValue")
    public String getFieldValue() {
        return fieldValue;
    }

    @JsonProperty("fieldValue")
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
}