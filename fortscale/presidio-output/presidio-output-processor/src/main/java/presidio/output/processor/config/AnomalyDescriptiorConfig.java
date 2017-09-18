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
        "anomalyField",
        "anomalyValue",
        "anomalyFilters"
})
public class AnomalyDescriptiorConfig {

    @JsonProperty("anomalyField")
    private String anomalyField;

    @JsonProperty("anomalyValue")
    private String anomalyValue;

    @JsonProperty("anomalyFilters")
    private AnomalyFiltersConfig anomalyFilters;

    @JsonProperty("anomalyField")
    public String getAnomalyField() {
        return anomalyField;
    }

    @JsonProperty("anomalyField")
    public void setAnomalyField(String anomalyField) {
        this.anomalyField = anomalyField;
    }

    @JsonProperty("anomalyValue")
    public String getAnomalyValue() {
        return anomalyValue;
    }

    @JsonProperty("anomalyValue")
    public void setAnomalyValue(String anomalyValue) {
        this.anomalyValue = anomalyValue;
    }

    @JsonProperty("anomalyFilters")
    public AnomalyFiltersConfig getAnomalyFilters() {
        return anomalyFilters;
    }

    @JsonProperty("anomalyFilters")
    public void setAnomalyFilters(AnomalyFiltersConfig anomalyFilters) {
        this.anomalyFilters = anomalyFilters;
    }
}
