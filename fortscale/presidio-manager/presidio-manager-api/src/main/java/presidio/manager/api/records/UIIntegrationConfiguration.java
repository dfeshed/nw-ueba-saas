package presidio.manager.api.records;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

/***
 * Holds properties regarding integration of presidio-ui and netwitness-ui
 */
public class UIIntegrationConfiguration {

    public static final String FIELD_BROKER_ID = "brokerId";
    public static final String FIELD_ADMIN_SERVER = "adminServer";

    @JsonProperty(FIELD_BROKER_ID)
    private String brokerId;

    @JsonProperty(FIELD_ADMIN_SERVER)
    private String adminServerAddress;

    /**
     * default C'tor used for object mapper
     */
    public UIIntegrationConfiguration() {
    }

    public UIIntegrationConfiguration(JsonNode value) {
        this(value.get(UIIntegrationConfiguration.FIELD_BROKER_ID).asText(), value.get(UIIntegrationConfiguration.FIELD_ADMIN_SERVER).asText());
    }

    /**
     *
     * @param brokerId identifier of the broker where the data is pulled from. used to pivot from alerts back to the raw events
     * @param adminServerAddress an IP address or a DNS name that identifies the admin server (netwitness UI is hosted on it)
     */
    public UIIntegrationConfiguration(String brokerId, String adminServerAddress) {
        this();
        this.brokerId = brokerId;
        this.adminServerAddress = adminServerAddress;
    }

    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    public String getAdminServerAddress() {
        return adminServerAddress;
    }

    public void setAdminServerAddress(String adminServerAddress) {
        this.adminServerAddress = adminServerAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UIIntegrationConfiguration that = (UIIntegrationConfiguration) o;
        return Objects.equals(brokerId, that.brokerId) &&
                Objects.equals(adminServerAddress, that.adminServerAddress);
    }

    @Override
    public int hashCode() {

        return Objects.hash(brokerId, adminServerAddress);
    }


    /**
     * @return ToString you know...
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
