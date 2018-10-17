package presidio.input.core.services.transformation.managers;

import fortscale.common.general.Schema;
import org.springframework.beans.factory.annotation.Value;
import presidio.input.core.services.transformation.transformer.FolderPathByOperationTypeTransformer;
import presidio.input.core.services.transformation.transformer.OperationTypeCategoriesHierarchyTransformer;
import presidio.input.core.services.transformation.transformer.OperationTypeToCategoriesTransformer;
import presidio.input.core.services.transformation.transformer.Transformer;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.FileRawEvent;
import presidio.sdk.api.domain.transformedevents.FileTransformedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FileTransformerManager implements TransformationManager {

    @Value("${folder.operation.types}")
    private String[] folderOperationTypes;
    private List<Transformer> transformers;
    private Map<Schema, Map<String, List<String>>> operationTypeToCategoriesMapping;
    private Map<Schema, Map<String, List<String>>> operationTypeCategoriesHierarchyMapping;

    public FileTransformerManager(Map<Schema, Map<String, List<String>>> operationTypeToCategoriesMapping,  Map<Schema, Map<String, List<String>>> operationTypeCategoriesHierarchyMapping) {
        this.operationTypeToCategoriesMapping = operationTypeToCategoriesMapping;
        this.operationTypeCategoriesHierarchyMapping = operationTypeCategoriesHierarchyMapping;
    }

    @Override
    public List<Transformer> getTransformers() {
        if (transformers == null) {

            List<String> folderOperations = Arrays.asList(folderOperationTypes);
            transformers = new ArrayList<>();
            transformers.add(new FolderPathByOperationTypeTransformer(FileRawEvent.SRC_FILE_PATH_FIELD_NAME, FileRawEvent.SRC_FILE_PATH_FIELD_NAME,
                    FileTransformedEvent.SRC_FOLDER_PATH_FIELD_NAME, FileRawEvent.OPERATION_TYPE_FIELD_NAME, folderOperations));
            transformers.add(new FolderPathByOperationTypeTransformer(FileRawEvent.DST_FILE_PATH_FIELD_NAME, FileRawEvent.DST_FILE_PATH_FIELD_NAME,
                    FileTransformedEvent.DST_FOLDER_PATH_FIELD_NAME, FileRawEvent.OPERATION_TYPE_FIELD_NAME, folderOperations));
            transformers.add(new OperationTypeToCategoriesTransformer(operationTypeToCategoriesMapping.get(Schema.FILE), FileRawEvent.OPERATION_TYPE_FIELD_NAME, FileRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME,  FileRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME));
            transformers.add(new OperationTypeCategoriesHierarchyTransformer(operationTypeCategoriesHierarchyMapping.get(Schema.FILE), FileRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME, FileRawEvent.OPERATION_TYPE_CATEGORIES_FIELD_NAME));
        }
        return transformers;
    }

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) new FileTransformedEvent((FileRawEvent) rawEvent);
    }
}
