package presidio.input.core.services.transformation.managers;

import fortscale.common.general.Schema;
import presidio.input.core.services.transformation.transformer.OperationTypeCategoriesHierarchyTransformer;
import presidio.input.core.services.transformation.transformer.OperationTypeToCategoriesTransformer;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.ActiveDirectoryRawEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActiveDirectoryTransformationManager implements TransformationManager {

    private List<Transformer> transformers;
    private Map<Schema, Map<String, List<String>>> operationTypeToCategoriesMapping;
    private Map<Schema, Map<String, List<String>>> operationTypeCategoriesHierarchyMapping;

    public ActiveDirectoryTransformationManager(Map<Schema, Map<String, List<String>>> operationTypeToCategoriesMapping, Map<Schema, Map<String, List<String>>> operationTypeCategoriesHierarchyMapping) {
        this.operationTypeToCategoriesMapping = operationTypeToCategoriesMapping;
        this.operationTypeCategoriesHierarchyMapping = operationTypeCategoriesHierarchyMapping;
    }

    @Override
    public List<Transformer> getTransformers() {
        if (transformers == null) {
            transformers = new ArrayList<>();
            transformers.add(new OperationTypeToCategoriesTransformer(operationTypeToCategoriesMapping.get(Schema.ACTIVE_DIRECTORY), ActiveDirectoryRawEvent.OPERATION_TYPE_FIELD_NAME, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME,  ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME));
            transformers.add(new OperationTypeCategoriesHierarchyTransformer(operationTypeCategoriesHierarchyMapping.get(Schema.ACTIVE_DIRECTORY), ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, ActiveDirectoryRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME));
        }
        return transformers;
    }

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) rawEvent;
    }
}
