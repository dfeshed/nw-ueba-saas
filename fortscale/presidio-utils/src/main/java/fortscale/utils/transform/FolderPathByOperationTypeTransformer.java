package fortscale.utils.transform;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.utils.logging.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("folder-path-by-operation-type-transformer")
public class FolderPathByOperationTypeTransformer extends AbstractJsonObjectTransformer {

    private static final Logger logger = Logger.getLogger(FolderPathByOperationTypeTransformer.class);
    private final FileToFolderPathTransformer folderPathTransformer;
    private final String filePathFieldName;
    private final String inputPathFieldName;
    private final String operationTypeFieldName;
    private final String folderPathFieldName;

    @Value("#{'${folder.operation.types}'.split(',')}")
    private List<String> folderOperations;

    @JsonCreator
    public FolderPathByOperationTypeTransformer(@JsonProperty("name") String name,
                                                @JsonProperty("inputPathFieldName") String inputPathFieldName,
                                                @JsonProperty("filePathFieldName") String filePathFieldName,
                                                @JsonProperty("folderPathFieldName") String folderPathFieldName,
                                                @JsonProperty("operationTypeFieldName") String operationTypeFieldName) {
        super(name);
        this.folderPathTransformer = new FileToFolderPathTransformer(name, inputPathFieldName, folderPathFieldName);
        this.inputPathFieldName = inputPathFieldName;
        this.filePathFieldName = filePathFieldName;
        this.folderPathFieldName = folderPathFieldName;
        this.operationTypeFieldName = operationTypeFieldName;
    }

    @Override
    /*
     * If the events operation type is in the folderOperations list the received path is a folder path.
     * If the events operation type is not in the list - extract the folder path from the received path.
     */
    public JSONObject transform(JSONObject document) {
        try {
            Object filePathValueObj = document.get(inputPathFieldName);
            if (filePathValueObj != JSONObject.NULL) {
                String filePathValue = (String) filePathValueObj;
                if (isNotBlank(filePathValue)) {
                    if (isFolderOperation(document)) {
                        document.put(folderPathFieldName, filePathValue);
                        document.put(filePathFieldName, JSONObject.NULL);
                    } else {
                        this.folderPathTransformer.transform(document);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("error setting one of {} {} field values", filePathFieldName, folderPathFieldName, e);
        }
        return document;
    }

    private boolean isFolderOperation(JSONObject document) {
        String operationType = (String) document.get(operationTypeFieldName);
        return CollectionUtils.contains(folderOperations.iterator(), operationType);
    }
}
