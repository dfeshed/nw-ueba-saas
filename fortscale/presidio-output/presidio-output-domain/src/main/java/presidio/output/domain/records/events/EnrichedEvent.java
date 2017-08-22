package presidio.output.domain.records.events;

import fortscale.domain.core.EventResult;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * A basic Output enriched event document.
 * All Output enriched event records (across all data sources) should extend this one.
 * <p>
 * Created by Efrat Noam on 02/08/2017.
 */
@Document
public class EnrichedEvent {

    public static final String EVENT_ID_FIELD = "eventId";
    public static final String SCHEMA_FIELD = "schema";
    public static final String DATA_SOURCE_FIELD = "dataSource";
    public static final String OPERATION_TYPE_FIELD = "operationType";
    public static final String OPERATION_TYPE_CATEGORIES_FIELD = "operationTypeCategories";
    public static final String RESULT_FIELD = "result";
    public static final String RESULT_CODE_FIELD = "resultCode";
    public static final String USERNAME_FIELD = "userName";
    public static final String USER_DISPLAY_NAME_FIELD = "userdisplayName";
    public static final String ADDITIONAL_INFO = "additionalInfo";
    public static final String USER_ID_FIELD = "userId";
    public static final String START_INSTANT_FIELD = "eventDate";

    @Id
    @Indexed
    @Field
    private String id;

    @CreatedDate
    private Instant createdDate;

    @Field(START_INSTANT_FIELD)
    private Instant eventDate;

    @Field(EVENT_ID_FIELD)
    private String eventId;

    @Field(SCHEMA_FIELD)
    private String schema;

    @Indexed
    @Field(USER_ID_FIELD)
    private String userId;

    @Field(USERNAME_FIELD)
    private String userName;

    @Field(USER_DISPLAY_NAME_FIELD)
    private String userDisplayName;

    @Field(DATA_SOURCE_FIELD)
    private String dataSource;

    @Field(OPERATION_TYPE_FIELD)
    private String operationType;

    @Field(OPERATION_TYPE_CATEGORIES_FIELD)
    private List<String> operationTypeCategories;

    @Field(RESULT_FIELD)
    private EventResult result;

    @Field(RESULT_CODE_FIELD)
    private String resultCode;

    @Field(ADDITIONAL_INFO)
    private Map<String,String> additionalnfo;

    public EnrichedEvent() {}

    public EnrichedEvent(Instant createdDate,
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
                         Map<String, String> additionalnfo) {
        this.createdDate = createdDate;
        this.eventDate = eventDate;
        this.eventId = eventId;
        this.schema = schema;
        this.userId = userId;
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.dataSource = dataSource;
        this.operationType = operationType;
        this.operationTypeCategories = operationTypeCategories;
        this.result = result;
        this.resultCode = resultCode;
        this.additionalnfo = additionalnfo;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public String getDataSource() {
        return dataSource;
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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
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

    public void setAdditionalnfo(Map<String, String> additionalnfo) {
        this.additionalnfo = additionalnfo;
    }

    public String getUserId() {
        return userId;
    }

    public Map<String, String> getAdditionalnfo() {
        return additionalnfo;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Instant getEventDate() {
        return eventDate;
    }

    public void setEventDate(Instant eventDate) {
        this.eventDate = eventDate;
    }
}
