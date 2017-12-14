package presidio.webapp.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Indicator
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-12T07:33:55.263Z")

public class Indicator {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("startDate")
    private BigDecimal startDate = null;

    @JsonProperty("endDate")
    private BigDecimal endDate = null;

    @JsonProperty("anomalyValue")
    private Object anomalyValue = null;

    @JsonProperty("schema")
    private String schema = null;

    @JsonProperty("scoreContribution")
    private Double scoreContribution = null;

    /**
     * Gets or Sets type
     */
    public enum TypeEnum {
        SCORE_AGGREGATION("SCORE_AGGREGATION"),

        FEATURE_AGGREGATION("FEATURE_AGGREGATION"),

        STATIC_INDICATOR("STATIC_INDICATOR");

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

    @JsonProperty("score")
    private Double score = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("historicalData")
    private HistoricalData historicalData = null;

    @JsonProperty("eventsNum")
    private Integer eventsNum = null;

    public Indicator id(String id) {
        this.id = id;
        return this;
    }


    public void setScoreContribution(Double scoreContribution) {
        this.scoreContribution = scoreContribution;
    }

    public Double getScoreContribution() {
        return scoreContribution;
    }

    public Indicator coreContribution(Double scoreContribution) {
        this.scoreContribution = scoreContribution;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     **/
    @ApiModelProperty(example = "d290f1ee-6c54-4b01-90e6-d701748f0851", required = true, value = "")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Indicator name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     *
     * @return name
     **/
    @ApiModelProperty(example = "high_number_of_distinct_folders_opened_attempts", value = "")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Indicator startDate(BigDecimal startDate) {
        this.startDate = startDate;
        return this;
    }

    /**
     * Get startDate
     *
     * @return startDate
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getStartDate() {
        return startDate;
    }

    public void setStartDate(BigDecimal startDate) {
        this.startDate = startDate;
    }

    public Indicator endDate(BigDecimal endDate) {
        this.endDate = endDate;
        return this;
    }

    /**
     * Get endDate
     *
     * @return endDate
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getEndDate() {
        return endDate;
    }

    public void setEndDate(BigDecimal endDate) {
        this.endDate = endDate;
    }

    public Indicator anomalyValue(Object anomalyValue) {
        this.anomalyValue = anomalyValue;
        return this;
    }

    /**
     * Can be any type- number, string, ip address
     *
     * @return anomalyValue
     **/
    @ApiModelProperty(value = "Can be any type- number, string, ip address")
    public Object getAnomalyValue() {
        return anomalyValue;
    }

    public void setAnomalyValue(Object anomalyValue) {
        this.anomalyValue = anomalyValue;
    }

    public Indicator schema(String schema) {
        this.schema = schema;
        return this;
    }

    /**
     * Get schema
     *
     * @return schema
     **/
    @ApiModelProperty(value = "")
    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Indicator type(TypeEnum type) {
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

    public Indicator score(Double score) {
        this.score = score;
        return this;
    }

    /**
     * Get score
     * minimum: 0
     * maximum: 100.0
     *
     * @return score
     **/
    @ApiModelProperty(value = "")
    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Indicator historicalData(HistoricalData historicalData) {
        this.historicalData = historicalData;
        return this;
    }

    /**
     * Get historicalData
     *
     * @return historicalData
     **/
    @ApiModelProperty(value = "")
    public HistoricalData getHistoricalData() {
        return historicalData;
    }

    public void setHistoricalData(HistoricalData historicalData) {
        this.historicalData = historicalData;
    }

    public Indicator eventsNum(Integer eventsNum) {
        this.eventsNum = eventsNum;
        return this;
    }

    /**
     * Get eventsNum
     *
     * @return eventsNum
     **/
    @ApiModelProperty(value = "")
    public Integer getEventsNum() {
        return eventsNum;
    }

    public void setEventsNum(Integer eventsNum) {
        this.eventsNum = eventsNum;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Indicator indicator = (Indicator) o;
        return Objects.equals(this.id, indicator.id) &&
                Objects.equals(this.name, indicator.name) &&
                Objects.equals(this.startDate, indicator.startDate) &&
                Objects.equals(this.endDate, indicator.endDate) &&
                Objects.equals(this.anomalyValue, indicator.anomalyValue) &&
                Objects.equals(this.schema, indicator.schema) &&
                Objects.equals(this.type, indicator.type) &&
                Objects.equals(this.score, indicator.score) &&
                Objects.equals(this.historicalData, indicator.historicalData) &&
                Objects.equals(this.eventsNum, indicator.eventsNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, startDate, endDate, anomalyValue, schema, type, score, historicalData, eventsNum);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Indicator {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
        sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
        sb.append("    anomalyValue: ").append(toIndentedString(anomalyValue)).append("\n");
        sb.append("    schema: ").append(toIndentedString(schema)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    score: ").append(toIndentedString(score)).append("\n");
        sb.append("    historicalData: ").append(toIndentedString(historicalData)).append("\n");
        sb.append("    eventsNum: ").append(toIndentedString(eventsNum)).append("\n");
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


