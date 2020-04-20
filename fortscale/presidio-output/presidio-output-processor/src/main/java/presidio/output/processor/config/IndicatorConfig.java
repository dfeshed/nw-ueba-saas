package presidio.output.processor.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import fortscale.common.general.Schema;
import presidio.output.processor.services.alert.indicator.enricher.IndicatorEnricher;

import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "type",
        "name",
        "adeEventType",
        "modelContextFields",
        "splitFields",
        "schema",
        "anomalyDescriptior",
        "historicalData",
        "classification",
        "transformer",
        "enrichers"
})
public class IndicatorConfig {
    public IndicatorConfig() {}

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

    @JsonProperty("splitFields")
    private List<String> splitFields;

    @JsonProperty("schema")
    private Schema schema;

    @JsonProperty("anomalyDescriptior")
    private AnomalyDescriptiorConfig anomalyDescriptior;

    @JsonProperty("historicalData")
    private List<HistoricalDataConfig> historicalData;

    @JsonProperty("classification")
    private String classification;

    @JsonProperty("transformer")
    private String transformer;

    @JsonProperty("enrichers")
    private List<IndicatorEnricher> enrichers = Collections.emptyList();

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
    public List<HistoricalDataConfig> getHistoricalData() {
        return historicalData;
    }

    @JsonProperty("historicalData")
    public void setHistoricalData(List<HistoricalDataConfig> historicalData) {
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

    @JsonProperty("enrichers")
    public List<IndicatorEnricher> getEnrichers() {
        return enrichers;
    }

    @JsonProperty("enrichers")
    public void setEnrichers(List<IndicatorEnricher> enrichers) {
        this.enrichers = enrichers == null ? Collections.emptyList() : enrichers;
    }

    @JsonProperty("modelContextFields")
    public List<String> getModelContextFields() {
        return modelContextFields;
    }

    @JsonProperty("modelContextFields")
    public void setModelContextFields(List<String> modelContextFields) {
        this.modelContextFields = modelContextFields;
    }

    @JsonProperty("splitFields")
    public List<String> getSplitFields() {
        return splitFields;
    }

    @JsonProperty("splitFields")
    public void setSplitFields(List<String> splitFields) {
        this.splitFields = splitFields;
    }
}
