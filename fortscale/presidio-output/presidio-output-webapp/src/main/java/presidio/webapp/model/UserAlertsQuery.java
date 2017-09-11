package presidio.webapp.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * UserAlertsQuery
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-11T10:27:29.360Z")

public class UserAlertsQuery   {
  @JsonProperty("pageSize")
  private Integer pageSize = null;

  @JsonProperty("pageNumber")
  private Integer pageNumber = null;

  @JsonProperty("sort")
  private List<String> sort = new ArrayList<String>();

  public UserAlertsQuery pageSize(Integer pageSize) {
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

  public UserAlertsQuery pageNumber(Integer pageNumber) {
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

  public UserAlertsQuery sort(List<String> sort) {
    this.sort = sort;
    return this;
  }

  public UserAlertsQuery addSortItem(String sortItem) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserAlertsQuery userAlertsQuery = (UserAlertsQuery) o;
    return Objects.equals(this.pageSize, userAlertsQuery.pageSize) &&
        Objects.equals(this.pageNumber, userAlertsQuery.pageNumber) &&
        Objects.equals(this.sort, userAlertsQuery.sort);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageSize, pageNumber, sort);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserAlertsQuery {\n");
    
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
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

