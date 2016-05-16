package fortscale.utils.samza.metricMessageModels;

/**
 * Created by baraks on 4/13/2016.
 */

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Metrics implements Serializable {

    public Metrics() {

    }


    @JsonIgnore
    private Map<String, Map<String, Object>> additionalProperties = new HashMap<String, Map<String, Object>>();

    @JsonAnyGetter
    public Map<String, Map<String, Object>> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Map<String, Object> value) {
        this.additionalProperties.put(name, value);
    }

    @JsonCreator
    public Metrics(Map<String, Map<String, Object>> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}