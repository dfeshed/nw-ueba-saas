package presidio.input.core.services.transformation.managers;

import fortscale.common.general.Schema;
import presidio.input.core.services.transformation.transformer.OperationTypeCategoryTransformer;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActiveDirectoryTransformationManager implements TransformationManager {

    private List<Transformer> transformers;
    private Map<Schema, Map<String, List<String>>> operationTypeToCategoryMapping;

    public ActiveDirectoryTransformationManager(Map<Schema, Map<String, List<String>>> operationTypeToCategoryMapping) {
        this.operationTypeToCategoryMapping = operationTypeToCategoryMapping;
    }

    @Override
    public List<Transformer> getTransformers() {
        if (transformers == null) {
            transformers = new ArrayList<>();
            transformers.add(new OperationTypeCategoryTransformer(operationTypeToCategoryMapping.get(Schema.ACTIVE_DIRECTORY.toString())));
        }
        return transformers;
    }

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) rawEvent;
    }
}
