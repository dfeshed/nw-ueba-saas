package presidio.input.core.services.transformation.managers;

import fortscale.common.general.Schema;
import fortscale.utils.factory.FactoryService;
import presidio.input.core.services.transformation.factory.SessionSplitTransformerConf;
import presidio.input.core.services.transformation.transformer.SessionSplitTransformer.SessionSplitTransformer;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;
import presidio.sdk.api.domain.transformedevents.TlsTransformedEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TlsTransformerManager implements TransformationManager {

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
    }

    @Override
    public List<Transformer> getTransformers() {
        return transformers;
    }

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) new TlsTransformedEvent((TlsRawEvent) rawEvent);
    }
}
