package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * historical data aggregated by value (for pie / histogram graphs)
 */
@ApiModel(description = "historical data aggregated by value (for pie / histogram graphs)")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-12T06:58:50.313Z")

public class HistoricalDataCountAggregation extends HistoricalData  {
  /**
   * Gets or Sets type
   */
  public enum TypeEnum {
    COUNT_AGGRAGEATION("COUNT_AGGREGATION");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TypeEnum fromValue(String text) {
      for (TypeEnum b : TypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("type")
  private TypeEnum type = null;

  @JsonProperty("buckets")
  private CountBuckets buckets = null;

  public HistoricalDataCountAggregation type(TypeEnum type) {
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @ApiModelProperty(value = "")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public HistoricalDataCountAggregation buckets(CountBuckets buckets) {
    this.buckets = buckets;
    return this;
  }

   /**
   * Get buckets
   * @return buckets
  **/
  @ApiModelProperty(value = "")
  public CountBuckets getBuckets() {
    return buckets;
  }

  public void setBuckets(CountBuckets buckets) {
    this.buckets = buckets;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HistoricalDataCountAggregation historicalDataCountAggregation = (HistoricalDataCountAggregation) o;
    return Objects.equals(this.type, historicalDataCountAggregation.type) &&
        Objects.equals(this.buckets, historicalDataCountAggregation.buckets) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, buckets, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HistoricalDataCountAggregation {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    buckets: ").append(toIndentedString(buckets)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

