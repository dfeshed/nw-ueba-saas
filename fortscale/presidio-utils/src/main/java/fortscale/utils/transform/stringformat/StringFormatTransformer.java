package fortscale.utils.transform.stringformat;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.utils.json.JsonPointerValueExtractor;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import fortscale.utils.transform.SetterTransformer;
import org.json.JSONObject;

import static org.apache.commons.lang3.Validate.*;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class StringFormatTransformer extends AbstractJsonObjectTransformer {
    public static final String TYPE = "string_format";

    private final JsonPointerValueExtractor sourceStringGetter;
    private final SetterTransformer targetStringSetter;
    private final StringFormat sourceStringFormat;
    private final StringFormat targetStringFormat;

    @JsonCreator
    public StringFormatTransformer(
            @JsonProperty("name") String name,
            @JsonProperty("sourceKey") String sourceKey,
            @JsonProperty("targetKey") String targetKey,
            @JsonProperty("sourceStringFormat") StringFormat sourceStringFormat,
            @JsonProperty("targetStringFormat") StringFormat targetStringFormat) {

        super(name);
        this.sourceStringGetter = new JsonPointerValueExtractor(notBlank(sourceKey, "sourceKey cannot be blank."));
        this.targetStringSetter = SetterTransformer.forKey(notBlank(targetKey, "targetKey cannot be blank."));
        this.sourceStringFormat = notNull(sourceStringFormat, "sourceStringFormat cannot be null.");
        this.targetStringFormat = notNull(targetStringFormat, "targetStringFormat cannot be null.");
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        notNull(jsonObject, "jsonObject cannot be null.");
        Object sourceValue = sourceStringGetter.getValue(jsonObject);
        if (sourceValue == null || sourceValue.equals(JSONObject.NULL)) return jsonObject;
        isInstanceOf(String.class, sourceValue, "sourceValue is not an instance of String.");
        String targetValue = sourceStringFormat.convert(targetStringFormat, (String)sourceValue);
        targetStringSetter.setValue(targetValue);
        return targetStringSetter.transform(jsonObject);
    }
}
