package presidio.manager.webapp.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Generated;

@Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-04-15T00:00:00.000Z")
public class Configuration extends SecureConfiguration {
    private DataPullingConfiguration dataPulling;

    @JsonCreator
    public Configuration(
            @JsonProperty("dataPipeline") DataPipelineConfiguration dataPipeline,
            // For backward compatibility, the "output" property is named "outputForwarding"
            @JsonProperty("outputForwarding") OutputConfiguration outputForwarding,
            @JsonProperty("uiIntegration") UiIntegrationConfiguration uiIntegration,
            @JsonProperty("dataPulling") DataPullingConfiguration dataPulling) {

        super(dataPipeline, outputForwarding, uiIntegration);
        this.dataPulling = Validate.notNull(dataPulling, "dataPulling cannot be null");
    }

    public DataPullingConfiguration getDataPulling() {
        return dataPulling;
    }

    public void setDataPulling(DataPullingConfiguration dataPulling) {
        this.dataPulling = dataPulling;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Configuration)) return false;
        Configuration that = (Configuration)object;
        return new EqualsBuilder()
                .appendSuper(super.equals(object))
                .append(dataPulling, that.dataPulling)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(dataPulling)
                .toHashCode();
    }
}
