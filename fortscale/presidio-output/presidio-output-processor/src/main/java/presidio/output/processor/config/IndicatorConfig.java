package presidio.output.processor.config;


import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fortscale.common.general.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "type",
        "name",
        "adeEventType",
        "schema",
        "anomalyDescriptior",
        "historicalData"
})
public class IndicatorConfig {

    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("ade_event_type")
    private String adeEventType;

    @JsonProperty("schema")
    private Schema schema;

    @JsonProperty("anomalyDescriptior")
    private AnomalyDescriptiorConfig anomalyDescriptior;

    @JsonProperty("historicalData")
    private HistoricalDataConfig historicalData;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("adeEventType")
    public String getAdeEventType() {
        return adeEventType;
    }

    @JsonProperty("anomalyDescriptior")
    public AnomalyDescriptiorConfig getAnomalyDescriptior() {
        return anomalyDescriptior;
    }

    @JsonProperty("anomalyDescriptior")
    public void setAnomalyDescriptior(AnomalyDescriptiorConfig anomalyDescriptior) {
        this.anomalyDescriptior = anomalyDescriptior;
    }

    @JsonProperty("historicalData")
    public HistoricalDataConfig getHistoricalData() {
        return historicalData;
    }

    @JsonProperty("historicalData")
    public void setHistoricalData(HistoricalDataConfig historicalData) {
        this.historicalData = historicalData;
    }

    @JsonProperty("schema")
    public Schema getSchema() {
        return schema;
    }

    @JsonProperty("schema")
    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    @JsonProperty("adeEventType")
    public void setAdeEventType(String adeEventType) {
        this.adeEventType = adeEventType;
    }



}