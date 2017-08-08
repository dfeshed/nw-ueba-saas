package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * A JSONPatch document as defined by RFC 6902 (http://jsonpatch.com/)
 */
@ApiModel(description = "A JSONPatch document as defined by RFC 6902 (http://jsonpatch.com/)")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class PatchRequestInner   {
  /**
   * The operation to be performed
   */
  public enum OpEnum {
    ADD("add"),
    
    REMOVE("remove"),
    
    REPLACE("replace");

    private String value;

    OpEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static OpEnum fromValue(String text) {
      for (OpEnum b : OpEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("op")
  private OpEnum op = null;

  @JsonProperty("path")
  private String path = null;

  @JsonProperty("value")
  private Object value = null;

  public PatchRequestInner op(OpEnum op) {
    this.op = op;
    return this;
  }

   /**
   * The operation to be performed
   * @return op
  **/
  @ApiModelProperty(required = true, value = "The operation to be performed")
  public OpEnum getOp() {
    return op;
  }

  public void setOp(OpEnum op) {
    this.op = op;
  }

  public PatchRequestInner path(String path) {
    this.path = path;
    return this;
  }

   /**
   * A JSON-Pointer
   * @return path
  **/
  @ApiModelProperty(example = "dataPipeline/schemas", required = true, value = "A JSON-Pointer")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public PatchRequestInner value(Object value) {
    this.value = value;
    return this;
  }

   /**
   * The value to be used within the operations.
   * @return value
  **/
  @ApiModelProperty(example = "&quot;authentication&quot;", value = "The value to be used within the operations.")
  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PatchRequestInner patchRequestInner = (PatchRequestInner) o;
    return Objects.equals(this.op, patchRequestInner.op) &&
        Objects.equals(this.path, patchRequestInner.path) &&
        Objects.equals(this.value, patchRequestInner.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(op, path, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PatchRequestInner {\n");

    sb.append("    op: ").append(toIndentedString(op)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

