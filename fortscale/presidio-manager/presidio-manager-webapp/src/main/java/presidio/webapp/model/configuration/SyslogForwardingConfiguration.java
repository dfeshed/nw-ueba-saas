package presidio.webapp.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Created by maors on 2/5/2018.
 */
public class SyslogForwardingConfiguration {

    @JsonProperty("alert")
    private SyslogConfiguration alert;
    @JsonProperty("user")
    private SyslogConfiguration user;


    public SyslogForwardingConfiguration alert(SyslogConfiguration alert) {
        this.alert = alert;
        return this;
    }

    public SyslogConfiguration getAlert() {
        return alert;
    }

    public void setAlert(SyslogConfiguration alert) {
        this.alert = alert;
    }

    public SyslogForwardingConfiguration user(SyslogConfiguration user) {
        this.user = user;
        return this;
    }

    public SyslogConfiguration getUser() {
        return user;
    }

    public void setUser(SyslogConfiguration user) {
        this.user = user;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SyslogForwardingConfiguration _syslogForwardingConfiguration = (SyslogForwardingConfiguration) o;
        return Objects.equals(_syslogForwardingConfiguration.getAlert(), this.alert) &&
                Objects.equals(_syslogForwardingConfiguration.getUser(), this.user);
    }

    public int hashCode() {
        return Objects.hash(user, alert);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SyslogForwardingConfiguration {\n");

        sb.append("    alert: ").append(toIndentedString(alert)).append("\n");
        sb.append("    user: ").append(toIndentedString(user)).append("\n");
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
