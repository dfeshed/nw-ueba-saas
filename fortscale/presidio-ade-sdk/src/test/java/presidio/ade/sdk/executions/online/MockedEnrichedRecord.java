package presidio.ade.sdk.executions.online;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.time.Instant;

/**
 * shouldInsertDataAndCreateIndexes pojo used to mock ade enriched data
 * Created by barak_schuster on 5/28/17.
 */
@Document
public class MockedEnrichedRecord extends EnrichedRecord {
    @Indexed
    private String contextField;
    @Indexed
    private String featureField;

    public MockedEnrichedRecord(Instant date_time, String contextField, String featureField) {
        super(date_time);
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
}
