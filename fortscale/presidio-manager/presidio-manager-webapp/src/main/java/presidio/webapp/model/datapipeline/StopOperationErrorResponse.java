package presidio.webapp.model.datapipeline;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * StopOperationErrorResponse
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class StopOperationErrorResponse   {
  @JsonProperty("code")
  private String code = null;

  @JsonProperty("message")
  private String message = null;

  @JsonProperty("error")
  private List<StopOperationErrorResponseError> error = new ArrayList<StopOperationErrorResponseError>();

  public StopOperationErrorResponse code(String code) {
    this.code = code;
    return this;
  }

   /**
   * Get code
   * @return code
  **/
  @ApiModelProperty(example = "500", value = "")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public StopOperationErrorResponse message(String message) {
    this.message = message;
    return this;
  }

   /**
   * Get message
   * @return message
  **/
  @ApiModelProperty(example = "Operation failed", value = "")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public StopOperationErrorResponse error(List<StopOperationErrorResponseError> error) {
    this.error = error;
    return this;
  }

  public StopOperationErrorResponse addErrorItem(StopOperationErrorResponseError errorItem) {
    this.error.add(errorItem);
    return this;
  }

   /**
   * Get error
   * @return error
  **/
  @ApiModelProperty(value = "")
  public List<StopOperationErrorResponseError> getError() {
    return error;
  }

  public void setError(List<StopOperationErrorResponseError> error) {
    this.error = error;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StopOperationErrorResponse stopOperationErrorResponse = (StopOperationErrorResponse) o;
    return Objects.equals(this.code, stopOperationErrorResponse.code) &&
        Objects.equals(this.message, stopOperationErrorResponse.message) &&
        Objects.equals(this.error, stopOperationErrorResponse.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message, error);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StopOperationErrorResponse {\n");

    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
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

