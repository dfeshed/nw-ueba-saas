package presidio.sdk.api.domain.rawevents;

import fortscale.domain.core.EventResult;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.time.Instant;
import java.util.HashMap;
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
    public static final String COUNTRY_FIELD_NAME = "country";
    public static final String CITY_FIELD_NAME = "city";
    public static final String USER_ID_FIELD_NAME = "userId";
    public static final String RESULT_FIELD_NAME = "result";
    public static final String OPERATION_TYPE_FIELD_NAME = "operationType";
    public static final String OPERATION_TYPE_CATEGORIES_FIELD_NAME = "operationTypeCategories";
    public static final String USER_NAME_FIELD_NAME = "userName";
    public static final String USER_DISPLAY_NAME_FIELD_NAME = "userDisplayName";
    public static final String RESULT_CODE_FIELD_NAME = "resultCode";
    public static final String IS_USER_ADMIN_FIELD_NAME = "isUserAdmin";

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

    @Field(COUNTRY_FIELD_NAME)
    private String country;

    @Field(CITY_FIELD_NAME)
    private String city;

    @Field(USER_ID_FIELD_NAME)
    @NotEmpty
    protected String userId;

    @Field(OPERATION_TYPE_FIELD_NAME)
    @NotEmpty
    protected String operationType;

    @Field(OPERATION_TYPE_CATEGORIES_FIELD_NAME)
    protected List<String> operationTypeCategories;

    @Field(RESULT_FIELD_NAME)
    protected EventResult result;

    @Field(USER_NAME_FIELD_NAME)
    protected String userName;

    @Field(USER_DISPLAY_NAME_FIELD_NAME)
    protected String userDisplayName;

    @Field(RESULT_CODE_FIELD_NAME)
    protected String resultCode;

    {
        additionalInfo = new HashMap<>();
        additionalInfo.put(IS_USER_ADMIN_FIELD_NAME, Boolean.toString(false));
    }

    public AuthenticationRawEvent(AuthenticationRawEvent other) {
        super(other);
        this.srcMachineId = other.srcMachineId;
        this.srcMachineName = other.srcMachineName;
        this.dstMachineId = other.dstMachineId;
        this.dstMachineName = other.dstMachineName;
        this.dstMachineDomain = other.dstMachineDomain;
        this.site = other.site;
        this.city = other.city;
        this.country = other.country;
        this.userId = other.userId;
        this.operationType = other.operationType;
        this.operationTypeCategories = other.operationTypeCategories;
        this.result = other.result;
        this.userName = other.userName;
        this.userDisplayName = other.userDisplayName;
        this.resultCode = other.resultCode;
    }

    public AuthenticationRawEvent() {
    }

    public AuthenticationRawEvent(Instant dateTime, String eventId, String dataSource, String userId, String operationType,
                                  List<String> operationTypeCategories, EventResult result, String userName,
                                  String userDisplayName, Map<String, String> additionalInfo, String srcMachineId,
                                  String srcMachineName, String dstMachineId, String dstMachineName, String dstMachineDomain,
                                  String resultCode, String site, String country, String city) {
        super(dateTime, eventId, dataSource, additionalInfo);
        this.srcMachineId = srcMachineId;
        this.srcMachineName = srcMachineName;
        this.dstMachineId = dstMachineId;
        this.dstMachineName = dstMachineName;
        this.dstMachineDomain = dstMachineDomain;
        this.site = site;
        this.country = country;
        this.city = city;
        this.userId = userId;
        this.operationType = operationType;
        this.operationTypeCategories = operationTypeCategories;
        this.result = result;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.resultCode = resultCode;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public EventResult getResult() {
        return result;
    }

    public void setResult(EventResult result) {
        this.result = result;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public List<String> getOperationTypeCategories() {
        return operationTypeCategories;
    }

    public void setOperationTypeCategories(List<String> operationTypeCategories) {
        this.operationTypeCategories = operationTypeCategories;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
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
                ", operationTypeCategories=" + operationTypeCategories +
                ", result=" + result +
                ", userName='" + userName + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                ", additionalInfo=" + additionalInfo +
                ", dateTime=" + dateTime +
                ", site=" + site +
                ", city=" + city +
                ", country=" + country +
                '}';
    }
}
