
package fortscale.utils.monitoring.stats.models.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "version",
    "metricGroups"
})
public class EngineData {

    @JsonProperty("version")
    private Long version;
    @JsonProperty("metricGroups")
    private List<MetricGroup> metricGroups = new ArrayList<MetricGroup>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public EngineData() {
    }

    /**
     * 
     * @param metricGroups
     * @param version
     */
    public EngineData(Long version, List<MetricGroup> metricGroups) {
        this.version = version;
        this.metricGroups = metricGroups;
    }

    /**
     * 
     * @return
     *     The version
     */
    @JsonProperty("version")
    public Long getVersion() {
        return version;
    }

    /**
     * 
     * @param version
     *     The version
     */
    @JsonProperty("version")
    public void setVersion(Long version) {
        this.version = version;
    }

    public EngineData withVersion(Long version) {
        this.version = version;
        return this;
    }

    /**
     * 
     * @return
     *     The metricGroups
     */
    @JsonProperty("metricGroups")
    public List<MetricGroup> getMetricGroups() {
        return metricGroups;
    }

    /**
     * 
     * @param metricGroups
     *     The metricGroups
     */
    @JsonProperty("metricGroups")
    public void setMetricGroups(List<MetricGroup> metricGroups) {
        this.metricGroups = metricGroups;
    }

    public EngineData withMetricGroups(List<MetricGroup> metricGroups) {
        this.metricGroups = metricGroups;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public EngineData withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
