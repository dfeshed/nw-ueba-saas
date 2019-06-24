package presidio.input.core.services.transformation.factory;

import fortscale.common.general.Schema;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.context.ApplicationContext;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class SessionSplitTransformerConf implements FactoryConfig {
    public static final String SESSION_SPLIT_TRANSFORMER_FACTORY_NAME = "split_transformer";
    private static final int ZERO_SESSION_SPLIT = 0;

    private Schema schema;
    private Instant endDate;
    private int zeroSessionSplit = ZERO_SESSION_SPLIT;

    public SessionSplitTransformerConf(Schema schema, Instant endDate) {
        this.schema = schema;
        this.endDate = endDate;
    }

    public Schema getSchema() {
        return schema;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public int getZeroSessionSplit() {
        return zeroSessionSplit;
    }

    public List<String> getProjectionFields() {
        List<String> projectionFields = new ArrayList<>();
        projectionFields.add(TlsRawEvent.SOURCE_IP_FIELD_NAME);
        projectionFields.add(TlsRawEvent.DESTINATION_IP_FIELD_NAME);
        projectionFields.add(TlsRawEvent.SOURCE_PORT_FIELD_NAME);
        projectionFields.add(TlsRawEvent.DESTINATION_PORT_FIELD_NAME);
        projectionFields.add(TlsRawEvent.JA3_FIELD_NAME);
        projectionFields.add(TlsRawEvent.JA3S_FIELD_NAME);
        projectionFields.add(TlsRawEvent.DATE_TIME_FIELD_NAME);
        projectionFields.add(TlsRawEvent.SESSION_SPLIT_FIELD_NAME);
        projectionFields.add(TlsRawEvent.SSL_SUBJECT_FIELD_NAME);
        projectionFields.add(TlsRawEvent.SSL_CA_FIELD_NAME);
        return projectionFields;
    }

    @Override
    public String getFactoryName() {
        return SESSION_SPLIT_TRANSFORMER_FACTORY_NAME;
    }
}
