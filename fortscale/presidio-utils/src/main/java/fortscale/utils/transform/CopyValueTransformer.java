package fortscale.utils.transform;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.utils.json.IJsonValueExtractor;
import fortscale.utils.json.JsonPointerValueExtractor;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notEmpty;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class CopyValueTransformer extends AbstractJsonObjectTransformer {
    public static final String TYPE = "copy_value";
    private static final boolean IS_REMOVE_SOURCE_KEY_DEFAULT = false;

    private String sourceKey;
    private boolean isRemoveSourceKey;
    private List<String> destinationKeys;

    @JsonIgnore
    private IJsonValueExtractor jsonValueExtractor;
    @JsonIgnore
    private List<SetterTransformer> setterTransformers;

    @JsonCreator
    public CopyValueTransformer(
            @JsonProperty("name") String name,
            @JsonProperty("sourceKey") String sourceKey,
            @JsonProperty("isRemoveSourceKey") Boolean isRemoveSourceKey,
            @JsonProperty("destinationKeys") List<String> destinationKeys) {

        super(name);
        this.sourceKey = notBlank(sourceKey, "sourceKey cannot be blank, empty or null.");
        this.isRemoveSourceKey = isRemoveSourceKey == null ? IS_REMOVE_SOURCE_KEY_DEFAULT : isRemoveSourceKey;
        this.destinationKeys = notEmpty(destinationKeys, "destinationKeys cannot be empty or null.");
        this.jsonValueExtractor = new JsonPointerValueExtractor(sourceKey);
        this.setterTransformers = destinationKeys.stream()
                .map(destinationKey -> new SetterTransformer(
                        "destination-key-setter-transformer",
                        notBlank(destinationKey, "destinationKeys cannot contain blank, empty or null elements."),
                        null
                ))
                .collect(Collectors.toList());
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        Object sourceValue = jsonValueExtractor.getValue(jsonObject);
        if (sourceValue == null) return jsonObject;

        for (SetterTransformer setterTransformer : setterTransformers) {
            setterTransformer.setValue(sourceValue);
            setterTransformer.transform(jsonObject);
        }

        // Currently the removal of the source key is only supported for non-hierarchical keys.
        if (isRemoveSourceKey) jsonObject.remove(sourceKey);
        return jsonObject;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public boolean isRemoveSourceKey() {
        return isRemoveSourceKey;
    }

    public List<String> getDestinationKeys() {
        return destinationKeys;
    }
}
