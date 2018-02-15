package presidio.webapp.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Created by maors on 2/5/2018.
 */
public class SyslogConfiguration {

    @JsonProperty("host")
    private String host;
    @JsonProperty("port")
    private int port;

    public SyslogConfiguration host(String host) {
        this.host = host;
        return this;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public SyslogConfiguration port(int port) {
        this.port = port;
        return this;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SyslogConfiguration _syslogConfiguration = (SyslogConfiguration) o;
        return _syslogConfiguration.getHost().equals(this.host) &&
                _syslogConfiguration.port == this.port;
    }

    public int hashCode() {
        return Objects.hash(host, port);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SyslogConfiguration {\n");

        sb.append("    host: ").append(toIndentedString(host)).append("\n");
        sb.append("    port: ").append(toIndentedString(port)).append("\n");
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
