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

        List<AbstractPresidioDocument> result = new ArrayList<>();

        events.forEach(event -> {
            AbstractPresidioDocument transformedDocument = getTransformedDocument(event);
            List<AbstractPresidioDocument> transformedDocuments = Arrays.asList(transformedDocument);
            if (CollectionUtils.isEmpty(transformers)){
                result.add(transformedDocument);
            }else {
                transformers.forEach(transformer -> {
                    transformer.transform(transformedDocuments);
                });
                result.addAll(transformedDocuments);
            }
        });

        return result;
    }
}
