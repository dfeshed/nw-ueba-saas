package presidio.ade.domain.record.enriched;

import fortscale.domain.core.EventResult;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.AdeRecord;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * A basic ADE enriched record. All ADE enriched records (across all data sources) should extend this one.
 * <p>
 * Created by Lior Govrin on 06/06/2017.
 */
@Document
public abstract class EnrichedRecord extends AdeRecord {

    public static final String EVENT_ID_FIELD = "eventId";
    public static final String DATA_SOURCE_FIELD = "dataSource";
    public static final String OPERATION_TYPE_FIELD = "operationType";
    public static final String OPERATION_TYPE_CATEGORIES_FIELD = "operationTypeCategories";
    public static final String RESULT_FIELD = "result";
    public static final String RESULT_CODE_FIELD = "resultCode";


    @Field(EVENT_ID_FIELD)
    @Indexed
    private String eventId;
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




    public EnrichedRecord(Instant startInstant) {
        super(startInstant);
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
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
    public List<String> getDataSources() {
        return Collections.singletonList(getDataSource());
    }
}
