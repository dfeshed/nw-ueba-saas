package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * IndicatorsWrapper
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-11T11:15:54.772Z")

public class IndicatorsWrapper   {
  @JsonProperty("indicators")
  private List<Indicator> indicators = new ArrayList<Indicator>();

  @JsonProperty("total")
  private Integer total = null;

  @JsonProperty("page")
  private Integer page = null;

  public IndicatorsWrapper indicators(List<Indicator> indicators) {
    this.indicators = indicators;
    return this;
  }

  public IndicatorsWrapper addIndicatorsItem(Indicator indicatorsItem) {
    this.indicators.add(indicatorsItem);
    return this;
  }

  /**
   * Get indicators
   * @return indicators
   **/
  @ApiModelProperty(value = "")
  public List<Indicator> getIndicators() {
    return indicators;
  }

  public void setIndicators(List<Indicator> indicators) {
    this.indicators = indicators;
  }

  public IndicatorsWrapper total(Integer total) {
    this.total = total;
    return this;
  }

  /**
   * Get total
   * minimum: 0
   * @return total
   **/
  @ApiModelProperty(value = "")
  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public IndicatorsWrapper page(Integer page) {
    this.page = page;
    return this;
  }

  /**
   * Get page
   * minimum: 0
   * @return page
   **/
  @ApiModelProperty(value = "")
  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IndicatorsWrapper indicatorsWrapper = (IndicatorsWrapper) o;
    return Objects.equals(this.indicators, indicatorsWrapper.indicators) &&
            Objects.equals(this.total, indicatorsWrapper.total) &&
            Objects.equals(this.page, indicatorsWrapper.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(indicators, total, page);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IndicatorsWrapper {\n");

    sb.append("    indicators: ").append(toIndentedString(indicators)).append("\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
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

