package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.utils.logging.Logger;
import fortscale.utils.reflection.PresidioReflectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("folder-path-by-operation-type-transformer")
public class FolderPathByOperationTypeTransformer implements InputDocumentTransformer {

    private static final Logger logger = Logger.getLogger(FolderPathByOperationTypeTransformer.class);
    private final FileToFolderPathTransformer folderPathTransformer;
    private final String filePathFieldName;
    private final String inputPathFieldName;
    private final String operationTypeFieldName;
    private final String folderPathFieldName;

    @Value("${folder.operation.types}")
    private List<String> folderOperations;

    @JsonCreator
    public FolderPathByOperationTypeTransformer(@JsonProperty("inputPathFieldName") String inputPathFieldName,
                                                @JsonProperty("filePathFieldName") String filePathFieldName,
                                                @JsonProperty("folderPathFieldName") String folderPathFieldName,
                                                @JsonProperty("operationTypeFieldName") String operationTypeFieldName) {
        this.folderPathTransformer = new FileToFolderPathTransformer(inputPathFieldName, folderPathFieldName);
        this.inputPathFieldName = inputPathFieldName;
        this.filePathFieldName = filePathFieldName;
        this.folderPathFieldName = folderPathFieldName;
        this.operationTypeFieldName = operationTypeFieldName;
        this.folderOperations = new LinkedList<>();
    }

    public void setFolderOperations(List<String> folderOperations) {
        this.folderOperations = folderOperations;
    }

    @Override
    /*
     * If the events operation type is in the folderOperations list the received path is a folder path.
     * If the events operation type is not in the list - extract the folder path from the received path.
     */
    public AbstractInputDocument transform(AbstractInputDocument document) {
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
                this.folderPathTransformer.transform(document);
            }
        }
        return document;
    }

    private boolean isFolderOperation(AbstractInputDocument document) {
        String operationType = (String) PresidioReflectionUtils.getFieldValue(document, operationTypeFieldName);
        return CollectionUtils.contains(folderOperations.iterator(), operationType);
    }
}
