package presidio.webapp.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * InlineResponse2001
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-31T11:27:51.258Z")

public class InlineResponse2001 {
    @JsonProperty("keys")
    private List<String> keys = new ArrayList<String>();

    @JsonProperty("value")
    private BigDecimal value = null;

    @JsonProperty("anomaly")
    private Boolean anomaly = null;

    public InlineResponse2001 keys(List<String> keys) {
        this.keys = keys;
        return this;
    }

    public InlineResponse2001 addKeysItem(String keysItem) {
        this.keys.add(keysItem);
        return this;
    }

    /**
     * The keys are can be one or more string, numbers or date, represnting the axis or axes.
     *
     * @return keys
     **/
    @ApiModelProperty(required = true, value = "The keys are can be one or more string, numbers or date, represnting the axis or axes.")
    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public InlineResponse2001 value(BigDecimal value) {
        this.value = value;
        return this;
    }

    /**
     * The value on the graph
     *
     * @return value
     **/
    @ApiModelProperty(value = "The value on the graph")
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public InlineResponse2001 anomaly(Boolean anomaly) {
        this.anomaly = anomaly;
        return this;
    }

    /**
     * Get anomaly
     *
     * @return anomaly
     **/
    @ApiModelProperty(required = true, value = "")
    public Boolean getAnomaly() {
        return anomaly;
    }

    public void setAnomaly(Boolean anomaly) {
        this.anomaly = anomaly;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InlineResponse2001 inlineResponse2001 = (InlineResponse2001) o;
        return Objects.equals(this.keys, inlineResponse2001.keys) &&
                Objects.equals(this.value, inlineResponse2001.value) &&
                Objects.equals(this.anomaly, inlineResponse2001.anomaly);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keys, value, anomaly);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class InlineResponse2001 {\n");

        sb.append("    keys: ").append(toIndentedString(keys)).append("\n");
        sb.append("    value: ").append(toIndentedString(value)).append("\n");
        sb.append("    anomaly: ").append(toIndentedString(anomaly)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

