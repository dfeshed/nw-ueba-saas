package presidio.ade.sdk.data_generator;

import org.springframework.data.mongodb.core.index.Indexed;
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
        return "mockedAdeEventType";
    }

    @Override
    public List<String> getDataSources() {
        return Collections.singletonList(getAdeEventType());
    }
}
