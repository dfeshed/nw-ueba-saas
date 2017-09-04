package presidio.input.core.services.transformation.managers;

import presidio.input.core.services.transformation.OperationTypeCategoryTransformer;
import presidio.input.core.services.transformation.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActiveDirectoryTransformationManager implements TransformationManager {

    private List<Transformer> transformers;

    @Override
    public List<Transformer> getTransformers() {
        if (transformers == null) {
            transformers = new ArrayList<>();
            Map<String, List<String>> operationTypeCategoryMapping = null;
            transformers.add(new OperationTypeCategoryTransformer(operationTypeCategoryMapping));
        }
        return transformers;
    }

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) rawEvent;
    }
}
