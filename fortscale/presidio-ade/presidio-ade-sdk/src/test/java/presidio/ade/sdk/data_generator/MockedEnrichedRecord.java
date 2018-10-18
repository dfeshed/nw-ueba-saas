package presidio.ade.sdk.data_generator;

import fortscale.domain.core.EventResult;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * @author Barak Schuster
 */
@Document
public class MockedEnrichedRecord extends EnrichedRecord {
    private String contextField;
    private String featureField;
    private String operationType;
    private List<String> operationTypeCategories;
    private EventResult result;
    private String resultCode;

    public MockedEnrichedRecord(Instant startInstant, String contextField, String featureField) {
        super(startInstant);
        this.contextField = contextField;
        this.featureField = featureField;
    }

    public String getContextField() {
        return contextField;
    }

    public void setContextField(String contextField) {
        this.contextField = contextField;
    }

    public String getFeatureField() {
        return featureField;
    }

    public void setFeatureField(String featureField) {
        this.featureField = featureField;
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
    public String getAdeEventType() {
        return "mockedAdeEventType";
    }

    @Override
    public List<String> getDataSources() {
        return Collections.singletonList(getAdeEventType());
    }
}
