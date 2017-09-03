package presidio.webapp.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Alert
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-03T07:32:57.308Z")

public class Alert {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("classifiation")
    private List<String> classifiation = new ArrayList<String>();

    @JsonProperty("startDate")
    private Integer startDate = null;

    @JsonProperty("endDate")
    private Integer endDate = null;

    @JsonProperty("username")
    private String username = null;

    @JsonProperty("indicators")
    private List<Indicator> indicators = new ArrayList<Indicator>();

    @JsonProperty("score")
    private Integer score = null;

    /**
     * Gets or Sets feedback
     */
    public enum FeedbackEnum {
        NONE("None"),

        APPROVED("Approved"),

        REJECTED("Rejected");

        private String value;

        FeedbackEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static FeedbackEnum fromValue(String text) {
            for (FeedbackEnum b : FeedbackEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("feedback")
    private FeedbackEnum feedback = null;

    @JsonProperty("userScoreContribution")
    private BigDecimal userScoreContribution = null;

    @JsonProperty("userScoreContributionFlag")
    private Boolean userScoreContributionFlag = null;

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

    @JsonProperty("feedbackHistory")
    private List<AlertFeedbackHistory> feedbackHistory = new ArrayList<AlertFeedbackHistory>();

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

    public Alert startDate(Integer startDate) {
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

    public Alert endDate(Integer endDate) {
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

    public Alert feedback(FeedbackEnum feedback) {
        this.feedback = feedback;
        return this;
    }

    /**
     * Get feedback
     *
     * @return feedback
     **/
    @ApiModelProperty(value = "")
    public FeedbackEnum getFeedback() {
        return feedback;
    }

    public void setFeedback(FeedbackEnum feedback) {
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

    public Alert userScoreContributionFlag(Boolean userScoreContributionFlag) {
        this.userScoreContributionFlag = userScoreContributionFlag;
        return this;
    }

    /**
     * Get userScoreContributionFlag
     *
     * @return userScoreContributionFlag
     **/
    @ApiModelProperty(value = "")
    public Boolean getUserScoreContributionFlag() {
        return userScoreContributionFlag;
    }

    public void setUserScoreContributionFlag(Boolean userScoreContributionFlag) {
        this.userScoreContributionFlag = userScoreContributionFlag;
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

    public Alert feedbackHistory(List<AlertFeedbackHistory> feedbackHistory) {
        this.feedbackHistory = feedbackHistory;
        return this;
    }

    public Alert addFeedbackHistoryItem(AlertFeedbackHistory feedbackHistoryItem) {
        this.feedbackHistory.add(feedbackHistoryItem);
        return this;
    }

    /**
     * Get feedbackHistory
     *
     * @return feedbackHistory
     **/
    @ApiModelProperty(value = "")
    public List<AlertFeedbackHistory> getFeedbackHistory() {
        return feedbackHistory;
    }

    public void setFeedbackHistory(List<AlertFeedbackHistory> feedbackHistory) {
        this.feedbackHistory = feedbackHistory;
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
                Objects.equals(this.indicators, alert.indicators) &&
                Objects.equals(this.score, alert.score) &&
                Objects.equals(this.feedback, alert.feedback) &&
                Objects.equals(this.userScoreContribution, alert.userScoreContribution) &&
                Objects.equals(this.userScoreContributionFlag, alert.userScoreContributionFlag) &&
                Objects.equals(this.timeframe, alert.timeframe) &&
                Objects.equals(this.feedbackHistory, alert.feedbackHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, classifiation, startDate, endDate, username, indicators, score, feedback, userScoreContribution, userScoreContributionFlag, timeframe, feedbackHistory);
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
        sb.append("    indicators: ").append(toIndentedString(indicators)).append("\n");
        sb.append("    score: ").append(toIndentedString(score)).append("\n");
        sb.append("    feedback: ").append(toIndentedString(feedback)).append("\n");
        sb.append("    userScoreContribution: ").append(toIndentedString(userScoreContribution)).append("\n");
        sb.append("    userScoreContributionFlag: ").append(toIndentedString(userScoreContributionFlag)).append("\n");
        sb.append("    timeframe: ").append(toIndentedString(timeframe)).append("\n");
        sb.append("    feedbackHistory: ").append(toIndentedString(feedbackHistory)).append("\n");
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

