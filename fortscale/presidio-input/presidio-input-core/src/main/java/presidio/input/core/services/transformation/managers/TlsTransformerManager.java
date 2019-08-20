package presidio.input.core.services.transformation.managers;

import fortscale.common.general.Schema;
import fortscale.utils.factory.FactoryService;
import presidio.input.core.services.transformation.factory.NewOccurrenceTransformerConf;
import presidio.input.core.services.transformation.factory.SessionSplitTransformerConf;
import presidio.input.core.services.transformation.transformer.NewOccurrenceTransformer;
import presidio.input.core.services.transformation.transformer.SessionSplitTransformer.SessionSplitTransformer;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;
import presidio.sdk.api.domain.transformedevents.TlsTransformedEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TlsTransformerManager implements TransformationManager {

    private static final String NAME_FIELD_PREFIX = ".name";
    private static final String NEW_OCCURRENCE_FIELD_PREFIX = ".isNewOccurrence";
    private static final List<String> NEW_OCCURRENCE_FIELD_NAMES = new ArrayList<>(Arrays.asList(TlsRawEvent.DOMAIN_FIELD_NAME,
            TlsRawEvent.SSL_SUBJECT_FIELD_NAME, TlsRawEvent.JA3_FIELD_NAME, TlsRawEvent.DESTINATION_ORGANIZATION_FIELD_NAME,
            TlsRawEvent.DESTINATION_COUNTRY_FIELD_NAME, TlsRawEvent.DESTINATION_PORT_FIELD_NAME));
    private List<Transformer> transformers = new ArrayList<>();
    private FactoryService<Transformer> transformerFactoryService;


    public TlsTransformerManager(FactoryService<Transformer> transformerFactoryService){
        this.transformerFactoryService = transformerFactoryService;
    }

    @Override
    public void init(Instant endDate){
        SessionSplitTransformerConf sessionSplitTransformerConf = new SessionSplitTransformerConf(Schema.TLS, endDate);
        SessionSplitTransformer sessionSplitTransformer = (SessionSplitTransformer) transformerFactoryService.getProduct(sessionSplitTransformerConf);
        transformers.add(sessionSplitTransformer);
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

    private NewOccurrenceTransformer createNewOccurrenceTransformer(String entityType) {
        NewOccurrenceTransformerConf newOccurrenceTransformerConf = new NewOccurrenceTransformerConf(Schema.TLS, entityType + NAME_FIELD_PREFIX,
                , entityType + NEW_OCCURRENCE_FIELD_PREFIX);
        return (NewOccurrenceTransformer) transformerFactoryService.getProduct(newOccurrenceTransformerConf);
    }
}
