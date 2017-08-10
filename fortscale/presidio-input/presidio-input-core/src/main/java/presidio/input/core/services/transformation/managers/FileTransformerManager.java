package presidio.input.core.services.transformation.managers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import presidio.input.core.services.transformation.FolderPathTransformer;
import presidio.sdk.api.domain.AbstractPresidioDocument;
import presidio.sdk.api.domain.rawevents.FileRawEvent;
import presidio.sdk.api.domain.transformedevents.FileTransformedEvent;

import java.util.Set;

@Component("FILE")
public class FileTransformerManager extends TransformationManager {

    @Value("${folder_operation_types}")
    private Set<String> folderOperations;

    public FileTransformerManager() {

        transformers.add(new FolderPathTransformer(FileRawEvent.SRC_FILE_PATH_FIELD_NAME, FileRawEvent.SRC_FILE_PATH_FIELD_NAME,
                "srcFolderPath", FileRawEvent.OPERATION_TYPE_FIELD_NAME, folderOperations));

        transformers.add(new FolderPathTransformer(FileRawEvent.DST_FILE_PATH_FIELD_NAME, FileRawEvent.DST_FILE_PATH_FIELD_NAME,
                "dstFolderPath", FileRawEvent.OPERATION_TYPE_FIELD_NAME, folderOperations));
    }

    @Override
    protected <U extends AbstractPresidioDocument> U getTransformedDocument(AbstractPresidioDocument rawEvent) {
        return (U) new FileTransformedEvent((FileRawEvent) rawEvent);
    }
}
