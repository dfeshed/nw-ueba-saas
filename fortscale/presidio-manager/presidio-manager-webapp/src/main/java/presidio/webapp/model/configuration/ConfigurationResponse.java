package presidio.webapp.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ConfigurationResponse
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class ConfigurationResponse {
  @JsonProperty("code")
  private String code = null;

  @JsonProperty("message")
  private String message = null;

  @JsonProperty("error")
  private List<ConfigurationResponseError> error = new ArrayList<ConfigurationResponseError>();

  public ConfigurationResponse code(String code) {
    this.code = code;
    return this;
  }

   /**
   * Get code
   * @return code
  **/
  @ApiModelProperty(example = "422", value = "")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public ConfigurationResponse message(String message) {
    this.message = message;
    return this;
  }

   /**
   * Get message
   * @return message
  **/
  @ApiModelProperty(example = "The configuration is invalid", value = "")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ConfigurationResponse error(List<ConfigurationResponseError> error) {
    this.error = error;
    return this;
  }

  public ConfigurationResponse addErrorItem(ConfigurationResponseError errorItem) {
    this.error.add(errorItem);
    return this;
  }

   /**
   * Get error
   * @return error
  **/
  @ApiModelProperty(value = "")
  public List<ConfigurationResponseError> getError() {
    return error;
  }

  public void setError(List<ConfigurationResponseError> error) {
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
    ConfigurationResponse configurationErrorResponse = (ConfigurationResponse) o;
    return Objects.equals(this.code, configurationErrorResponse.code) &&
        Objects.equals(this.message, configurationErrorResponse.message) &&
        Objects.equals(this.error, configurationErrorResponse.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message, error);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConfigurationResponse {\n");

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

