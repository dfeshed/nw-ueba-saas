package presidio.input.core.services.transformation.managers;

import fortscale.common.general.Schema;
import presidio.input.core.services.transformation.transformer.OperationTypeCategoryHierarchyTransformer;
import presidio.input.core.services.transformation.transformer.OperationTypeToCategoryTransformer;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActiveDirectoryTransformationManager implements TransformationManager {

    private List<Transformer> transformers;
    private Map<Schema, Map<String, List<String>>> operationTypeToCategoryMapping;
    private Map<Schema, Map<String, List<String>>> operationTypeCategoryHierarchyMapping;

    public ActiveDirectoryTransformationManager(Map<Schema, Map<String, List<String>>> operationTypeToCategoryMapping, Map<Schema, Map<String, List<String>>> operationTypeCategoryHierarchyMapping) {
        this.operationTypeToCategoryMapping = operationTypeToCategoryMapping;
        this.operationTypeCategoryHierarchyMapping = operationTypeCategoryHierarchyMapping;
    }

    @Override
    public List<Transformer> getTransformers() {
        if (transformers == null) {
            transformers = new ArrayList<>();
            transformers.add(new OperationTypeToCategoryTransformer(operationTypeToCategoryMapping.get(Schema.ACTIVE_DIRECTORY)));
            transformers.add(new OperationTypeCategoryHierarchyTransformer(operationTypeCategoryHierarchyMapping.get(Schema.ACTIVE_DIRECTORY)));
        }
        return transformers;
    }

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) rawEvent;
    }
}
