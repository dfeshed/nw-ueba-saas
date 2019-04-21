package presidio.manager.webapp.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Generated;

@Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-04-15T00:00:00.000Z")
public class SecureConfiguration {
    private DataPipelineConfiguration dataPipeline;
    private OutputConfiguration outputForwarding;
    private UiIntegrationConfiguration uiIntegration;

    @JsonCreator
    public SecureConfiguration(
            @JsonProperty("dataPipeline") DataPipelineConfiguration dataPipeline,
            // For backward compatibility, the "output" property is named "outputForwarding"
            @JsonProperty("outputForwarding") OutputConfiguration outputForwarding,
            @JsonProperty("uiIntegration") UiIntegrationConfiguration uiIntegration) {

        this.dataPipeline = Validate.notNull(dataPipeline, "dataPipeline cannot be null");
        this.outputForwarding = outputForwarding == null ? OutputConfiguration.getDefaultOutputConfiguration() : outputForwarding;
        this.uiIntegration = Validate.notNull(uiIntegration, "uiIntegration cannot be null");
    }

    public DataPipelineConfiguration getDataPipeline() {
        return dataPipeline;
    }

    public void setDataPipeline(DataPipelineConfiguration dataPipeline) {
        this.dataPipeline = dataPipeline;
    }

    public OutputConfiguration getOutputForwarding() {
        return outputForwarding;
    }

    public void setOutputForwarding(OutputConfiguration outputForwarding) {
        this.outputForwarding = outputForwarding;
    }

    public UiIntegrationConfiguration getUiIntegration() {
        return uiIntegration;
    }

    public void setUiIntegration(UiIntegrationConfiguration uiIntegration) {
        this.uiIntegration = uiIntegration;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof SecureConfiguration)) return false;
        SecureConfiguration that = (SecureConfiguration)object;
        return new EqualsBuilder()
                .append(dataPipeline, that.dataPipeline)
                .append(outputForwarding, that.outputForwarding)
                .append(uiIntegration, that.uiIntegration)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(dataPipeline)
                .append(outputForwarding)
                .append(uiIntegration)
                .toHashCode();
    }
}
