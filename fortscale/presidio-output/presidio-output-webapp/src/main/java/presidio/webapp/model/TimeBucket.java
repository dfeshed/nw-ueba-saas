package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * TimeBucket
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-17T11:29:11.540Z")

public class TimeBucket   {
  @JsonProperty("key")
  private BigDecimal key = null;

  @JsonProperty("value")
  private Double value = null;

  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  @JsonProperty("anomaly")
  private Boolean anomaly = false;

  public TimeBucket key(BigDecimal key) {
    this.key = key;
    return this;
  }

  /**
   * Get key
   * @return key
   **/
  @ApiModelProperty(value = "")
  public BigDecimal getKey() {
    return key;
  }

  public void setKey(BigDecimal key) {
    this.key = key;
  }

  public TimeBucket value(Double value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
   **/
  @ApiModelProperty(value = "")
  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public TimeBucket anomaly(Boolean anomaly) {
    this.anomaly = anomaly;
    return this;
  }

  /**
   * Get anomaly
   * @return anomaly
   **/
  @ApiModelProperty(value = "")
  public Boolean getAnomaly() {
    return anomaly;
  }

  public void setAnomaly(Boolean anomaly) {
    this.anomaly = anomaly;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TimeBucket timeBucket = (TimeBucket) o;
    return Objects.equals(this.key, timeBucket.key) &&
            Objects.equals(this.value, timeBucket.value) &&
            Objects.equals(this.anomaly, timeBucket.anomaly);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value, anomaly);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TimeBucket {\n");

    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    anomaly: ").append(toIndentedString(anomaly)).append("\n");
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

