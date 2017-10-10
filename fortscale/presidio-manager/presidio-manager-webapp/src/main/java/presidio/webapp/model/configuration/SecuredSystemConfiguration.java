package presidio.webapp.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * SecuredSystemConfiguration
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class SecuredSystemConfiguration   {
  @JsonProperty("username")
  private String username = null;

  @JsonProperty("analystGroup")
  private String analystGroup = null;

  @JsonProperty("smtpHost")
  private String smtpHost = null;

  @JsonProperty("ldapUrl")
  private String ldapUrl = null;

  @JsonProperty("realmName")
  private String realmName=null;

  @JsonProperty("krbServiceName")
  private String krbServiceName=null;

  public SecuredSystemConfiguration username(String username) {
    this.username = username;
    return this;
  }

   /**
   * a domain user, already havin Service Principal Name(SPN) for this tomcat
   * @return username
  **/
  @ApiModelProperty(example = "presidio@somecompany.dom", value = "a domain user, already havin Service Principal Name(SPN) for this tomcat")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public SecuredSystemConfiguration analystGroup(String analystGroup) {
    this.analystGroup = analystGroup;
    return this;
  }

   /**
   * Active directory group that represent presidio security analyst user
   * @return analystGroup
  **/
  @ApiModelProperty(example = "presidio-soc-team-somecompany", value = "Active directory group that represent presidio security analyst user")
  public String getAnalystGroup() {
    return analystGroup;
  }

  public void setAnalystGroup(String analystGroup) {
    this.analystGroup = analystGroup;
  }

  public SecuredSystemConfiguration smtpHost(String smtpHost) {
    this.smtpHost = smtpHost;
    return this;
  }

   /**
   * smtp host to be configured for system health alerts
   * @return smtpHost
  **/
  @ApiModelProperty(example = "name.of-server.com:25", value = "smtp host to be configured for system health alerts")
  public String getSmtpHost() {
    return smtpHost;
  }

  public void setSmtpHost(String smtpHost) {
    this.smtpHost = smtpHost;
  }

  public SecuredSystemConfiguration ldapUrl(String ldapUrl) {
    this.ldapUrl = ldapUrl;
    return this;
  }

  public String getRealmName() {
    return realmName;
  }

  public void setRealmName(String realmName) {
    this.realmName = realmName;
  }

  public String getKrbServiceName() {
    return krbServiceName;
  }

  public void setKrbServiceName(String krbServiceName) {
    this.krbServiceName = krbServiceName;
  }

  /**
   * The Key Distribution Center URL
   * @return ldapUrl
  **/
  @ApiModelProperty(value = "The Key Distribution Center URL")
  public String getLdapUrl() {
    return ldapUrl;
  }

  public void setLdapUrl(String ldapUrl) {
    this.ldapUrl = ldapUrl;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SecuredSystemConfiguration securedSystemConfiguration = (SecuredSystemConfiguration) o;
    return Objects.equals(this.username, securedSystemConfiguration.username) &&
        Objects.equals(this.realmName, securedSystemConfiguration.realmName) &&
        Objects.equals(this.analystGroup, securedSystemConfiguration.analystGroup) &&
        Objects.equals(this.smtpHost, securedSystemConfiguration.smtpHost) &&
        Objects.equals(this.ldapUrl, securedSystemConfiguration.ldapUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, realmName, analystGroup, smtpHost, ldapUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SecuredSystemConfiguration {\n");

    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    realmName: ").append(toIndentedString(realmName)).append("\n");
    sb.append("    analystGroup: ").append(toIndentedString(analystGroup)).append("\n");
    sb.append("    smtpHost: ").append(toIndentedString(smtpHost)).append("\n");
    sb.append("    ldapUrl: ").append(toIndentedString(ldapUrl)).append("\n");
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

