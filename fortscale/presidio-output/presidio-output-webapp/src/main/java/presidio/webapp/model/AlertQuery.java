package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.domain.Sort;
import presidio.webapp.model.AlertQueryEnums.AlertQueryAggregationFieldName;
import presidio.webapp.model.AlertQueryEnums.AlertQuerySortFieldName;
import presidio.webapp.model.AlertQueryEnums.AlertSeverity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * AlertQuery
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-12T09:35:35.500Z")

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

    @JsonProperty("feedback")
    private List<AlertQueryEnums.AlertFeedback> feedback = new ArrayList<AlertQueryEnums.AlertFeedback>();

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<String>();

    @JsonProperty("entityName")
    private List<String> entityName = new ArrayList<String>();

    @JsonProperty("classification")
    private List<String> classification = new ArrayList<String>();

    @JsonProperty("indicatorsName")
    private List<String> indicatorsName = new ArrayList<String>();

    @JsonProperty("entityDocumentIds")
    private List<String> entityDocumentIds = new ArrayList<String>();

    @JsonProperty("sortFieldNames")
    private List<AlertQuerySortFieldName> sortFieldNames = new ArrayList<AlertQuerySortFieldName>();

    @JsonProperty("severity")
    private List<AlertSeverity> severity = new ArrayList<AlertSeverity>();

    @JsonProperty("expand")
    private Boolean expand = false;

    @JsonProperty("sortDirection")
    private Sort.Direction sortDirection = Sort.Direction.ASC;

    @JsonProperty("aggregateBy")
    private List<AlertQueryAggregationFieldName> aggregateBy;

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

    /**
     * Get feedback
     *
     * @return feedback
     **/
    @ApiModelProperty(value = "")
    public List<AlertQueryEnums.AlertFeedback> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<AlertQueryEnums.AlertFeedback> feedback) {
        this.feedback = feedback;
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

    /**
     * Get entityName
     *
     * @return entityName
     **/
    @ApiModelProperty(value = "")
    public List<String> getEntityName() {
        return entityName;
    }

    public void setEntityName(List<String> entityName) {
        this.entityName = entityName;
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
     * Get entityDocumentIds
     *
     * @return entityDocumentIds
     **/
    @ApiModelProperty(value = "")
    public List<String> getEntityDocumentIds() {
        return entityDocumentIds;
    }

    public void setEntityDocumentIds(List<String> entityDocumentIds) {
        this.entityDocumentIds = entityDocumentIds;
    }

    /**
     * Get sortFieldNames
     *
     * @return sortFieldNames
     **/
    @ApiModelProperty(value = "")
    public List<AlertQuerySortFieldName> getSortFieldNames() {
        return sortFieldNames;
    }

    public void setSortFieldNames(List<AlertQuerySortFieldName> sortFieldNames) {
        this.sortFieldNames = sortFieldNames;
    }

    /**
     * Get severity
     *
     * @return severity
     **/
    @ApiModelProperty(value = "")
    public List<AlertSeverity> getSeverity() {
        return severity;
    }

    public void setSeverity(List<AlertSeverity> severity) {
        this.severity = severity;
    }

    /**
     * Get sortDirection
     *
     * @return sortDirection
     **/
    @ApiModelProperty(value = "")
    public Sort.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Sort.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    /**
     * Get expand
     *
     * @return expand
     **/
    @ApiModelProperty(value = "")
    public Boolean getExpand() {
        return expand;
    }

    public void setExpand(Boolean expand) {
        this.expand = expand;
    }

    /**
     * Get expand
     *
     * @return expand
     **/
    @ApiModelProperty(value = "")
    public List<AlertQueryAggregationFieldName> getAggregateBy() {
        return aggregateBy;
    }

    public void setAggregateBy(List<AlertQueryAggregationFieldName> aggregateBy) {
        this.aggregateBy = aggregateBy;
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
                Objects.equals(this.entityName, alertQuery.entityName) &&
                Objects.equals(this.classification, alertQuery.classification) &&
                Objects.equals(this.indicatorsName, alertQuery.indicatorsName) &&
                Objects.equals(this.entityDocumentIds, alertQuery.entityDocumentIds) &&
                Objects.equals(this.sortFieldNames, alertQuery.sortFieldNames) &&
                Objects.equals(this.severity, alertQuery.severity) &&
                Objects.equals(this.expand, alertQuery.expand) &&
                Objects.equals(this.sortDirection, alertQuery.sortDirection) &&
                Objects.equals(this.aggregateBy, alertQuery.aggregateBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageSize, pageNumber, minScore, maxScore, startTimeFrom, startTimeTo, feedback, tags, entityName, classification, indicatorsName, entityDocumentIds, sortFieldNames, severity, expand, sortDirection, aggregateBy);

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
        sb.append("    entityName: ").append(toIndentedString(entityName)).append("\n");
        sb.append("    classification: ").append(toIndentedString(classification)).append("\n");
        sb.append("    indicatorsName: ").append(toIndentedString(indicatorsName)).append("\n");
        sb.append("    entityDocumentIds: ").append(toIndentedString(entityDocumentIds)).append("\n");
        sb.append("    sortFieldNames: ").append(toIndentedString(sortFieldNames)).append("\n");
        sb.append("    sortDirection: ").append(toIndentedString(sortDirection)).append("\n");
        sb.append("    severity: ").append(toIndentedString(severity)).append("\n");
        sb.append("    expand: ").append(toIndentedString(expand)).append("\n");
        sb.append("    aggregateBy: ").append(toIndentedString(aggregateBy)).append("\n");
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

