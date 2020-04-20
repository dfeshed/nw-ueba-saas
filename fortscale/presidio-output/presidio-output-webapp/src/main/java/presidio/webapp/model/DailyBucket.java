package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * DailyBucket
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-12T09:35:35.500Z")

public class DailyBucket   {
  @JsonProperty("key")
  private String key = null;

  @JsonProperty("value")
  private HourlyBuckets value = null;

  public DailyBucket key(String key) {
    this.key = key;
    return this;
  }

   /**
   * Day of Week
   * @return key
  **/
  @ApiModelProperty(value = "Day of Week")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public DailyBucket value(HourlyBuckets value) {
    this.value = value;
    return this;
  }

   /**
   * Get value
   * @return value
  **/
  @ApiModelProperty(value = "")
  public HourlyBuckets getValue() {
    return value;
  }

  public void setValue(HourlyBuckets value) {
    this.value = value;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DailyBucket dailyBucket = (DailyBucket) o;
    return Objects.equals(this.key, dailyBucket.key) &&
        Objects.equals(this.value, dailyBucket.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DailyBucket {\n");
    
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

