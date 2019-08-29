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
@JsonTypeName("file-extension-transformer")
public class FileExtensionTransformer extends AbstractInputDocumentTransformer {
    private final RegexTransformer regexTransformer;
    private static final String EXTENSION_REGEX = "\\.[0-9a-z]+$";

    @JsonCreator
    public FileExtensionTransformer(@JsonProperty("inputFieldName") String inputPathFieldName,
                                    @JsonProperty("outputFieldName") String outputPathFieldName) {
        this.regexTransformer = new RegexTransformer(inputPathFieldName, outputPathFieldName, EXTENSION_REGEX);
    }

    @Override
    public AbstractInputDocument transform(AbstractInputDocument document) {
        this.regexTransformer.transform(document);
        return document;
    }
}
