package presidio.input.core.services.transformation.managers;

import fortscale.common.general.Schema;
import fortscale.utils.factory.FactoryService;
import presidio.input.core.services.transformation.factory.NewOccurrenceTransformerConf;
import presidio.input.core.services.transformation.factory.SessionSplitTransformerConf;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;
import presidio.sdk.api.domain.transformedevents.TlsTransformedEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TlsTransformerManager implements TransformationManager {

    private static final String NAME_FIELD_SUFFIX = ".name";
    private static final String NEW_OCCURRENCE_FIELD_SUFFIX = ".isNewOccurrence";
    private static final List<String> NEW_OCCURRENCE_FIELD_NAMES = Arrays.asList(
            TlsRawEvent.DOMAIN_FIELD_NAME,
            TlsRawEvent.SSL_SUBJECT_FIELD_NAME,
            TlsRawEvent.JA3_FIELD_NAME,
            TlsRawEvent.DESTINATION_ORGANIZATION_FIELD_NAME,
            TlsRawEvent.DESTINATION_COUNTRY_FIELD_NAME,
            TlsRawEvent.DESTINATION_PORT_FIELD_NAME,
            TlsRawEvent.DESTINATION_ASN_FIELD_NAME);
    private static final long NUM_DAYS_HALF_YEAR = 182;
    private static final Duration EXPIRATION_DELTA = Duration.ofDays(NUM_DAYS_HALF_YEAR);
    private List<Transformer> transformers = new ArrayList<>();
    private FactoryService<Transformer> transformerFactoryService;


    public TlsTransformerManager(FactoryService<Transformer> transformerFactoryService) {
        this.transformerFactoryService = transformerFactoryService;
    }

    @Override
    public void init(Instant endDate) {
        SessionSplitTransformerConf sessionSplitTransformerConf = new SessionSplitTransformerConf(Schema.TLS, endDate);
        transformers.add(transformerFactoryService.getProduct(sessionSplitTransformerConf));
        NEW_OCCURRENCE_FIELD_NAMES.forEach(fieldName -> transformers.add(createNewOccurrenceTransformer(fieldName)));
    }

    @Override
    public List<Transformer> getTransformers() {
        return transformers;
    }

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) new TlsTransformedEvent((TlsRawEvent) rawEvent);
    }

    private Transformer createNewOccurrenceTransformer(String entityType) {
        NewOccurrenceTransformerConf newOccurrenceTransformerConf = new NewOccurrenceTransformerConf(Schema.TLS, entityType + NAME_FIELD_SUFFIX,
                EXPIRATION_DELTA, entityType + NEW_OCCURRENCE_FIELD_SUFFIX);
        return transformerFactoryService.getProduct(newOccurrenceTransformerConf);
    }
}
