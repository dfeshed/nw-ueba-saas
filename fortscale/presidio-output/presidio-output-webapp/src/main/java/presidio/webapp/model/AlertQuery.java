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
 * AlertQuery
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-06T11:48:16.912Z")

public class AlertQuery {
    @JsonProperty("pageSize")
    private Integer pageSize = null;

    @JsonProperty("pageNumber")
    private Integer pageNumber = null;

    @JsonProperty("minScore")
    private Integer minScore = null;

    @JsonProperty("maxScore")
    private Integer maxScore = null;

    @JsonProperty("startTimeFrom")
    private BigDecimal startTimeFrom = null;

    @JsonProperty("startTimeTo")
    private BigDecimal startTimeTo = null;

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
    private List<FeedbackEnum> feedback = new ArrayList<FeedbackEnum>();

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<String>();

    @JsonProperty("userName")
    private List<String> userName = new ArrayList<String>();

    @JsonProperty("classification")
    private List<String> classification = new ArrayList<String>();

    @JsonProperty("indicatorsType")
    private List<String> indicatorsType = new ArrayList<String>();

    @JsonProperty("usersId")
    private List<String> usersId = new ArrayList<String>();

    @JsonProperty("sort")
    private List<String> sort = new ArrayList<String>();

    /**
     * Gets or Sets severity
     */
    public enum SeverityEnum {
        CRITICAL("CRITICAL"),

        HIGH("HIGH"),

        MEDIUM("MEDIUM"),

        LOW("LOW");

        private String value;

        SeverityEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static SeverityEnum fromValue(String text) {
            for (SeverityEnum b : SeverityEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("severity")
    private List<SeverityEnum> severity = new ArrayList<SeverityEnum>();

    public AlertQuery pageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Get pageSize
     *
     * @return pageSize
     **/
    @ApiModelProperty(value = "")
    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public AlertQuery pageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    /**
     * Get pageNumber
     *
     * @return pageNumber
     **/
    @ApiModelProperty(value = "")
    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public AlertQuery minScore(Integer minScore) {
        this.minScore = minScore;
        return this;
    }

    /**
     * Get minScore
     *
     * @return minScore
     **/
    @ApiModelProperty(value = "")
    public Integer getMinScore() {
        return minScore;
    }

    public void setMinScore(Integer minScore) {
        this.minScore = minScore;
    }

    public AlertQuery maxScore(Integer maxScore) {
        this.maxScore = maxScore;
        return this;
    }

    /**
     * Get maxScore
     *
     * @return maxScore
     **/
    @ApiModelProperty(value = "")
    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public AlertQuery startTimeFrom(BigDecimal startTimeFrom) {
        this.startTimeFrom = startTimeFrom;
        return this;
    }

    /**
     * Get startTimeFrom
     *
     * @return startTimeFrom
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getStartTimeFrom() {
        return startTimeFrom;
    }

    public void setStartTimeFrom(BigDecimal startTimeFrom) {
        this.startTimeFrom = startTimeFrom;
    }

    public AlertQuery startTimeTo(BigDecimal startTimeTo) {
        this.startTimeTo = startTimeTo;
        return this;
    }

    /**
     * Get startTimeTo
     *
     * @return startTimeTo
     **/
    @ApiModelProperty(value = "")
    public BigDecimal getStartTimeTo() {
        return startTimeTo;
    }

    public void setStartTimeTo(BigDecimal startTimeTo) {
        this.startTimeTo = startTimeTo;
    }

    public AlertQuery feedback(List<FeedbackEnum> feedback) {
        this.feedback = feedback;
        return this;
    }

    public AlertQuery addFeedbackItem(FeedbackEnum feedbackItem) {
        this.feedback.add(feedbackItem);
        return this;
    }

    /**
     * Get feedback
     *
     * @return feedback
     **/
    @ApiModelProperty(value = "")
    public List<FeedbackEnum> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<FeedbackEnum> feedback) {
        this.feedback = feedback;
    }

    public AlertQuery tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public AlertQuery addTagsItem(String tagsItem) {
        this.tags.add(tagsItem);
        return this;
    }

    /**
     * Get tags
     *
     * @return tags
     **/
    @ApiModelProperty(value = "")
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public AlertQuery userName(List<String> userName) {
        this.userName = userName;
        return this;
    }

    public AlertQuery addUserNameItem(String userNameItem) {
        this.userName.add(userNameItem);
        return this;
    }

    /**
     * Get userName
     *
     * @return userName
     **/
    @ApiModelProperty(value = "")
    public List<String> getUserName() {
        return userName;
    }

    public void setUserName(List<String> userName) {
        this.userName = userName;
    }

    public AlertQuery classification(List<String> classification) {
        this.classification = classification;
        return this;
    }

    public AlertQuery addClassificationItem(String classificationItem) {
        this.classification.add(classificationItem);
        return this;
    }

    /**
     * Get classification
     *
     * @return classification
     **/
    @ApiModelProperty(value = "")
    public List<String> getClassification() {
        return classification;
    }

    public void setClassification(List<String> classification) {
        this.classification = classification;
    }

    public AlertQuery indicatorsType(List<String> indicatorsType) {
        this.indicatorsType = indicatorsType;
        return this;
    }

    public AlertQuery addIndicatorsTypeItem(String indicatorsTypeItem) {
        this.indicatorsType.add(indicatorsTypeItem);
        return this;
    }

    /**
     * Get indicatorsType
     *
     * @return indicatorsType
     **/
    @ApiModelProperty(value = "")
    public List<String> getIndicatorsType() {
        return indicatorsType;
    }

    public void setIndicatorsType(List<String> indicatorsType) {
        this.indicatorsType = indicatorsType;
    }

    public AlertQuery usersId(List<String> usersId) {
        this.usersId = usersId;
        return this;
    }

    public AlertQuery addUsersIdItem(String usersIdItem) {
        this.usersId.add(usersIdItem);
        return this;
    }

    /**
     * Get usersId
     *
     * @return usersId
     **/
    @ApiModelProperty(value = "")
    public List<String> getUsersId() {
        return usersId;
    }

    public void setUsersId(List<String> usersId) {
        this.usersId = usersId;
    }

    public AlertQuery sort(List<String> sort) {
        this.sort = sort;
        return this;
    }

    public AlertQuery addSortItem(String sortItem) {
        this.sort.add(sortItem);
        return this;
    }

    /**
     * Get sort
     *
     * @return sort
     **/
    @ApiModelProperty(value = "")
    public List<String> getSort() {
        return sort;
    }

    public void setSort(List<String> sort) {
        this.sort = sort;
    }

    public AlertQuery severity(List<SeverityEnum> severity) {
        this.severity = severity;
        return this;
    }

    public AlertQuery addSeverityItem(SeverityEnum severityItem) {
        this.severity.add(severityItem);
        return this;
    }

    /**
     * Get severity
     *
     * @return severity
     **/
    @ApiModelProperty(value = "")
    public List<SeverityEnum> getSeverity() {
        return severity;
    }

    public void setSeverity(List<SeverityEnum> severity) {
        this.severity = severity;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AlertQuery alertQuery = (AlertQuery) o;
        return Objects.equals(this.pageSize, alertQuery.pageSize) &&
                Objects.equals(this.pageNumber, alertQuery.pageNumber) &&
                Objects.equals(this.minScore, alertQuery.minScore) &&
                Objects.equals(this.maxScore, alertQuery.maxScore) &&
                Objects.equals(this.startTimeFrom, alertQuery.startTimeFrom) &&
                Objects.equals(this.startTimeTo, alertQuery.startTimeTo) &&
                Objects.equals(this.feedback, alertQuery.feedback) &&
                Objects.equals(this.tags, alertQuery.tags) &&
                Objects.equals(this.userName, alertQuery.userName) &&
                Objects.equals(this.classification, alertQuery.classification) &&
                Objects.equals(this.indicatorsType, alertQuery.indicatorsType) &&
                Objects.equals(this.usersId, alertQuery.usersId) &&
                Objects.equals(this.sort, alertQuery.sort) &&
                Objects.equals(this.severity, alertQuery.severity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageSize, pageNumber, minScore, maxScore, startTimeFrom, startTimeTo, feedback, tags, userName, classification, indicatorsType, usersId, sort, severity);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AlertQuery {\n");

        sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
        sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
        sb.append("    minScore: ").append(toIndentedString(minScore)).append("\n");
        sb.append("    maxScore: ").append(toIndentedString(maxScore)).append("\n");
        sb.append("    startTimeFrom: ").append(toIndentedString(startTimeFrom)).append("\n");
        sb.append("    startTimeTo: ").append(toIndentedString(startTimeTo)).append("\n");
        sb.append("    feedback: ").append(toIndentedString(feedback)).append("\n");
        sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
        sb.append("    userName: ").append(toIndentedString(userName)).append("\n");
        sb.append("    classification: ").append(toIndentedString(classification)).append("\n");
        sb.append("    indicatorsType: ").append(toIndentedString(indicatorsType)).append("\n");
        sb.append("    usersId: ").append(toIndentedString(usersId)).append("\n");
        sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
        sb.append("    severity: ").append(toIndentedString(severity)).append("\n");
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

