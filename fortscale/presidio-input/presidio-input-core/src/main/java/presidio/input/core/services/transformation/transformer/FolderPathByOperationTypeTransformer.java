package presidio.input.core.services.transformation.transformer;

import fortscale.utils.logging.Logger;
import fortscale.utils.reflection.PresidioReflectionUtils;
import org.springframework.util.CollectionUtils;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class FolderPathByOperationTypeTransformer implements Transformer {

    private static final Logger logger = Logger.getLogger(FolderPathByOperationTypeTransformer.class);
    private final FileToFolderPathTransformer folderPathTransformer;
    private final String filePathFieldName;
    private final String inputPathFieldName;
    private final String operationTypeFieldName;
    private final String folderPathFieldName;
    private final List<String> folderOperations;

    public FolderPathByOperationTypeTransformer(String inputPathFieldName, String filePathFieldName, String folderPathFieldName, String operationTypeFieldName, List<String> folderOperations) {
        this.folderPathTransformer = new FileToFolderPathTransformer(inputPathFieldName, folderPathFieldName);
        this.inputPathFieldName = inputPathFieldName;
        this.filePathFieldName = filePathFieldName;
        this.folderPathFieldName = folderPathFieldName;
        this.operationTypeFieldName = operationTypeFieldName;
        this.folderOperations = folderOperations;
    }

    @Override
    /*
     * If the events operation type is in the folderOperations list the received path is a folder path.
     * If the events operation type is not in the list - extract the folder path from the received path.
     */
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {

        documents.forEach((AbstractInputDocument document) -> {

                    String filePathValue = (String) PresidioReflectionUtils.getFieldValue(document, inputPathFieldName);

                    if (isNotBlank(filePathValue)) {
                        if (isFolderOperation(document)) {
                            try {
                                PresidioReflectionUtils.setFieldValue(document, folderPathFieldName, filePathValue);
                                PresidioReflectionUtils.setFieldValue(document, filePathFieldName, null);
                            } catch (IllegalAccessException e) {
                                logger.error("error setting one of {} {} field values", filePathFieldName, folderPathFieldName, e);
                            }
                        } else {
                            this.folderPathTransformer.transform(Collections.singletonList(document));
                        }
                    }
                }
        );

        return documents;
    }

    private boolean isFolderOperation(AbstractInputDocument document) {
        String operationType = (String) PresidioReflectionUtils.getFieldValue(document, operationTypeFieldName);
        return CollectionUtils.contains(folderOperations.iterator(), operationType);
    }
}
