package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;
import java.util.Objects;

/**
 * historical data aggregated by weekday
 */
@ApiModel(description = "historical data aggregated by weekday", value = "WeekdayAggregation")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-12T09:35:35.500Z")

public class HistoricalDataWeekdayAggregation extends HistoricalData {
    /**
     * Gets or Sets type
     */
    public enum TypeEnum {
        WeekdayAggregation("WeekdayAggregation");

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static TypeEnum fromValue(String text) {
            for (TypeEnum b : TypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("type")
    private TypeEnum type = null;

    @JsonProperty("contexts")
    private Map<String, String> contexts = null;

    @JsonProperty("buckets")
    private DailyBuckets buckets = null;

    public HistoricalDataWeekdayAggregation type(TypeEnum type) {
        this.type = type;
        return this;
    }

    /**
     * Get type
     *
     * @return type
     **/
    @ApiModelProperty(value = "")
    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    public HistoricalDataWeekdayAggregation buckets(DailyBuckets buckets) {
        this.buckets = buckets;
        return this;
    }

    /**
     * Get contexts
     *
     * @return contexts
     **/
    @ApiModelProperty(value = "")
    public Map<String, String> getContexts() {
        return contexts;
    }

    public void setContexts(Map<String, String> contexts) {
        this.contexts = contexts;
    }

    /**
     * Get buckets
     *
     * @return buckets
     **/
    @ApiModelProperty(value = "")
    public DailyBuckets getBuckets() {
        return buckets;
    }

    public void setBuckets(DailyBuckets buckets) {
        this.buckets = buckets;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HistoricalDataWeekdayAggregation historicalDataWeekdayAggregation = (HistoricalDataWeekdayAggregation) o;
        return Objects.equals(this.type, historicalDataWeekdayAggregation.type) &&
                Objects.equals(this.contexts, historicalDataWeekdayAggregation.contexts) &&
                Objects.equals(this.buckets, historicalDataWeekdayAggregation.buckets) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, buckets, contexts, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class HistoricalDataWeekdayAggregation {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    contexts: ").append(toIndentedString(contexts)).append("\n");
        sb.append("    buckets: ").append(toIndentedString(buckets)).append("\n");
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
