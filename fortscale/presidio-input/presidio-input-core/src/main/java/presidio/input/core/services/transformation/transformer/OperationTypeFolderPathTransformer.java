package presidio.input.core.services.transformation.transformer;

import fortscale.utils.logging.Logger;
import org.springframework.util.CollectionUtils;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.utils.ReflectionUtils;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class OperationTypeFolderPathTransformer implements Transformer {

    private static final Logger logger = Logger.getLogger(OperationTypeFolderPathTransformer.class);
    private final FileToFolderPathTransformer folderPathTransformer;
    private final String filePathFieldName;
    private final String inputPathFieldName;
    private final String operationTypeFieldName;
    private final String folderPathFieldName;
    private final List<String> folderOperations;

    public OperationTypeFolderPathTransformer(String inputPathFieldName, String filePathFieldName, String folderPathFieldName, String operationTypeFieldName, List<String> folderOperations) {
        this.folderPathTransformer = new FileToFolderPathTransformer(inputPathFieldName, folderPathFieldName);
        this.inputPathFieldName = inputPathFieldName;
        this.filePathFieldName = filePathFieldName;
        this.folderPathFieldName = folderPathFieldName;
        this.operationTypeFieldName = operationTypeFieldName;
        this.folderOperations = folderOperations;
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {

        documents.forEach((AbstractInputDocument document) -> {

                    String filePathValue = (String) ReflectionUtils.getFieldValue(document, inputPathFieldName);

                    if (isNotBlank(filePathValue)) {
                        if (isFolderOperation(document)) {
                            try {
                                ReflectionUtils.setFieldValue(document, folderPathFieldName, filePathValue);
                                ReflectionUtils.setFieldValue(document, filePathFieldName, null);
                            } catch (IllegalAccessException e) {
                                logger.error("error setting one of {} {} field values", filePathFieldName, folderPathFieldName, e);
                            }
                        } else {
                            this.folderPathTransformer.transform(Arrays.asList(document));
                        }
                    }
                }
        );

        return documents;
    }

    private boolean isFolderOperation(AbstractInputDocument document) {
        String operationType = (String) ReflectionUtils.getFieldValue(document, operationTypeFieldName);
        return CollectionUtils.contains(folderOperations.iterator(), operationType);
    }
}
