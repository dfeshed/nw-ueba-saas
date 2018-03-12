package presidio.sdk.api.domain.rawevents;

import fortscale.domain.core.EventResult;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document
public class AuthenticationRawEvent extends AbstractInputDocument {

    public static final String SRC_MACHINE_ID_FIELD_NAME = "srcMachineId";
    public static final String RESOURCE_ID_FIELD_NAME = "resourceId";
    public static final String SRC_MACHINE_NAME_FIELD_NAME = "srcMachineName";
    public static final String RESOURCE_NAME_FIELD_NAME = "resourceName";
    public static final String RESOURCE_DOMAIN_FIELD_NAME = "resourceDomain";
    public static final String SITE_FIELD_NAME = "site";

    @Field(SRC_MACHINE_ID_FIELD_NAME)
    private String srcMachineId;

    @Field(SRC_MACHINE_NAME_FIELD_NAME)
    private String srcMachineName;

    @Field(RESOURCE_ID_FIELD_NAME)
    private String resourceId;

    @Field(RESOURCE_NAME_FIELD_NAME)
    private String resourceName;

    @Field(RESOURCE_DOMAIN_FIELD_NAME)
    private String resourceDomain;

    @Field(SITE_FIELD_NAME)
    private String site;

    public AuthenticationRawEvent(AuthenticationRawEvent other) {
        super(other);
        this.srcMachineId = other.srcMachineId;
        this.srcMachineName = other.srcMachineName;
        this.resourceId = other.resourceId;
        this.resourceName = other.resourceName;
        this.resourceDomain = other.resourceDomain;
        this.site = other.site;
    }

    public AuthenticationRawEvent() {
    }

    public AuthenticationRawEvent(Instant dateTime, String eventId, String dataSource, String userId, String operationType,
                                  List<String> operationTypeCategory, EventResult result, String userName,
                                  String userDisplayName, Map<String, String> additionalInfo, String srcMachineId,
                                  String srcMachineName, String resourceId, String resourceName, String resourceDomain, String resultCode, String site) {
        super(dateTime, eventId, dataSource, userId, operationType, operationTypeCategory, result, userName, userDisplayName, additionalInfo, resultCode);
        this.srcMachineId = srcMachineId;
        this.srcMachineName = srcMachineName;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceDomain = resourceDomain;
        this.site = site;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getSrcMachineId() {
        return srcMachineId;
    }

    public void setSrcMachineId(String srcMachineId) {
        this.srcMachineId = srcMachineId;
    }

    public String getSrcMachineName() {
        return srcMachineName;
    }

    public void setSrcMachineName(String srcMachineName) {
        this.srcMachineName = srcMachineName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceDomain() {
        return resourceDomain;
    }

    public void setResourceDomain(String resourceDomain) {
        this.resourceDomain = resourceDomain;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public String toString() {
        return "AuthenticationRawEvent{" +
                "srcMachineId='" + srcMachineId + '\'' +
                ", srcMachineName='" + srcMachineName + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", resourceDomain='" + resourceDomain + '\'' +
                ", eventId='" + eventId + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", userId='" + userId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", operationTypeCategory=" + operationTypeCategory +
                ", result=" + result +
                ", userName='" + userName + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", additionalInfo=" + additionalInfo +
                ", dateTime=" + dateTime +
                ", site=" + site +
                '}';
    }
}
