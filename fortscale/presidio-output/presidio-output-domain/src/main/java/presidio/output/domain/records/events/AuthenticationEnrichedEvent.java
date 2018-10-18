package presidio.output.domain.records.events;

import fortscale.domain.core.EventResult;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Created by efratn on 02/08/2017.
 */
@Document
public class AuthenticationEnrichedEvent extends EnrichedEvent {

    public static final String SRC_MACHINE_ID_FIELD = "srcMachineId";
    public static final String DST_MACHINE_ID_FIELD = "dstMachineId";
    public static final String SRC_MACHINE_NAME_REGEX_CLUSTER_FIELD = "srcMachineNameRegexCluster";
    public static final String DST_MACHINE_NAME_REGEX_CLUSTER_FIELD = "dstMachineNameRegexCluster";
    public static final String DST_MACHINE_DOMAIN_FIELD = "dstMachineDomain";
    public static final String SITE_FIELD = "site";
    public static final String CITY_FIELD = "city";
    public static final String COUNTRY_FIELD = "country";
    public static final String OPERATION_TYPE_FIELD = "operationType";
    public static final String OPERATION_TYPE_CATEGORIES_FIELD = "operationTypeCategories";
    public static final String RESULT_FIELD = "result";
    public static final String RESULT_CODE_FIELD = "resultCode";


    public AuthenticationEnrichedEvent() {
        super();
    }

    public AuthenticationEnrichedEvent(Instant createdDate, Instant eventDate, String eventId, String schema, String userId, String userName, String userDisplayName, String dataSource, String operationType, List<String> operationTypeCategories, EventResult result, String resultCode, Map<String, String> additionalInfo) {
        super(createdDate, eventDate, eventId, schema, userId, userName, userDisplayName, dataSource, additionalInfo);
        this.operationType = operationType;
        this.operationTypeCategories = operationTypeCategories;
        this.result = result;
        this.resultCode = resultCode;
    }

    @Field(SRC_MACHINE_ID_FIELD)
    private String srcMachineId;

    @Field(DST_MACHINE_ID_FIELD)
    private String dstMachineId;

    @Field(SRC_MACHINE_NAME_REGEX_CLUSTER_FIELD)
    private String srcMachineNameRegexCluster;

    @Field(DST_MACHINE_NAME_REGEX_CLUSTER_FIELD)
    private String dstMachineNameRegexCluster;

    @Field(DST_MACHINE_DOMAIN_FIELD)
    private String dstMachineDomain;

    @Field(SITE_FIELD)
    private String site;

    @Field(CITY_FIELD)
    private String city;

    @Field(COUNTRY_FIELD)
    private String country;

    @Field(OPERATION_TYPE_FIELD)
    private String operationType;

    @Field(OPERATION_TYPE_CATEGORIES_FIELD)
    private List<String> operationTypeCategories;

    @Field(RESULT_FIELD)
    private EventResult result;

    @Field(RESULT_CODE_FIELD)
    private String resultCode;

    public void setSrcMachineId(String srcMachineId) {
        this.srcMachineId = srcMachineId;
    }

    public void setDstMachineId(String dstMachineId) {
        this.dstMachineId = dstMachineId;
    }

    public void setSrcMachineNameRegexCluster(String srcMachineNameRegexCluster) {
        this.srcMachineNameRegexCluster = srcMachineNameRegexCluster;
    }

    public void setDstMachineNameRegexCluster(String dstMachineNameRegexCluster) {
        this.dstMachineNameRegexCluster = dstMachineNameRegexCluster;
    }

    public void setDstMachineDomain(String dstMachineDomain) {
        this.dstMachineDomain = dstMachineDomain;
    }

    public String getSrcMachineId() {
        return srcMachineId;
    }

    public String getDstMachineId() {
        return dstMachineId;
    }

    public String getSrcMachineNameRegexCluster() {
        return srcMachineNameRegexCluster;
    }

    public String getDstMachineNameRegexCluster() {
        return dstMachineNameRegexCluster;
    }

    public String getDstMachineDomain() {
        return dstMachineDomain;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getOperationType() {
        return operationType;
    }

    public List<String> getOperationTypeCategories() {
        return operationTypeCategories;
    }

    public EventResult getResult() {
        return result;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public void setOperationTypeCategories(List<String> operationTypeCategories) {
        this.operationTypeCategories = operationTypeCategories;
    }

    public void setResult(EventResult result) {
        this.result = result;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

}
