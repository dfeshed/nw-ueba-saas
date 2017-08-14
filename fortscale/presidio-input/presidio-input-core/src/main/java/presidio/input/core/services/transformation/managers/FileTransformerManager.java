package presidio.input.core.services.transformation.managers;

import org.springframework.beans.factory.annotation.Value;
import presidio.input.core.services.transformation.FolderPathTransformer;
import presidio.input.core.services.transformation.Transformer;
import presidio.sdk.api.domain.AbstractPresidioDocument;
import presidio.sdk.api.domain.rawevents.FileRawEvent;
import presidio.sdk.api.domain.transformedevents.FileTransformedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileTransformerManager implements TransformationManager {

    @Value("${folder_operation_types}")
    private String[] folderOperationTypes;
    private List<Transformer> transformers;

    @Override
    public List<Transformer> getTransformers() {
        if (transformers == null) {

            List<String> folderOperations = Arrays.asList(folderOperationTypes);
            transformers = new ArrayList<>();
            transformers.add(new FolderPathTransformer(FileRawEvent.SRC_FILE_PATH_FIELD_NAME, FileRawEvent.SRC_FILE_PATH_FIELD_NAME,
                    "srcFolderPath", FileRawEvent.OPERATION_TYPE_FIELD_NAME, folderOperations));

            transformers.add(new FolderPathTransformer(FileRawEvent.DST_FILE_PATH_FIELD_NAME, FileRawEvent.DST_FILE_PATH_FIELD_NAME,
                    "dstFolderPath", FileRawEvent.OPERATION_TYPE_FIELD_NAME, folderOperations));
        }
        return transformers;
    }

    @Override
    public <U extends AbstractPresidioDocument> U getTransformedDocument(AbstractPresidioDocument rawEvent) {
        return (U) new FileTransformedEvent((FileRawEvent) rawEvent);
    }
}
