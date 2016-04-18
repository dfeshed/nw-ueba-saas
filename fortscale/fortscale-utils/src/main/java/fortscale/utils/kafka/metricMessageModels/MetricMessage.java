package fortscale.utils.kafka.metricMessageModels;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "header",
        "metrics"
})
public class MetricMessage {
    @JsonProperty("header")
    private Header header;
    @JsonProperty("metrics")
    private Metrics metrics;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The Header
     */
    @JsonProperty("header")
    public Header getHeader() {
        return header;
    }

    /**
     *
     * @param header
     * The Header
     */
    @JsonProperty("header")
    public void setHeader(Header header) {
        this.header = header;
    }

    /**
     *
     * @return
     * The Metrics
     */
    @JsonProperty("metrics")
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     *
     * @param metrics
     * The Metrics
     */
    @JsonProperty("metrics")
    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    @JsonCreator
    public MetricMessage(@JsonProperty("header") Header header, @JsonProperty("metrics") Metrics metrics){
        this.header = header;
        this.metrics = metrics;
    }
    public MetricMessage()
    {

    }
}
