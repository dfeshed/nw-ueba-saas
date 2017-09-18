package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonProperty("username")
    private String username = null;

    @JsonProperty("indicatorsName")
    private List<String> indicatorsName = new ArrayList<String>();

    @JsonProperty("indicatorsNum")
    private Integer indicatorsNum = null;

    @JsonProperty("score")
    private Integer score = null;

    @JsonProperty("feedback")
    private Feedback feedback = null;

    @JsonProperty("userScoreContribution")
    private BigDecimal userScoreContribution = null;

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
                if (String.valueOf(b.value).equals(text.toLowerCase())) {
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

    @JsonProperty("userId")
    private String userId = null;

    @JsonProperty("indicators")
    private List<Indicator> indicators = new ArrayList<Indicator>();

    public Alert id(String id) {
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

    public Alert classifiation(List<String> classifiation) {
        this.classifiation = classifiation;
        return this;
    }

    public Alert addClassifiationItem(String classifiationItem) {
        this.classifiation.add(classifiationItem);
        return this;
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

    public Alert startDate(BigDecimal startDate) {
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

    public Alert endDate(BigDecimal endDate) {
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

    public Alert username(String username) {
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

    public Alert indicatorsName(List<String> indicatorsName) {
        this.indicatorsName = indicatorsName;
        return this;
    }

    public Alert addIndicatorsNameItem(String indicatorsNameItem) {
        this.indicatorsName.add(indicatorsNameItem);
        return this;
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

    public Alert indicatorsNum(Integer indicatorsNum) {
        this.indicatorsNum = indicatorsNum;
        return this;
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

    public Alert score(Integer score) {
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

    public Alert feedback(Feedback feedback) {
        this.feedback = feedback;
        return this;
    }

    /**
     * Get feedback
     *
     * @return feedback
     **/
    @ApiModelProperty(value = "")
    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public Alert userScoreContribution(BigDecimal userScoreContribution) {
        this.userScoreContribution = userScoreContribution;
        return this;
    }

    /**
     * Get userScoreContribution
     *
     * @return userScoreContribution
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getUserScoreContribution() {
        return userScoreContribution;
    }

    public void setUserScoreContribution(BigDecimal userScoreContribution) {
        this.userScoreContribution = userScoreContribution;
    }

    public Alert timeframe(TimeframeEnum timeframe) {
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

    public Alert severity(AlertSeverity severity) {
        this.severity = severity;
        return this;
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

    public Alert userId(String userId) {
        this.userId = userId;
        return this;
    }

    /**
     * Get userId
     *
     * @return userId
     **/
    @ApiModelProperty(value = "")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Alert indicators(List<Indicator> indicators) {
        this.indicators = indicators;
        return this;
    }

    public Alert addIndicatorsItem(Indicator indicatorsItem) {
        this.indicators.add(indicatorsItem);
        return this;
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
                Objects.equals(this.username, alert.username) &&
                Objects.equals(this.indicatorsName, alert.indicatorsName) &&
                Objects.equals(this.indicatorsNum, alert.indicatorsNum) &&
                Objects.equals(this.score, alert.score) &&
                Objects.equals(this.feedback, alert.feedback) &&
                Objects.equals(this.userScoreContribution, alert.userScoreContribution) &&
                Objects.equals(this.timeframe, alert.timeframe) &&
                Objects.equals(this.severity, alert.severity) &&
                Objects.equals(this.userId, alert.userId) &&
                Objects.equals(this.indicators, alert.indicators);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, classifiation, startDate, endDate, username, indicatorsName, indicatorsNum, score, feedback, userScoreContribution, timeframe, severity, userId, indicators);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Alert {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    classifiation: ").append(toIndentedString(classifiation)).append("\n");
        sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
        sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
        sb.append("    username: ").append(toIndentedString(username)).append("\n");
        sb.append("    indicatorsName: ").append(toIndentedString(indicatorsName)).append("\n");
        sb.append("    indicatorsNum: ").append(toIndentedString(indicatorsNum)).append("\n");
        sb.append("    score: ").append(toIndentedString(score)).append("\n");
        sb.append("    feedback: ").append(toIndentedString(feedback)).append("\n");
        sb.append("    userScoreContribution: ").append(toIndentedString(userScoreContribution)).append("\n");
        sb.append("    timeframe: ").append(toIndentedString(timeframe)).append("\n");
        sb.append("    severity: ").append(toIndentedString(severity)).append("\n");
        sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
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

