package presidio.input.core.services.transformation.managers;

import org.apache.commons.collections.CollectionUtils;
import presidio.input.core.services.transformation.Transformer;
import presidio.sdk.api.domain.AbstractPresidioDocument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class TransformationManager {
    protected List<Transformer> transformers;

    public TransformationManager() {
        transformers = new ArrayList<>();
    }

    abstract protected <U extends AbstractPresidioDocument> U getTransformedDocument(AbstractPresidioDocument rawEvent);

    public List<AbstractPresidioDocument> run(List<AbstractPresidioDocument> events) {

        List<AbstractPresidioDocument> transformedEvents = new ArrayList<>();

        events.forEach(event -> {
            AbstractPresidioDocument transformedDocument = getTransformedDocument(event);
            if (CollectionUtils.isEmpty(transformers)){
                transformedEvents.add(transformedDocument);
            }else {
                transformers.forEach(transformer -> {
                    transformedEvents.addAll(transformer.transform(Arrays.asList(transformedDocument)));
                });
            }
        });

        return transformedEvents;
    }
}
