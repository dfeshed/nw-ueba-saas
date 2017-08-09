package presidio.webapp.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * SystemConfiguration
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class SystemConfiguration {
    @JsonProperty("username")
    private String username = null;

    @JsonProperty("password")
    private String password = null;

    @JsonProperty("adminGroup")
    private String adminGroup = null;

    @JsonProperty("analystGroup")
    private String analystGroup = null;

    @JsonProperty("smtpHost")
    private String smtpHost = null;

    @JsonProperty("kdcUrl")
    private String kdcUrl = null;

    public SystemConfiguration username(String username) {
        this.username = username;
        return this;
    }

    /**
     * a domain user, already havin Service Principal Name(SPN) for this tomcat
     *
     * @return username
     **/
    @ApiModelProperty(example = "presidio@somecompany.dom", value = "a domain user, already havin Service Principal Name(SPN) for this tomcat")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public SystemConfiguration password(String password) {
        this.password = password;
        return this;
    }

    /**
     * The domain user’s password
     *
     * @return password
     **/
    @ApiModelProperty(example = "password", value = "The domain user’s password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SystemConfiguration adminGroup(String adminGroup) {
        this.adminGroup = adminGroup;
        return this;
    }

    /**
     * Active directory group that represent presidio admin user
     *
     * @return adminGroup
     **/
    @ApiModelProperty(example = "presidio-admins-somecompany", value = "Active directory group that represent presidio admin user")
    public String getAdminGroup() {
        return adminGroup;
    }

    public void setAdminGroup(String adminGroup) {
        this.adminGroup = adminGroup;
    }

    public SystemConfiguration analystGroup(String analystGroup) {
        this.analystGroup = analystGroup;
        return this;
    }

    /**
     * Active directory group that represent presidio security analyst user
     *
     * @return analystGroup
     **/
    @ApiModelProperty(example = "presidio-soc-team-somecompany", value = "Active directory group that represent presidio security analyst user")
    public String getAnalystGroup() {
        return analystGroup;
    }

    public void setAnalystGroup(String analystGroup) {
        this.analystGroup = analystGroup;
    }

    public SystemConfiguration smtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
        return this;
    }

    /**
     * smtp host to be configured for system health alerts
     *
     * @return smtpHost
     **/
    @ApiModelProperty(example = "name.of-server.com:25", value = "smtp host to be configured for system health alerts")
    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public SystemConfiguration kdcUrl(String kdcUrl) {
        this.kdcUrl = kdcUrl;
        return this;
    }

    /**
     * The Key Distribution Center URL
     *
     * @return kdcUrl
     **/
    @ApiModelProperty(value = "The Key Distribution Center URL")
    public String getKdcUrl() {
        return kdcUrl;
    }

    public void setKdcUrl(String kdcUrl) {
        this.kdcUrl = kdcUrl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SystemConfiguration systemConfiguration = (SystemConfiguration) o;
        return Objects.equals(this.username, systemConfiguration.username) &&
                Objects.equals(this.password, systemConfiguration.password) &&
                Objects.equals(this.adminGroup, systemConfiguration.adminGroup) &&
                Objects.equals(this.analystGroup, systemConfiguration.analystGroup) &&
                Objects.equals(this.smtpHost, systemConfiguration.smtpHost) &&
                Objects.equals(this.kdcUrl, systemConfiguration.kdcUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, adminGroup, analystGroup, smtpHost, kdcUrl);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SystemConfiguration {\n");

        sb.append("    username: ").append(toIndentedString(username)).append("\n");
        sb.append("    password: ").append(toIndentedString(password)).append("\n");
        sb.append("    adminGroup: ").append(toIndentedString(adminGroup)).append("\n");
        sb.append("    analystGroup: ").append(toIndentedString(analystGroup)).append("\n");
        sb.append("    smtpHost: ").append(toIndentedString(smtpHost)).append("\n");
        sb.append("    kdcUrl: ").append(toIndentedString(kdcUrl)).append("\n");
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

    public void setParameters(JsonNode node) {
        Iterator<Map.Entry<String, JsonNode>> itr = node.fields();
        String key;
        while (itr.hasNext()) {
            key = itr.next().getKey();
            setValues(key, node.get(key).toString());
        }
    }

    private void setValues(String key, String value) {
        switch (key) {
            case "password":
                setPassword(value);
                break;
            case "username":
                setUsername(value);
                break;
            case "adminGroup":
                setAdminGroup(value);
                break;
            case "analystGroup":
                setAnalystGroup(value);
                break;
            case "smtpHost":
                setSmtpHost(value);
                break;
            case "kdcUrl":
                setKdcUrl(value);
                break;
        }
    }
}

