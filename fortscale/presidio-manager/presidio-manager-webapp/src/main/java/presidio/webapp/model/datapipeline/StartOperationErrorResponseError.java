package presidio.webapp.model.datapipeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * StartOperationErrorResponseError
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class StartOperationErrorResponseError   {
  /**
   * The name of the service for where the error occurred
   */
  public enum DomainEnum {
    DATAPIPLINE("dataPipline");

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
    ALREADYSTARTED("alreadyStarted"),
    
    SERVICESDOWN("servicesDown"),
    
    MISSINGCONFIGURATION("missingConfiguration");

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

  public StartOperationErrorResponseError domain(DomainEnum domain) {
    this.domain = domain;
    return this;
  }

   /**
   * The name of the service for where the error occurred
   * @return domain
  **/
  @ApiModelProperty(example = "dataPipline", value = "The name of the service for where the error occurred")
  public DomainEnum getDomain() {
    return domain;
  }

  public void setDomain(DomainEnum domain) {
    this.domain = domain;
  }

  public StartOperationErrorResponseError reason(ReasonEnum reason) {
    this.reason = reason;
    return this;
  }

   /**
   * A unique identifier for the error
   * @return reason
  **/
  @ApiModelProperty(value = "A unique identifier for the error")
  public ReasonEnum getReason() {
    return reason;
  }

  public void setReason(ReasonEnum reason) {
    this.reason = reason;
  }

  public StartOperationErrorResponseError message(String message) {
    this.message = message;
    return this;
  }

   /**
   * A description of the error in a human-readable format
   * @return message
  **/
  @ApiModelProperty(example = "Data pipe line is already started", value = "A description of the error in a human-readable format")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StartOperationErrorResponseError startOperationErrorResponseError = (StartOperationErrorResponseError) o;
    return Objects.equals(this.domain, startOperationErrorResponseError.domain) &&
        Objects.equals(this.reason, startOperationErrorResponseError.reason) &&
        Objects.equals(this.message, startOperationErrorResponseError.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(domain, reason, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StartOperationErrorResponseError {\n");

    sb.append("    domain: ").append(toIndentedString(domain)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

