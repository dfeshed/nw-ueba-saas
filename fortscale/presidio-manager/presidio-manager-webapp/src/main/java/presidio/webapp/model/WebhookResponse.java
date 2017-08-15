package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * WebhookResponse
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class WebhookResponse   {
  @JsonProperty("notificationUrl")
  private String notificationUrl = null;

  @JsonProperty("authorizationId")
  private String authorizationId = null;

  @JsonProperty("notificationInterval")
  private Integer notificationInterval = null;

  @JsonProperty("heartbeatInterval")
  private Integer heartbeatInterval = null;

  @JsonProperty("batchSize")
  private Integer batchSize = null;

  public WebhookResponse notificationUrl(String notificationUrl) {
    this.notificationUrl = notificationUrl;
    return this;
  }

   /**
   * base webhook URL to receive event notifications
   * @return notificationUrl
  **/
  @ApiModelProperty(example = "/controller/v1.0/webhook", value = "base webhook URL to receive event notifications")
  public String getNotificationUrl() {
    return notificationUrl;
  }

  public void setNotificationUrl(String notificationUrl) {
    this.notificationUrl = notificationUrl;
  }

  public WebhookResponse authorizationId(String authorizationId) {
    this.authorizationId = authorizationId;
    return this;
  }

   /**
   *  AuthorizationID
   * @return authorizationId
  **/
  @ApiModelProperty(example = "&lt;Auth Id&gt;", value = " AuthorizationID")
  public String getAuthorizationId() {
    return authorizationId;
  }

  public void setAuthorizationId(String authorizationId) {
    this.authorizationId = authorizationId;
  }

  public WebhookResponse notificationInterval(Integer notificationInterval) {
    this.notificationInterval = notificationInterval;
    return this;
  }

   /**
   * preferred notification interval in milliseconds, 0 for none (1 millisecond will be used when no more events)
   * @return notificationInterval
  **/
  @ApiModelProperty(example = "5", value = "preferred notification interval in milliseconds, 0 for none (1 millisecond will be used when no more events)")
  public Integer getNotificationInterval() {
    return notificationInterval;
  }

  public void setNotificationInterval(Integer notificationInterval) {
    this.notificationInterval = notificationInterval;
  }

  public WebhookResponse heartbeatInterval(Integer heartbeatInterval) {
    this.heartbeatInterval = heartbeatInterval;
    return this;
  }

   /**
   * preferred heartbeat interval in milliseconds, 0 for none
   * @return heartbeatInterval
  **/
  @ApiModelProperty(example = "1", value = "preferred heartbeat interval in milliseconds, 0 for none")
  public Integer getHeartbeatInterval() {
    return heartbeatInterval;
  }

  public void setHeartbeatInterval(Integer heartbeatInterval) {
    this.heartbeatInterval = heartbeatInterval;
  }

  public WebhookResponse batchSize(Integer batchSize) {
    this.batchSize = batchSize;
    return this;
  }

   /**
   * preferred event batch size
   * @return batchSize
  **/
  @ApiModelProperty(example = "1000", value = "preferred event batch size")
  public Integer getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(Integer batchSize) {
    this.batchSize = batchSize;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WebhookResponse webhookResponse = (WebhookResponse) o;
    return Objects.equals(this.notificationUrl, webhookResponse.notificationUrl) &&
        Objects.equals(this.authorizationId, webhookResponse.authorizationId) &&
        Objects.equals(this.notificationInterval, webhookResponse.notificationInterval) &&
        Objects.equals(this.heartbeatInterval, webhookResponse.heartbeatInterval) &&
        Objects.equals(this.batchSize, webhookResponse.batchSize);
  }

  @Override
  public int hashCode() {
    return Objects.hash(notificationUrl, authorizationId, notificationInterval, heartbeatInterval, batchSize);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WebhookResponse {\n");

    sb.append("    notificationUrl: ").append(toIndentedString(notificationUrl)).append("\n");
    sb.append("    authorizationId: ").append(toIndentedString(authorizationId)).append("\n");
    sb.append("    notificationInterval: ").append(toIndentedString(notificationInterval)).append("\n");
    sb.append("    heartbeatInterval: ").append(toIndentedString(heartbeatInterval)).append("\n");
    sb.append("    batchSize: ").append(toIndentedString(batchSize)).append("\n");
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

