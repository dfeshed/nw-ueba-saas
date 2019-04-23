package presidio.manager.webapp.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Generated;

@Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-04-15T00:00:00.000Z")
public class OutputConfiguration {
    private boolean enableForwarding;

    @JsonCreator
    public OutputConfiguration(@JsonProperty("enableForwarding") Boolean enableForwarding) {
        // Default "enableForwarding" value is false
        this.enableForwarding = enableForwarding == null ? false : enableForwarding;
    }

    public boolean isEnableForwarding() {
        return enableForwarding;
    }

    public void setEnableForwarding(boolean enableForwarding) {
        this.enableForwarding = enableForwarding;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof OutputConfiguration)) return false;
        OutputConfiguration that = (OutputConfiguration)object;
        return new EqualsBuilder()
                .append(enableForwarding, that.enableForwarding)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(enableForwarding)
                .toHashCode();
    }

    public static OutputConfiguration getDefaultOutputConfiguration() {
        return new OutputConfiguration(null);
    }
}
