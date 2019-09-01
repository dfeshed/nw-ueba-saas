package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import presidio.sdk.api.domain.AbstractInputDocument;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("file-to-folder-path-transformer")
public class FileToFolderPathTransformer extends AbstractInputDocumentTransformer {

    private static final String FOLDER_PATH_REGEX = ".*\\\\(?!.*\\\\)|.*/(?!.*/)";
    private final RegexTransformer regexTransformer;

    @JsonCreator
    public FileToFolderPathTransformer(@JsonProperty("inputFieldName") String inputPathFieldName,
                                       @JsonProperty("outputFieldName") String folderPathFieldName) {
        this.regexTransformer = new RegexTransformer(inputPathFieldName, folderPathFieldName, FOLDER_PATH_REGEX);
    }

    @Override
    public AbstractInputDocument transform(AbstractInputDocument document) {
        this.regexTransformer.transform(document);
        return document;
    }
}
