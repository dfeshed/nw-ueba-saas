package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * ConnectorConfiguration
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class ConnectorConfiguration   {
  /**
   * Connector strategy describes the communication channel between an externaly system, like a security log repository, to presidio connector and determines how the data flows between the systems.
   */
  public enum ConnectorStrategyEnum {
    WEBHOOK("quest-webhook");

    private String value;

    ConnectorStrategyEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ConnectorStrategyEnum fromValue(String text) {
      for (ConnectorStrategyEnum b : ConnectorStrategyEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("connectorStrategy")
  private ConnectorStrategyEnum connectorStrategy = null;

  /**
   * Data availability helps to determine when an hour bucket is fully loaded to the system. Events are filtered after bucket is closed.
   */
  public enum DataAvailabilityStrategyEnum {
    HEURISTIC("time-heuristic");

    private String value;

    DataAvailabilityStrategyEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static DataAvailabilityStrategyEnum fromValue(String text) {
      for (DataAvailabilityStrategyEnum b : DataAvailabilityStrategyEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("dataAvailabilityStrategy")
  private DataAvailabilityStrategyEnum dataAvailabilityStrategy = null;

  @JsonProperty("webhookConf")
  private ConnectorConfigurationWebhookConf webhookConf = null;

  public ConnectorConfiguration connectorStrategy(ConnectorStrategyEnum connectorStrategy) {
    this.connectorStrategy = connectorStrategy;
    return this;
  }

   /**
   * Connector strategy describes the communication channel between an externaly system, like a security log repository, to presidio connector and determines how the data flows between the systems.
   * @return connectorStrategy
  **/
  @ApiModelProperty(example = "quest-webhook", value = "Connector strategy describes the communication channel between an externaly system, like a security log repository, to presidio connector and determines how the data flows between the systems.")
  public ConnectorStrategyEnum getConnectorStrategy() {
    return connectorStrategy;
  }

  public void setConnectorStrategy(ConnectorStrategyEnum connectorStrategy) {
    this.connectorStrategy = connectorStrategy;
  }

  public ConnectorConfiguration dataAvailabilityStrategy(DataAvailabilityStrategyEnum dataAvailabilityStrategy) {
    this.dataAvailabilityStrategy = dataAvailabilityStrategy;
    return this;
  }

   /**
   * Data availability helps to determine when an hour bucket is fully loaded to the system. Events are filtered after bucket is closed.
   * @return dataAvailabilityStrategy
  **/
  @ApiModelProperty(example = "time-heuristic", value = "Data availability helps to determine when an hour bucket is fully loaded to the system. Events are filtered after bucket is closed.")
  public DataAvailabilityStrategyEnum getDataAvailabilityStrategy() {
    return dataAvailabilityStrategy;
  }

  public void setDataAvailabilityStrategy(DataAvailabilityStrategyEnum dataAvailabilityStrategy) {
    this.dataAvailabilityStrategy = dataAvailabilityStrategy;
  }

  public ConnectorConfiguration webhookConf(ConnectorConfigurationWebhookConf webhookConf) {
    this.webhookConf = webhookConf;
    return this;
  }

   /**
   * Get webhookConf
   * @return webhookConf
  **/
  @ApiModelProperty(value = "")
  public ConnectorConfigurationWebhookConf getWebhookConf() {
    return webhookConf;
  }

  public void setWebhookConf(ConnectorConfigurationWebhookConf webhookConf) {
    this.webhookConf = webhookConf;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConnectorConfiguration connectorConfiguration = (ConnectorConfiguration) o;
    return Objects.equals(this.connectorStrategy, connectorConfiguration.connectorStrategy) &&
        Objects.equals(this.dataAvailabilityStrategy, connectorConfiguration.dataAvailabilityStrategy) &&
        Objects.equals(this.webhookConf, connectorConfiguration.webhookConf);
  }

  @Override
  public int hashCode() {
    return Objects.hash(connectorStrategy, dataAvailabilityStrategy, webhookConf);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnectorConfiguration {\n");

    sb.append("    connectorStrategy: ").append(toIndentedString(connectorStrategy)).append("\n");
    sb.append("    dataAvailabilityStrategy: ").append(toIndentedString(dataAvailabilityStrategy)).append("\n");
    sb.append("    webhookConf: ").append(toIndentedString(webhookConf)).append("\n");
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

