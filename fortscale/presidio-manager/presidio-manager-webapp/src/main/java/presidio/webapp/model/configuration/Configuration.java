package presidio.webapp.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import presidio.manager.api.records.UIIntegrationConfiguration;

import java.util.Objects;

/**
 * SecuredConfiguration
 */
@javax.annotation.Generated(value = "PresidioManagerConfiguration", date = "2017-08-07T07:15:37.402Z")

public class Configuration {
  @JsonProperty("system")
  private SystemConfiguration system = null;

  @JsonProperty("dataPipeline")
  private DataConfiguration dataPipeline = null;

  @JsonProperty("dataPulling")
  private DataPullingConfiguration dataPulling = null;

  @JsonProperty("outputForwarding")
  private OutputConfiguration outputForwarding = null;

  @JsonProperty("uiIntegration")
  private UIIntegrationConfiguration uiIntegration = null;


    public Configuration outputForwarding(OutputConfiguration outputForwarding) {
        this.outputForwarding = outputForwarding;
        return this;
    }

    public OutputConfiguration getOutputForwarding() {
        return outputForwarding;
    }

    public void setOutputForwarding(OutputConfiguration outputForwarding) {
        this.outputForwarding = outputForwarding;
    }

  public Configuration system(SystemConfiguration system) {
    this.system = system;
    return this;
  }

   /**
   * Get system
   * @return system
  **/
  @ApiModelProperty(value = "")
  public SystemConfiguration getSystem() {
    return system;
  }

  public void setSystem(SystemConfiguration system) {
    this.system = system;
  }

  public Configuration dataPipeline(DataConfiguration dataPipeline) {
    this.dataPipeline = dataPipeline;
    return this;
  }

   /**
   * Get dataPipeline
   * @return dataPipeline
  **/
  @ApiModelProperty(value = "")
  public DataConfiguration getDataPipeline() {
    return dataPipeline;
  }

  public void setDataPipeline(DataConfiguration dataPipeline) {
    this.dataPipeline = dataPipeline;
  }

  public DataPullingConfiguration getDataPulling() {
    return dataPulling;
  }

  public void setDataPulling(DataPullingConfiguration dataPulling) {
    this.dataPulling = dataPulling;
  }

  public UIIntegrationConfiguration getUiIntegration() {
    return uiIntegration;
  }

  public void setUiIntegration(UIIntegrationConfiguration uiIntegration) {
    this.uiIntegration = uiIntegration;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Configuration that = (Configuration) o;
    return Objects.equals(system, that.system) &&
            Objects.equals(dataPipeline, that.dataPipeline) &&
            Objects.equals(dataPulling, that.dataPulling) &&
            Objects.equals(outputForwarding, that.outputForwarding) &&
            Objects.equals(uiIntegration, that.uiIntegration);
  }

  @Override
  public int hashCode() {

    return Objects.hash(system, dataPipeline, dataPulling, outputForwarding, uiIntegration);
  }


  /**
   * @return ToString you know...
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
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

