package presidio.output.processor.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "type",
        "name",
        "adeEventType",
        "modelContextFields",
        "schema",
        "anomalyDescriptior",
        "historicalData",
        "classification",
        "transformer"
})
public class IndicatorConfig {

    public IndicatorConfig() {
        this.modelContextFields = Arrays.asList(CommonStrings.CONTEXT_USERID);
    }

    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("ade_event_type")
    private String adeEventType;

    @JsonProperty("modelContextFields")
    private List<String> modelContextFields;

    @JsonProperty("schema")
    private Schema schema;

    @JsonProperty("anomalyDescriptior")
    private AnomalyDescriptiorConfig anomalyDescriptior;

    @JsonProperty("historicalData")
    private HistoricalDataConfig historicalData;

    @JsonProperty("classification")
    private String classification;

    @JsonProperty("transformer")
    private String transformer;

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

    @JsonProperty("classification")
    public String getClassification() {
        return classification;
    }

    @JsonProperty("classification")
    public void setClassification(String classification) {
        this.classification = classification;
    }

    @JsonProperty("transformer")
    public String getTransformer() {
        return transformer;
    }
    @JsonProperty("transformer")
    public void setTransformer(String transformer) {
        this.transformer = transformer;
    }

    @JsonProperty("modelContextFields")
    public List<String> getModelContextFields() {
        return modelContextFields;
    }

    @JsonProperty("modelContextFields")
    public void setModelContextFields(List<String> modelContextFields) {
        this.modelContextFields = modelContextFields;
    }
}