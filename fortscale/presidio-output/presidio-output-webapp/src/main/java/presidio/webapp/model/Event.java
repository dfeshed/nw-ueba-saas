package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Objects;

/**
 * event
 */
@ApiModel(description = "event")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-11T15:24:24.489Z")

public class Event extends HashMap<String, Object>  {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("schema")
  private String schema = null;

  @JsonProperty("time")
  private BigDecimal time = null;

  public Event id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   **/
  @ApiModelProperty(required = true, value = "")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Event schema(String schema) {
    this.schema = schema;
    return this;
  }

  /**
   * Schema Name
   * @return schema
   **/
  @ApiModelProperty(required = true, value = "Schema Name")
  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public Event time(BigDecimal time) {
    this.time = time;
    return this;
  }

  /**
   * Get time
   * @return time
   **/
  @ApiModelProperty(required = true, value = "")
  public BigDecimal getTime() {
    return time;
  }

  public void setTime(BigDecimal time) {
    this.time = time;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Event event = (Event) o;
    return Objects.equals(this.id, event.id) &&
            Objects.equals(this.schema, event.schema) &&
            Objects.equals(this.time, event.time) &&
            super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, schema, time, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Event {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    schema: ").append(toIndentedString(schema)).append("\n");
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
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

