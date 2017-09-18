package presidio.webapp.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * IndicatorQuery
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-12T02:53:18.177Z")

public class IndicatorQuery   {
  @JsonProperty("pageSize")
  private Integer pageSize = null;

  @JsonProperty("pageNumber")
  private Integer pageNumber = null;

  @JsonProperty("sort")
  private List<String> sort = new ArrayList<String>();

  @JsonProperty("expand")
  private Boolean expand = false;

  public IndicatorQuery pageSize(Integer pageSize) {
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

  public IndicatorQuery pageNumber(Integer pageNumber) {
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

  public IndicatorQuery sort(List<String> sort) {
    this.sort = sort;
    return this;
  }

  public IndicatorQuery addSortItem(String sortItem) {
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

  public IndicatorQuery expand(Boolean expand) {
    this.expand = expand;
    return this;
  }

  /**
   * Get expand
   * @return expand
   **/
  @ApiModelProperty(value = "")
  public Boolean getExpand() {
    return expand;
  }

  public void setExpand(Boolean expand) {
    this.expand = expand;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IndicatorQuery indicatorQuery = (IndicatorQuery) o;
    return Objects.equals(this.pageSize, indicatorQuery.pageSize) &&
            Objects.equals(this.pageNumber, indicatorQuery.pageNumber) &&
            Objects.equals(this.sort, indicatorQuery.sort) &&
            Objects.equals(this.expand, indicatorQuery.expand);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageSize, pageNumber, sort, expand);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IndicatorQuery {\n");

    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    pageNumber: ").append(toIndentedString(pageNumber)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    expand: ").append(toIndentedString(expand)).append("\n");
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



