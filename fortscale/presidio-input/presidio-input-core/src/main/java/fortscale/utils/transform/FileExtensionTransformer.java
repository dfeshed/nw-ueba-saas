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
@JsonTypeName("file-extension-transformer")
public class FileExtensionTransformer extends AbstractJsonObjectTransformer {
    private final RegexTransformer regexTransformer;
    private static final String EXTENSION_REGEX = "\\.[0-9a-z]+$";

    @JsonCreator
    public FileExtensionTransformer(@JsonProperty("name") String name,
                                    @JsonProperty("inputFieldName") String inputFieldName,
                                    @JsonProperty("outputFieldName") String outputFieldName) {
        super(name);
        this.regexTransformer = new RegexTransformer(name, inputFieldName, outputFieldName, EXTENSION_REGEX);
    }

    @Override
    public JSONObject transform(JSONObject document) {
        this.regexTransformer.transform(document);
        return document;
    }
}
