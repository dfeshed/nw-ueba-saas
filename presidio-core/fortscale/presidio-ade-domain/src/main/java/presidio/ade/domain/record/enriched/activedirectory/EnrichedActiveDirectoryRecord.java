package presidio.ade.domain.record.enriched.activedirectory;

import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.util.AdeRecordMetadata;

import java.time.Instant;
import java.util.List;

/**
 * The enriched active directory record POJO.
 */
@Document
@AdeRecordMetadata(adeEventType = Schema.ACTIVE_DIRECTORY)
public class EnrichedActiveDirectoryRecord extends EnrichedRecord {
    public static final String USER_ID_FIELD = "userId";
    public static final String SRC_MACHINE_ID_FIELD = "srcMachineId";
    public static final String SRC_MACHINE_NAME_REGEX_CLUSTER_FIELD = "srcMachineNameRegexCluster";
    public static final String OBJECT_ID_FIELD = "objectId";
    public static final String OPERATION_TYPE_FIELD = "operationType";
    public static final String OPERATION_TYPE_CATEGORIES_FIELD = "operationTypeCategories";
    public static final String RESULT_FIELD = "result";
    public static final String RESULT_CODE_FIELD = "resultCode";

    @Field(USER_ID_FIELD)
    private String userId;
    @Field(SRC_MACHINE_ID_FIELD)
    private String srcMachineId;
    @Field(SRC_MACHINE_NAME_REGEX_CLUSTER_FIELD)
    private String srcMachineNameRegexCluster;
    @Field(OBJECT_ID_FIELD)
    private String objectId;
    @Field(OPERATION_TYPE_FIELD)
    private String operationType;
    @Field(OPERATION_TYPE_CATEGORIES_FIELD)
    private List<String> operationTypeCategories;
    @Field(RESULT_FIELD)
    private EventResult result;
    @Field(RESULT_CODE_FIELD)
    private String resultCode;

    /**
     * C'tor.
     *
     * @param startInstant The record's logical time
     */
    public EnrichedActiveDirectoryRecord(Instant startInstant) {
        super(startInstant);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSrcMachineId() {
        return srcMachineId;
    }

    public void setSrcMachineId(String srcMachineId) {
        this.srcMachineId = srcMachineId;
    }

    public String getSrcMachineNameRegexCluster() {
        return srcMachineNameRegexCluster;
    }

    public void setSrcMachineNameRegexCluster(String srcMachineNameRegexCluster) {
        this.srcMachineNameRegexCluster = srcMachineNameRegexCluster;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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

    public EventResult getResult() {
        return result;
    }

    public void setResult(EventResult result) {
        this.result = result;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    @Transient
    public String getAdeEventType() {
        return Schema.ACTIVE_DIRECTORY.getName();
    }

    @Transient
    public AdeEnrichedActiveDirectoryContext getContext() {
        return new AdeEnrichedActiveDirectoryContext(this);
    }
}
