package presidio.ade.sdk.executions.online;

import presidio.ade.domain.store.input.ADEInputRecord;

import java.time.Instant;

/**
 * shouldInsertDataAndCreateIndexes pojo used to mock ade input data
 * Created by barak_schuster on 5/28/17.
 */
public class ADEMockedInputRecord extends ADEInputRecord {

    private String contextField;
    private String featureField;

    public ADEMockedInputRecord(Instant eventTime, String contextField, String featureField) {
        super(eventTime);
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
