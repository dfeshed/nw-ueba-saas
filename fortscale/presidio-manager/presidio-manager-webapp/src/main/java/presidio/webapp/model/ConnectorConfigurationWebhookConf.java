package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * ConnectorConfigurationWebhookConf
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class ConnectorConfigurationWebhookConf   {
  @JsonProperty("notificationUrl")
  private String notificationUrl = null;

  @JsonProperty("subscriptionId")
  private String subscriptionId = null;

  @JsonProperty("notificationInterval")
  private Integer notificationInterval = null;

  @JsonProperty("heartbeatInterval")
  private Integer heartbeatInterval = null;

  @JsonProperty("batchSize")
  private Integer batchSize = null;

  public ConnectorConfigurationWebhookConf notificationUrl(String notificationUrl) {
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

  public ConnectorConfigurationWebhookConf subscriptionId(String subscriptionId) {
    this.subscriptionId = subscriptionId;
    return this;
  }

   /**
   *  subscription ID
   * @return subscriptionId
  **/
  @ApiModelProperty(example = "&lt;subscription Id&gt;", value = " subscription ID")
  public String getSubscriptionId() {
    return subscriptionId;
  }

  public void setSubscriptionId(String subscriptionId) {
    this.subscriptionId = subscriptionId;
  }

  public ConnectorConfigurationWebhookConf notificationInterval(Integer notificationInterval) {
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

  public ConnectorConfigurationWebhookConf heartbeatInterval(Integer heartbeatInterval) {
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

  public ConnectorConfigurationWebhookConf batchSize(Integer batchSize) {
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
    ConnectorConfigurationWebhookConf connectorConfigurationWebhookConf = (ConnectorConfigurationWebhookConf) o;
    return Objects.equals(this.notificationUrl, connectorConfigurationWebhookConf.notificationUrl) &&
        Objects.equals(this.subscriptionId, connectorConfigurationWebhookConf.subscriptionId) &&
        Objects.equals(this.notificationInterval, connectorConfigurationWebhookConf.notificationInterval) &&
        Objects.equals(this.heartbeatInterval, connectorConfigurationWebhookConf.heartbeatInterval) &&
        Objects.equals(this.batchSize, connectorConfigurationWebhookConf.batchSize);
  }

  @Override
  public int hashCode() {
    return Objects.hash(notificationUrl, subscriptionId, notificationInterval, heartbeatInterval, batchSize);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnectorConfigurationWebhookConf {\n");

    sb.append("    notificationUrl: ").append(toIndentedString(notificationUrl)).append("\n");
    sb.append("    subscriptionId: ").append(toIndentedString(subscriptionId)).append("\n");
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

