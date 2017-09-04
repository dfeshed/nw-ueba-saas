package presidio.webapp.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * Indicator
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-31T11:27:51.258Z")

public class Indicator {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("startDate")
    private Integer startDate = null;

    @JsonProperty("endDate")
    private Integer endDate = null;

    @JsonProperty("username")
    private String username = null;

    @JsonProperty("anomalyType")
    private String anomalyType = null;

    @JsonProperty("anomalyValue")
    private Object anomalyValue = null;

    @JsonProperty("dataSource")
    private String dataSource = null;

    /**
     * I'm not sure if we need this
     */
    public enum EvidenceTypeEnum {
        ANOMALYAGGREGATEDEVENT("AnomalyAggregatedEvent"),

        SINGLEEVENT("SingleEvent");

        private String value;

        EvidenceTypeEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static EvidenceTypeEnum fromValue(String text) {
            for (EvidenceTypeEnum b : EvidenceTypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("evidenceType")
    private EvidenceTypeEnum evidenceType = null;

    @JsonProperty("score")
    private Integer score = null;

    /**
     * Gets or Sets timeframe
     */
    public enum TimeframeEnum {
        HOURLY("Hourly"),

        DAILY("Daily");

        private String value;

        TimeframeEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static TimeframeEnum fromValue(String text) {
            for (TimeframeEnum b : TimeframeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("timeframe")
    private TimeframeEnum timeframe = null;

    public Indicator id(String id) {
        this.id = id;
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
    @ApiModelProperty(example = "Moshe", value = "")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Indicator startDate(Integer startDate) {
        this.startDate = startDate;
        return this;
    }

    /**
     * Get startDate
     *
     * @return startDate
     **/
    @ApiModelProperty(value = "")
    public Integer getStartDate() {
        return startDate;
    }

    public void setStartDate(Integer startDate) {
        this.startDate = startDate;
    }

    public Indicator endDate(Integer endDate) {
        this.endDate = endDate;
        return this;
    }

    /**
     * Get endDate
     *
     * @return endDate
     **/
    @ApiModelProperty(value = "")
    public Integer getEndDate() {
        return endDate;
    }

    public void setEndDate(Integer endDate) {
        this.endDate = endDate;
    }

    public Indicator username(String username) {
        this.username = username;
        return this;
    }

    /**
     * Get username
     *
     * @return username
     **/
    @ApiModelProperty(value = "")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Indicator anomalyType(String anomalyType) {
        this.anomalyType = anomalyType;
        return this;
    }

    /**
     * Get anomalyType
     *
     * @return anomalyType
     **/
    @ApiModelProperty(value = "")
    public String getAnomalyType() {
        return anomalyType;
    }

    public void setAnomalyType(String anomalyType) {
        this.anomalyType = anomalyType;
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

    public Indicator dataSource(String dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    /**
     * Get dataSource
     *
     * @return dataSource
     **/
    @ApiModelProperty(value = "")
    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Indicator evidenceType(EvidenceTypeEnum evidenceType) {
        this.evidenceType = evidenceType;
        return this;
    }

    /**
     * I'm not sure if we need this
     *
     * @return evidenceType
     **/
    @ApiModelProperty(value = "I'm not sure if we need this")
    public EvidenceTypeEnum getEvidenceType() {
        return evidenceType;
    }

    public void setEvidenceType(EvidenceTypeEnum evidenceType) {
        this.evidenceType = evidenceType;
    }

    public Indicator score(Integer score) {
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
    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Indicator timeframe(TimeframeEnum timeframe) {
        this.timeframe = timeframe;
        return this;
    }

    /**
     * Get timeframe
     *
     * @return timeframe
     **/
    @ApiModelProperty(value = "")
    public TimeframeEnum getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(TimeframeEnum timeframe) {
        this.timeframe = timeframe;
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
                Objects.equals(this.username, indicator.username) &&
                Objects.equals(this.anomalyType, indicator.anomalyType) &&
                Objects.equals(this.anomalyValue, indicator.anomalyValue) &&
                Objects.equals(this.dataSource, indicator.dataSource) &&
                Objects.equals(this.evidenceType, indicator.evidenceType) &&
                Objects.equals(this.score, indicator.score) &&
                Objects.equals(this.timeframe, indicator.timeframe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, startDate, endDate, username, anomalyType, anomalyValue, dataSource, evidenceType, score, timeframe);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Indicator {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
        sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
        sb.append("    username: ").append(toIndentedString(username)).append("\n");
        sb.append("    anomalyType: ").append(toIndentedString(anomalyType)).append("\n");
        sb.append("    anomalyValue: ").append(toIndentedString(anomalyValue)).append("\n");
        sb.append("    dataSource: ").append(toIndentedString(dataSource)).append("\n");
        sb.append("    evidenceType: ").append(toIndentedString(evidenceType)).append("\n");
        sb.append("    score: ").append(toIndentedString(score)).append("\n");
        sb.append("    timeframe: ").append(toIndentedString(timeframe)).append("\n");
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

