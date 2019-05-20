package presidio.manager.webapp.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Generated;

@Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-04-15T00:00:00.000Z")
public class UiIntegrationConfiguration {
    private String brokerId;
    private String adminServer;

    @JsonCreator
    public UiIntegrationConfiguration(
            @JsonProperty("brokerId") String brokerId,
            @JsonProperty("adminServer") String adminServer) {

        this.brokerId = Validate.notBlank(brokerId, "brokerId cannot be blank");
        this.adminServer = Validate.notBlank(adminServer, "adminServer cannot be blank");
    }

    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    public String getAdminServer() {
        return adminServer;
    }

    public void setAdminServer(String adminServer) {
        this.adminServer = adminServer;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof UiIntegrationConfiguration)) return false;
        UiIntegrationConfiguration that = (UiIntegrationConfiguration)object;
        return new EqualsBuilder()
                .append(brokerId, that.brokerId)
                .append(adminServer, that.adminServer)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(brokerId)
                .append(adminServer)
                .toHashCode();
    }
}
