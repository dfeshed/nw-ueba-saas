package presidio.input.core.services.transformation.managers;

import fortscale.common.general.Schema;
import org.springframework.beans.factory.annotation.Value;
import presidio.input.core.services.transformation.FolderPathTransformer;
import presidio.input.core.services.transformation.OperationTypeCategoryTransformer;
import presidio.input.core.services.transformation.Transformer;
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
    private Map<Schema, Map<String, List<String>>> operationTypeToCategoryMapping;

    public FileTransformerManager(Map<Schema, Map<String, List<String>>> operationTypeToCategoryMapping) {
        this.operationTypeToCategoryMapping = operationTypeToCategoryMapping;
    }

    @Override
    public List<Transformer> getTransformers() {
        if (transformers == null) {

            List<String> folderOperations = Arrays.asList(folderOperationTypes);
            transformers = new ArrayList<>();
            transformers.add(new FolderPathTransformer(FileRawEvent.SRC_FILE_PATH_FIELD_NAME, FileRawEvent.SRC_FILE_PATH_FIELD_NAME,
                    FileTransformedEvent.SRC_FOLDER_PATH_FIELD_NAME, FileRawEvent.OPERATION_TYPE_FIELD_NAME, folderOperations));
            transformers.add(new FolderPathTransformer(FileRawEvent.DST_FILE_PATH_FIELD_NAME, FileRawEvent.DST_FILE_PATH_FIELD_NAME,
                    FileTransformedEvent.DST_FOLDER_PATH_FIELD_NAME, FileRawEvent.OPERATION_TYPE_FIELD_NAME, folderOperations));
            transformers.add(new OperationTypeCategoryTransformer(operationTypeToCategoryMapping.get(Schema.FILE.toString())));
        }
        return transformers;
    }

    @Override
    public <U extends AbstractInputDocument> U getTransformedDocument(AbstractInputDocument rawEvent) {
        return (U) new FileTransformedEvent((FileRawEvent) rawEvent);
    }
}
