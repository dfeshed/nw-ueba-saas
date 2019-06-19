package presidio.input.core.services.transformation.factory;

import fortscale.common.general.Schema;
import fortscale.utils.factory.FactoryConfig;
import org.springframework.context.ApplicationContext;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;

import java.time.Instant;
import java.util.List;


public class SessionSplitTransformerConf implements FactoryConfig {
    public static final String SESSION_SPLIT_TRANSFORMER_FACTORY_NAME = "split_transformer";

    private Schema schema;
    private Instant endDate;

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

    @Override
    public String getFactoryName() {
        return SESSION_SPLIT_TRANSFORMER_FACTORY_NAME;
    }
}
