package fortscale.utils.transform;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.json.JSONObject;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("file-to-folder-path-transformer")
public class FileToFolderPathTransformer extends AbstractJsonObjectTransformer {

    private static final String FOLDER_PATH_REGEX = ".*\\\\(?!.*\\\\)|.*/(?!.*/)";
    private final RegexTransformer regexTransformer;

    @JsonCreator
    public FileToFolderPathTransformer(@JsonProperty("name") String name,
                                       @JsonProperty("inputFieldName") String inputPathFieldName,
                                       @JsonProperty("outputFieldName") String folderPathFieldName) {
        super(name);
        this.regexTransformer = new RegexTransformer(name, inputPathFieldName, folderPathFieldName, FOLDER_PATH_REGEX);
    }

    @Override
    public JSONObject transform(JSONObject document) {
        this.regexTransformer.transform(document);
        return document;
    }
}
