package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * WebhookConfiguration
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class WebhookConfiguration   {
  @JsonProperty("connector")
  private ConnectorConfiguration connector = null;

  public WebhookConfiguration connector(ConnectorConfiguration connector) {
    this.connector = connector;
    return this;
  }

   /**
   * Get connector
   * @return connector
  **/
  @ApiModelProperty(value = "")
  public ConnectorConfiguration getConnector() {
    return connector;
  }

  public void setConnector(ConnectorConfiguration connector) {
    this.connector = connector;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WebhookConfiguration webhookConfiguration = (WebhookConfiguration) o;
    return Objects.equals(this.connector, webhookConfiguration.connector);
  }

  @Override
  public int hashCode() {
    return Objects.hash(connector);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WebhookConfiguration {\n");

    sb.append("    connector: ").append(toIndentedString(connector)).append("\n");
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

