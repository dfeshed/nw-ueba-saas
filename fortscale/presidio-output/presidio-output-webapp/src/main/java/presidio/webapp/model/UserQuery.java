package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * UserQuery
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-11T19:39:01.299Z")

public class UserQuery {
    @JsonProperty("userName")
    private String userName = null;

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
    private List<UserQuerySortFieldName> sortFieldNames = new ArrayList<UserQuerySortFieldName>();

    @JsonProperty("severity")
    private List<UserSeverity> severity = new ArrayList<UserSeverity>();

    @JsonProperty("expand")
    private Boolean expand = false;

    @JsonProperty("sortDirection")
    private Sort.Direction sortDirection = Sort.Direction.ASC;

    @JsonProperty("aggregateBySeverity")
    private Boolean aggregateBySeverity = false;

    public UserQuery userName(String userName) {
        this.userName = userName;
        return this;
    }

    /**
     * Get userName
     *
     * @return userName
     **/
    @ApiModelProperty(value = "")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserQuery pageSize(Integer pageSize) {
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

    public UserQuery pageNumber(Integer pageNumber) {
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

    public UserQuery minScore(Integer minScore) {
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

    public UserQuery maxScore(Integer maxScore) {
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

    public UserQuery isPrefix(Boolean isPrefix) {
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

    public UserQuery tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public UserQuery addTagsItem(String tagsItem) {
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

    public UserQuery alertClassifications(List<String> alertClassifications) {
        this.alertClassifications = alertClassifications;
        return this;
    }

    public UserQuery addAlertClassificationsItem(String alertClassificationsItem) {
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

    public UserQuery indicatorsName(List<String> indicatorsName) {
        this.indicatorsName = indicatorsName;
        return this;
    }

    public UserQuery addIndicatorsNameItem(String indicatorsNameItem) {
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

    public UserQuery sortFieldNames(List<UserQuerySortFieldName> sortFieldNames) {
        this.sortFieldNames = sortFieldNames;
        return this;
    }

    public UserQuery addSortFieldNameItem(UserQuerySortFieldName sortFieldName) {
        this.sortFieldNames.add(sortFieldName);
        return this;
    }

    /**
     * Get sortFieldNames
     *
     * @return sortFieldNames
     **/
    @ApiModelProperty(value = "")
    public List<UserQuerySortFieldName> getSortFieldNames() {
        return sortFieldNames;
    }

    public void setSortFieldNames(List<UserQuerySortFieldName> sortFieldNames) {
        this.sortFieldNames = sortFieldNames;
    }

    public UserQuery severity(List<UserSeverity> severity) {
        this.severity = severity;
        return this;
    }

    public UserQuery addSeverityItem(UserSeverity severityItem) {
        this.severity.add(severityItem);
        return this;
    }

    /**
     * Get severity
     *
     * @return severity
     **/
    @ApiModelProperty(value = "")
    public List<UserSeverity> getSeverity() {
        return severity;
    }

    public void setSeverity(List<UserSeverity> severity) {
        this.severity = severity;
    }

    public UserQuery expand(Boolean expand) {
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

    public UserQuery sortDirection(Sort.Direction sortDirection) {
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

    public UserQuery aggregateBySeverity(Boolean aggregateBySeverity) {
        this.aggregateBySeverity = aggregateBySeverity;
        return this;
    }

    /**
     * Get expand
     *
     * @return expand
     **/
    @ApiModelProperty(value = "")
    public Boolean getAggregateBySeverity() {
        return aggregateBySeverity;
    }

    public void setAggregateBySeverity(Boolean aggregateBySeverity) {
        this.aggregateBySeverity = aggregateBySeverity;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserQuery userQuery = (UserQuery) o;
        return Objects.equals(this.userName, userQuery.userName) &&
                Objects.equals(this.pageSize, userQuery.pageSize) &&
                Objects.equals(this.pageNumber, userQuery.pageNumber) &&
                Objects.equals(this.minScore, userQuery.minScore) &&
                Objects.equals(this.maxScore, userQuery.maxScore) &&
                Objects.equals(this.isPrefix, userQuery.isPrefix) &&
                Objects.equals(this.tags, userQuery.tags) &&
                Objects.equals(this.alertClassifications, userQuery.alertClassifications) &&
                Objects.equals(this.indicatorsName, userQuery.indicatorsName) &&
                Objects.equals(this.sortFieldNames, userQuery.sortFieldNames) &&
                Objects.equals(this.severity, userQuery.severity) &&
                Objects.equals(this.expand, userQuery.expand) &&
                Objects.equals(this.sortDirection, userQuery.sortDirection) &&
                Objects.equals(this.aggregateBySeverity, userQuery.aggregateBySeverity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, pageSize, pageNumber, minScore, maxScore, isPrefix, tags, alertClassifications, indicatorsName, sortFieldNames, severity, expand, sortDirection, aggregateBySeverity);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class UserQuery {\n");

        sb.append("    userName: ").append(toIndentedString(userName)).append("\n");
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
        sb.append("    aggregateBySeverity: ").append(toIndentedString(aggregateBySeverity)).append("\n");
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

