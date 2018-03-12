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
    public static final String DST_MACHINE_ID_FIELD_NAME = "dstMachineId";
    public static final String SRC_MACHINE_NAME_FIELD_NAME = "srcMachineName";
    public static final String DST_MACHINE_NAME_FIELD_NAME = "dstMachineName";
    public static final String DST_MACHINE_DOMAIN_FIELD_NAME = "dstMachineDomain";
    public static final String SITE_FIELD_NAME = "site";

    @Field(SRC_MACHINE_ID_FIELD_NAME)
    private String srcMachineId;

    @Field(SRC_MACHINE_NAME_FIELD_NAME)
    private String srcMachineName;

    @Field(DST_MACHINE_ID_FIELD_NAME)
    private String dstMachineId;

    @Field(DST_MACHINE_NAME_FIELD_NAME)
    private String dstMachineName;

    @Field(DST_MACHINE_DOMAIN_FIELD_NAME)
    private String dstMachineDomain;

    @Field(SITE_FIELD_NAME)
    private String site;

    public AuthenticationRawEvent(AuthenticationRawEvent other) {
        super(other);
        this.srcMachineId = other.srcMachineId;
        this.srcMachineName = other.srcMachineName;
        this.dstMachineId = other.dstMachineId;
        this.dstMachineName = other.dstMachineName;
        this.dstMachineDomain = other.dstMachineDomain;
        this.site = other.site;
    }

    public AuthenticationRawEvent() {
    }

    public AuthenticationRawEvent(Instant dateTime, String eventId, String dataSource, String userId, String operationType,
                                  List<String> operationTypeCategory, EventResult result, String userName,
                                  String userDisplayName, Map<String, String> additionalInfo, String srcMachineId,
                                  String srcMachineName, String dstMachineId, String dstMachineName, String dstMachineDomain, String resultCode, String site) {
        super(dateTime, eventId, dataSource, userId, operationType, operationTypeCategory, result, userName, userDisplayName, additionalInfo, resultCode);
        this.srcMachineId = srcMachineId;
        this.srcMachineName = srcMachineName;
        this.dstMachineId = dstMachineId;
        this.dstMachineName = dstMachineName;
        this.dstMachineDomain = dstMachineDomain;
        this.site = site;
    }

    public String getDstMachineId() {
        return dstMachineId;
    }

    public void setDstMachineId(String dstMachineId) {
        this.dstMachineId = dstMachineId;
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

    public String getDstMachineName() {
        return dstMachineName;
    }

    public void setDstMachineName(String dstMachineName) {
        this.dstMachineName = dstMachineName;
    }

    public String getDstMachineDomain() {
        return dstMachineDomain;
    }

    public void setDstMachineDomain(String dstMachineDomain) {
        this.dstMachineDomain = dstMachineDomain;
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
                ", dstMachineId='" + dstMachineId + '\'' +
                ", dstMachineName='" + dstMachineName + '\'' +
                ", dstMachineDomain='" + dstMachineDomain + '\'' +
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
