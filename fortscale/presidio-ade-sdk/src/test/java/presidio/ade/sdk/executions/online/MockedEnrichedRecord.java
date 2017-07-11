package presidio.ade.sdk.executions.online;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

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

    @Override
    public String getAdeEventType() {
        return "mockedDataSource";
    }

    @Override
    public List<String> getDataSources() {
        return Collections.singletonList(getAdeEventType());
    }
}
