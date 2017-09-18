package presidio.output.processor.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"indicators"})
public class SupportingInformationConfig {

    @JsonProperty("indicators")
    private List<IndicatorConfig> indicators = null;

    @JsonProperty("indicators")
    public List<IndicatorConfig> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<IndicatorConfig> indicators) {
        this.indicators = indicators;
    }

    public IndicatorConfig getIndicatorConfig(String id) {
        return indicators.stream().filter(indicator -> indicator.getId().equals(id)).findFirst().get();
    }

    public IndicatorConfig getIndicatorConfigByClassification(String classification) {
        return indicators.stream().filter(indicator -> indicator.getClassification().equals(classification)).findFirst().get();
    }

    public SupportingInformationConfig(List<IndicatorConfig> indicators) {
        this.indicators = indicators;
    }
}
