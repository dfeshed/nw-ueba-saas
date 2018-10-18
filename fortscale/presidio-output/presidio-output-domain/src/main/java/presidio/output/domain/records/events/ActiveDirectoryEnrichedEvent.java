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
public class ActiveDirectoryEnrichedEvent extends EnrichedEvent{

    public static final String OBJECT_ID_FIELD = "objectId";
    public static final String OPERATION_TYPE_FIELD = "operationType";
    public static final String OPERATION_TYPE_CATEGORIES_FIELD = "operationTypeCategories";
    public static final String RESULT_FIELD = "result";
    public static final String RESULT_CODE_FIELD = "resultCode";

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

    public ActiveDirectoryEnrichedEvent() {
    }

    public ActiveDirectoryEnrichedEvent(Instant createdDate,
                                        Instant eventDate,
                                        String eventId,
                                        String schema,
                                        String userId,
                                        String userName,
                                        String userDisplayName,
                                        String dataSource,
                                        String operationType,
                                        List<String> operationTypeCategories,
                                        EventResult result,
                                        String resultCode,
                                        Map<String, String> additionalInfo,
                                        String objectId) {
        super(createdDate, eventDate, eventId, schema, userId, userName, userDisplayName, dataSource, additionalInfo);
        this.objectId = objectId;
        this.operationType = operationType;
        this.operationTypeCategories = operationTypeCategories;
        this.result = result;
        this.resultCode = resultCode;
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
