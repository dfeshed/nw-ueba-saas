package presidio.webapp.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * HourlyBucket
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-17T11:29:11.540Z")

public class HourlyBucket   {
  @JsonProperty("key")
  private String key = null;

  @JsonProperty("value")
  private Integer value = null;

  @JsonProperty("anomaly")
  private Boolean anomaly = null;

  public HourlyBucket key(String key) {
    this.key = key;
    return this;
  }

  /**
   * Get key
   * @return key
   **/
  @ApiModelProperty(value = "")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public HourlyBucket value(Integer value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
   **/
  @ApiModelProperty(value = "")
  public Integer getValue() {
    return value;
  }

  public void setValue(Integer value) {
    this.value = value;
  }

  public HourlyBucket anomaly(Boolean anomaly) {
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
    HourlyBucket hourlyBucket = (HourlyBucket) o;
    return Objects.equals(this.key, hourlyBucket.key) &&
            Objects.equals(this.value, hourlyBucket.value) &&
            Objects.equals(this.anomaly, hourlyBucket.anomaly);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value, anomaly);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HourlyBucket {\n");

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

