package presidio.webapp.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * UserQuery
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-10T10:33:22.473Z")

public class UserQuery   {
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

  @JsonProperty("classification")
  private List<String> classification = new ArrayList<String>();

  @JsonProperty("indicatorsType")
  private List<String> indicatorsType = new ArrayList<String>();

  @JsonProperty("sort")
  private List<String> sort = new ArrayList<String>();

  @JsonProperty("severity")
  private List<UserSeverity> severity = new ArrayList<UserSeverity>();

  public UserQuery userName(String userName) {
    this.userName = userName;
    return this;
  }

   /**
   * Get userName
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
   * @return tags
  **/
  @ApiModelProperty(value = "")
  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public UserQuery classification(List<String> classification) {
    this.classification = classification;
    return this;
  }

  public UserQuery addClassificationItem(String classificationItem) {
    this.classification.add(classificationItem);
    return this;
  }

   /**
   * Get classification
   * @return classification
  **/
  @ApiModelProperty(value = "")
  public List<String> getClassification() {
    return classification;
  }

  public void setClassification(List<String> classification) {
    this.classification = classification;
  }

  public UserQuery indicatorsType(List<String> indicatorsType) {
    this.indicatorsType = indicatorsType;
    return this;
  }

  public UserQuery addIndicatorsTypeItem(String indicatorsTypeItem) {
    this.indicatorsType.add(indicatorsTypeItem);
    return this;
  }

   /**
   * Get indicatorsType
   * @return indicatorsType
  **/
  @ApiModelProperty(value = "")
  public List<String> getIndicatorsType() {
    return indicatorsType;
  }

  public void setIndicatorsType(List<String> indicatorsType) {
    this.indicatorsType = indicatorsType;
  }

  public UserQuery sort(List<String> sort) {
    this.sort = sort;
    return this;
  }

  public UserQuery addSortItem(String sortItem) {
    this.sort.add(sortItem);
    return this;
  }

   /**
   * Get sort
   * @return sort
  **/
  @ApiModelProperty(value = "")
  public List<String> getSort() {
    return sort;
  }

  public void setSort(List<String> sort) {
    this.sort = sort;
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
   * @return severity
  **/
  @ApiModelProperty(value = "")
  public List<UserSeverity> getSeverity() {
    return severity;
  }

  public void setSeverity(List<UserSeverity> severity) {
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
    UserQuery userQuery = (UserQuery) o;
    return Objects.equals(this.userName, userQuery.userName) &&
        Objects.equals(this.pageSize, userQuery.pageSize) &&
        Objects.equals(this.pageNumber, userQuery.pageNumber) &&
        Objects.equals(this.minScore, userQuery.minScore) &&
        Objects.equals(this.maxScore, userQuery.maxScore) &&
        Objects.equals(this.isPrefix, userQuery.isPrefix) &&
        Objects.equals(this.tags, userQuery.tags) &&
        Objects.equals(this.classification, userQuery.classification) &&
        Objects.equals(this.indicatorsType, userQuery.indicatorsType) &&
        Objects.equals(this.sort, userQuery.sort) &&
        Objects.equals(this.severity, userQuery.severity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userName, pageSize, pageNumber, minScore, maxScore, isPrefix, tags, classification, indicatorsType, sort, severity);
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
    sb.append("    classification: ").append(toIndentedString(classification)).append("\n");
    sb.append("    indicatorsType: ").append(toIndentedString(indicatorsType)).append("\n");
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

