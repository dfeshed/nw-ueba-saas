package presidio.webapp.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Created by maors on 2/5/2018.
 */
public class OutputConfiguration {

    @JsonProperty("syslog")
    private SyslogForwardingConfiguration syslogForwardingConfiguration;

    public OutputConfiguration syslogForwardingConfiguration(SyslogForwardingConfiguration syslogForwardingConfiguration) {
        this.syslogForwardingConfiguration = syslogForwardingConfiguration;
        return this;
    }

    public SyslogForwardingConfiguration getSyslogForwardingConfiguration() {
        return syslogForwardingConfiguration;
    }

    public void setSyslogForwardingConfiguration(SyslogForwardingConfiguration syslogForwardingConfiguration) {
        this.syslogForwardingConfiguration = syslogForwardingConfiguration;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OutputConfiguration _outputConfiguration = (OutputConfiguration) o;
        return Objects.equals(_outputConfiguration.getSyslogForwardingConfiguration(), this.syslogForwardingConfiguration);
    }

    public int hashCode() {
        return Objects.hash(syslogForwardingConfiguration);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OutputConfiguration {\n");

        sb.append("    syslogForwardingConfiguration: ").append(toIndentedString(syslogForwardingConfiguration)).append("\n");
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

