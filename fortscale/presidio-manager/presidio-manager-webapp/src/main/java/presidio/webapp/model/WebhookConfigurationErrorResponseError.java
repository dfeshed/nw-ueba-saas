package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * WebhookConfigurationErrorResponseError
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class WebhookConfigurationErrorResponseError   {
  /**
   * The name of the service for where the error occurred
   */
  public enum DomainEnum {
    CONNECTOR("connector");

    private String value;

    DomainEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static DomainEnum fromValue(String text) {
      for (DomainEnum b : DomainEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("domain")
  private DomainEnum domain = null;

  /**
   * A unique identifier for the error
   */
  public enum ReasonEnum {
    INVALIDPROPERTY("invalidProperty"),
    
    MISSINGPROPERTY("missingProperty");

    private String value;

    ReasonEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ReasonEnum fromValue(String text) {
      for (ReasonEnum b : ReasonEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("reason")
  private ReasonEnum reason = null;

  @JsonProperty("message")
  private String message = null;

  /**
   * Determines how the client should interpret the location propery
   */
  public enum LocationTypeEnum {
    JSONPATH("jsonPath"),
    
    PARAMETER("parameter");

    private String value;

    LocationTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static LocationTypeEnum fromValue(String text) {
      for (LocationTypeEnum b : LocationTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("locationType")
  private LocationTypeEnum locationType = null;

  @JsonProperty("location")
  private String location = null;

  public WebhookConfigurationErrorResponseError domain(DomainEnum domain) {
    this.domain = domain;
    return this;
  }

   /**
   * The name of the service for where the error occurred
   * @return domain
  **/
  @ApiModelProperty(example = "connector", value = "The name of the service for where the error occurred")
  public DomainEnum getDomain() {
    return domain;
  }

  public void setDomain(DomainEnum domain) {
    this.domain = domain;
  }

  public WebhookConfigurationErrorResponseError reason(ReasonEnum reason) {
    this.reason = reason;
    return this;
  }

   /**
   * A unique identifier for the error
   * @return reason
  **/
  @ApiModelProperty(example = "invalidProperty", value = "A unique identifier for the error")
  public ReasonEnum getReason() {
    return reason;
  }

  public void setReason(ReasonEnum reason) {
    this.reason = reason;
  }

  public WebhookConfigurationErrorResponseError message(String message) {
    this.message = message;
    return this;
  }

   /**
   * A description of the error in a human-readable format
   * @return message
  **/
  @ApiModelProperty(example = "Invalid string value: &quot;TIME&quot;. Allowed values: [time-heuristic]", value = "A description of the error in a human-readable format")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public WebhookConfigurationErrorResponseError locationType(LocationTypeEnum locationType) {
    this.locationType = locationType;
    return this;
  }

   /**
   * Determines how the client should interpret the location propery
   * @return locationType
  **/
  @ApiModelProperty(value = "Determines how the client should interpret the location propery")
  public LocationTypeEnum getLocationType() {
    return locationType;
  }

  public void setLocationType(LocationTypeEnum locationType) {
    this.locationType = locationType;
  }

  public WebhookConfigurationErrorResponseError location(String location) {
    this.location = location;
    return this;
  }

   /**
   * The location of the error with interpertation determined by the location type. (e.g. parameter name, json path)
   * @return location
  **/
  @ApiModelProperty(example = "dataAvailabilityStrategy", value = "The location of the error with interpertation determined by the location type. (e.g. parameter name, json path)")
  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WebhookConfigurationErrorResponseError webhookConfigurationErrorResponseError = (WebhookConfigurationErrorResponseError) o;
    return Objects.equals(this.domain, webhookConfigurationErrorResponseError.domain) &&
        Objects.equals(this.reason, webhookConfigurationErrorResponseError.reason) &&
        Objects.equals(this.message, webhookConfigurationErrorResponseError.message) &&
        Objects.equals(this.locationType, webhookConfigurationErrorResponseError.locationType) &&
        Objects.equals(this.location, webhookConfigurationErrorResponseError.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(domain, reason, message, locationType, location);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WebhookConfigurationErrorResponseError {\n");

    sb.append("    domain: ").append(toIndentedString(domain)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    locationType: ").append(toIndentedString(locationType)).append("\n");
    sb.append("    location: ").append(toIndentedString(location)).append("\n");
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

