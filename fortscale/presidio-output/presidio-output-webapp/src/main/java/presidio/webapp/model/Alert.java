package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;
import presidio.webapp.model.AlertQueryEnums.AlertSeverity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Alert
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-12T09:35:35.500Z")

public class Alert {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("classifiation")
    private List<String> classifiation = new ArrayList<String>();

    @JsonProperty("startDate")
    private BigDecimal startDate = null;

    @JsonProperty("endDate")
    private BigDecimal endDate = null;

    @JsonProperty("entityName")
    private String entityName = null;

    @JsonProperty("indicatorsName")
    private List<String> indicatorsName = new ArrayList<String>();

    @JsonProperty("indicatorsNum")
    private Integer indicatorsNum = null;

    @JsonProperty("score")
    private Integer score = null;

    @JsonProperty("feedback")
    private AlertQueryEnums.AlertFeedback feedback = null;

    @JsonProperty("entityScoreContribution")
    private BigDecimal entityScoreContribution = null;

    /**
     * Gets or Sets timeframe
     */
    public enum TimeframeEnum {
        HOURLY("hourly"),

        DAILY("daily");

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

    @JsonProperty("severity")
    private AlertSeverity severity = null;

    @JsonProperty("entityDocumentId")
    private String entityDocumentId = null;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonProperty("indicators")
    private List<Indicator> indicators = new ArrayList<Indicator>();

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

    /**
     * Get classifiation
     *
     * @return classifiation
     **/
    @ApiModelProperty(value = "")
    public List<String> getClassifiation() {
        return classifiation;
    }

    public void setClassifiation(List<String> classifiation) {
        this.classifiation = classifiation;
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

    /**
     * Get entityName
     *
     * @return entityName
     **/
    @ApiModelProperty(value = "")
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * Get indicatorsName
     *
     * @return indicatorsName
     **/
    @ApiModelProperty(value = "")
    public List<String> getIndicatorsName() {
        return indicatorsName;
    }

    public void setIndicatorsName(List<String> indicatorsName) {
        this.indicatorsName = indicatorsName;
    }

    /**
     * Get indicatorsNum
     *
     * @return indicatorsNum
     **/
    @ApiModelProperty(value = "")
    public Integer getIndicatorsNum() {
        return indicatorsNum;
    }

    public void setIndicatorsNum(Integer indicatorsNum) {
        this.indicatorsNum = indicatorsNum;
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

    /**
     * Get feedback
     *
     * @return feedback
     **/
    @ApiModelProperty(value = "")
    public AlertQueryEnums.AlertFeedback getFeedback() {
        return feedback;
    }

    public void setFeedback(AlertQueryEnums.AlertFeedback feedback) {
        this.feedback = feedback;
    }

    /**
     * Get entityScoreContribution
     *
     * @return entityScoreContribution
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getEntityScoreContribution() {
        return entityScoreContribution;
    }

    public void setEntityScoreContribution(BigDecimal entityScoreContribution) {
        this.entityScoreContribution = entityScoreContribution;
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

    /**
     * Get severity
     *
     * @return severity
     **/
    @ApiModelProperty(value = "")
    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }

    /**
     * Get entityDocumentId
     *
     * @return entityDocumentId
     **/
    @ApiModelProperty(value = "")
    public String getEntityDocumentId() {
        return entityDocumentId;
    }

    public void setEntityDocumentId(String entityDocumentId) {
        this.entityDocumentId = entityDocumentId;
    }

    /**
     * Get indicators
     *
     * @return indicators
     **/
    @ApiModelProperty(value = "")
    public List<Indicator> getIndicators() {
        return indicators;
    }

    public void setIndicators(List<Indicator> indicators) {
        this.indicators = indicators;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Alert alert = (Alert) o;
        return Objects.equals(this.id, alert.id) &&
                Objects.equals(this.classifiation, alert.classifiation) &&
                Objects.equals(this.startDate, alert.startDate) &&
                Objects.equals(this.endDate, alert.endDate) &&
                Objects.equals(this.entityName, alert.entityName) &&
                Objects.equals(this.indicatorsName, alert.indicatorsName) &&
                Objects.equals(this.indicatorsNum, alert.indicatorsNum) &&
                Objects.equals(this.score, alert.score) &&
                Objects.equals(this.feedback, alert.feedback) &&
                Objects.equals(this.entityScoreContribution, alert.entityScoreContribution) &&
                Objects.equals(this.timeframe, alert.timeframe) &&
                Objects.equals(this.severity, alert.severity) &&
                Objects.equals(this.entityDocumentId, alert.entityDocumentId) &&
                Objects.equals(this.indicators, alert.indicators);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, classifiation, startDate, endDate, entityName, indicatorsName, indicatorsNum, score, feedback, entityScoreContribution, timeframe, severity, entityDocumentId, indicators);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Alert {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    classifiation: ").append(toIndentedString(classifiation)).append("\n");
        sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
        sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
        sb.append("    entityName: ").append(toIndentedString(entityName)).append("\n");
        sb.append("    indicatorsName: ").append(toIndentedString(indicatorsName)).append("\n");
        sb.append("    indicatorsNum: ").append(toIndentedString(indicatorsNum)).append("\n");
        sb.append("    score: ").append(toIndentedString(score)).append("\n");
        sb.append("    feedback: ").append(toIndentedString(feedback)).append("\n");
        sb.append("    entityScoreContribution: ").append(toIndentedString(entityScoreContribution)).append("\n");
        sb.append("    timeframe: ").append(toIndentedString(timeframe)).append("\n");
        sb.append("    severity: ").append(toIndentedString(severity)).append("\n");
        sb.append("    entityDocumentId: ").append(toIndentedString(entityDocumentId)).append("\n");
        sb.append("    indicators: ").append(toIndentedString(indicators)).append("\n");
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

