package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.domain.Sort;
import presidio.webapp.model.EntityQueryEnums.EntityQueryAggregationFieldName;
import presidio.webapp.model.EntityQueryEnums.EntityQuerySortFieldName;
import presidio.webapp.model.EntityQueryEnums.EntitySeverity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * EntityQuery
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-11T19:39:01.299Z")

public class EntityQuery {
    @JsonProperty("entityName")
    private String entityName = null;

    @JsonProperty("entityType")
    private String entityType = null;

    @JsonProperty("freeText")
    private String freeText = null;

    @JsonProperty("pageSize")
    private Integer pageSize = null;

    @JsonProperty("pageNumber")
    private Integer pageNumber = null;

    @JsonProperty("minScore")
    private Integer minScore = null;

    @JsonProperty("maxScore")
    private Integer maxScore = null;

    @JsonProperty("isPrefix")
    private Boolean isPrefix = null;

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<String>();

    @JsonProperty("alertClassifications")
    private List<String> alertClassifications = new ArrayList<String>();

    @JsonProperty("indicatorsName")
    private List<String> indicatorsName = new ArrayList<String>();

    @JsonProperty("sortFieldNames")
    private List<EntityQuerySortFieldName> sortFieldNames = new ArrayList<EntityQuerySortFieldName>();

    @JsonProperty("severity")
    private List<EntitySeverity> severity = new ArrayList<EntitySeverity>();

    @JsonProperty("expand")
    private Boolean expand = false;

    @JsonProperty("sortDirection")
    private Sort.Direction sortDirection = Sort.Direction.ASC;

    @JsonProperty("aggregateBy")
    private List<EntityQueryAggregationFieldName> aggregateBy;

    public EntityQuery entityName(String entityName) {
        this.entityName = entityName;
        return this;
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

    public EntityQuery freeText(String freeText) {
        this.freeText = freeText;
        return this;
    }

    /**
     * Get entityType
     *
     * @return entityType
     **/
    @ApiModelProperty(value = "")
    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public EntityQuery entityType(String entityType) {
        this.entityType = entityType;
        return this;
    }

    /**
     * Get freeText
     *
     * @return freeText
     **/
    @ApiModelProperty(value = "")
    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public EntityQuery pageSize(Integer pageSize) {
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

    public EntityQuery pageNumber(Integer pageNumber) {
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

    public EntityQuery minScore(Integer minScore) {
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

    public EntityQuery maxScore(Integer maxScore) {
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

    public EntityQuery isPrefix(Boolean isPrefix) {
        this.isPrefix = isPrefix;
        return this;
    }

    /**
     * Get isPrefix
     *
     * @return isPrefix
     **/
    @ApiModelProperty(value = "")
    public Boolean getIsPrefix() {
        return isPrefix;
    }

    public void setIsPrefix(Boolean isPrefix) {
        this.isPrefix = isPrefix;
    }

    public EntityQuery tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public EntityQuery addTagsItem(String tagsItem) {
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

    public EntityQuery alertClassifications(List<String> alertClassifications) {
        this.alertClassifications = alertClassifications;
        return this;
    }

    public EntityQuery addAlertClassificationsItem(String alertClassificationsItem) {
        this.alertClassifications.add(alertClassificationsItem);
        return this;
    }

    /**
     * Get alertClassifications
     *
     * @return alertClassifications
     **/
    @ApiModelProperty(value = "")
    public List<String> getAlertClassifications() {
        return alertClassifications;
    }

    public void setAlertClassifications(List<String> alertClassifications) {
        this.alertClassifications = alertClassifications;
    }

    public EntityQuery indicatorsName(List<String> indicatorsName) {
        this.indicatorsName = indicatorsName;
        return this;
    }

    public EntityQuery addIndicatorsNameItem(String indicatorsNameItem) {
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

    public EntityQuery sortFieldNames(List<EntityQuerySortFieldName> sortFieldNames) {
        this.sortFieldNames = sortFieldNames;
        return this;
    }

    public EntityQuery addSortFieldNameItem(EntityQuerySortFieldName sortFieldName) {
        this.sortFieldNames.add(sortFieldName);
        return this;
    }

    /**
     * Get sortFieldNames
     *
     * @return sortFieldNames
     **/
    @ApiModelProperty(value = "")
    public List<EntityQuerySortFieldName> getSortFieldNames() {
        return sortFieldNames;
    }

    public void setSortFieldNames(List<EntityQuerySortFieldName> sortFieldNames) {
        this.sortFieldNames = sortFieldNames;
    }

    public EntityQuery severity(List<EntitySeverity> severity) {
        this.severity = severity;
        return this;
    }

    public EntityQuery addSeverityItem(EntitySeverity severityItem) {
        this.severity.add(severityItem);
        return this;
    }

    /**
     * Get severity
     *
     * @return severity
     **/
    @ApiModelProperty(value = "")
    public List<EntitySeverity> getSeverity() {
        return severity;
    }

    public void setSeverity(List<EntitySeverity> severity) {
        this.severity = severity;
    }

    public EntityQuery expand(Boolean expand) {
        this.expand = expand;
        return this;
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

    public EntityQuery sortDirection(Sort.Direction sortDirection) {
        this.sortDirection = sortDirection;
        return this;
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

    public EntityQuery aggregateBy(List<EntityQueryAggregationFieldName> aggregateBy) {
        this.aggregateBy = aggregateBy;
        return this;
    }

    /**
     * Get expand
     *
     * @return expand
     **/
    @ApiModelProperty(value = "")
    public List<EntityQueryAggregationFieldName> getAggregateBy() {
        return aggregateBy;
    }

    public void setAggregateBy(List<EntityQueryAggregationFieldName> aggregateBy) {
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
        EntityQuery entityQuery = (EntityQuery) o;
        return Objects.equals(this.entityName, entityQuery.entityName) &&
                Objects.equals(this.entityType, entityQuery.entityType) &&
                Objects.equals(this.freeText, entityQuery.freeText) &&
                Objects.equals(this.pageSize, entityQuery.pageSize) &&
                Objects.equals(this.pageNumber, entityQuery.pageNumber) &&
                Objects.equals(this.minScore, entityQuery.minScore) &&
                Objects.equals(this.maxScore, entityQuery.maxScore) &&
                Objects.equals(this.isPrefix, entityQuery.isPrefix) &&
                Objects.equals(this.tags, entityQuery.tags) &&
                Objects.equals(this.alertClassifications, entityQuery.alertClassifications) &&
                Objects.equals(this.indicatorsName, entityQuery.indicatorsName) &&
                Objects.equals(this.sortFieldNames, entityQuery.sortFieldNames) &&
                Objects.equals(this.severity, entityQuery.severity) &&
                Objects.equals(this.expand, entityQuery.expand) &&
                Objects.equals(this.sortDirection, entityQuery.sortDirection) &&
                Objects.equals(this.aggregateBy, entityQuery.aggregateBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityName, freeText, pageSize, pageNumber, minScore, maxScore, isPrefix, tags, alertClassifications, indicatorsName, sortFieldNames, severity, expand, sortDirection, aggregateBy);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class EntityQuery {\n");

        sb.append("    entityName: ").append(toIndentedString(entityName)).append("\n");
        sb.append("    entityType: ").append(toIndentedString(entityType)).append("\n");
        sb.append("    freeText: ").append(toIndentedString(freeText)).append("\n");
        sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
        sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
        sb.append("    minScore: ").append(toIndentedString(minScore)).append("\n");
        sb.append("    maxScore: ").append(toIndentedString(maxScore)).append("\n");
        sb.append("    isPrefix: ").append(toIndentedString(isPrefix)).append("\n");
        sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
        sb.append("    alertClassifications: ").append(toIndentedString(alertClassifications)).append("\n");
        sb.append("    indicatorsName: ").append(toIndentedString(indicatorsName)).append("\n");
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


