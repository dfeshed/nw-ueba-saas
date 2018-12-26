package fortscale.utils.transform.stringformat;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.utils.json.JsonPointerValueExtractor;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import fortscale.utils.transform.SetterTransformer;
import org.json.JSONArray;
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

    private final JsonPointerValueExtractor sourceValueGetter;
    private final SetterTransformer targetValueSetter;
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
        this.sourceValueGetter = new JsonPointerValueExtractor(notBlank(sourceKey, "sourceKey cannot be blank."));
        this.targetValueSetter = SetterTransformer.forKey(notBlank(targetKey, "targetKey cannot be blank."));
        this.sourceStringFormat = notNull(sourceStringFormat, "sourceStringFormat cannot be null.");
        this.targetStringFormat = notNull(targetStringFormat, "targetStringFormat cannot be null.");
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        notNull(jsonObject, "jsonObject cannot be null.");
        Object sourceValue = sourceValueGetter.getValue(jsonObject);
        Object targetValue;

        if (sourceValue == null || sourceValue.equals(JSONObject.NULL)) {
            targetValue = JSONObject.NULL;
        } else if (sourceValue instanceof String) {
            targetValue = convert((String)sourceValue);
        } else if (sourceValue instanceof Iterable<?>) {
            targetValue = convert((Iterable<?>)sourceValue);
        } else if (sourceValue instanceof JSONArray) {
            targetValue = convert((JSONArray)sourceValue);
        } else {
            String sourceValueClassName = sourceValue.getClass().getName();
            String s = String.format("sourceValue is an instance of unsupported class %s.", sourceValueClassName);
            throw new IllegalArgumentException(s);
        }

        targetValueSetter.setValue(targetValue);
        return targetValueSetter.transform(jsonObject);
    }

    // Convert a non-null String.
    private String convert(String string) {
        return sourceStringFormat.convert(targetStringFormat, string);
    }

    // Convert each nullable Object of an Iterable, expected to be a String if not null.
    private JSONArray convert(Iterable<?> iterable) {
        JSONArray convertedObjects = new JSONArray();
        for (Object object : iterable)
            convertedObjects.put(convert(object));
        return convertedObjects;
    }

    // Convert each nullable Object of a JSONArray, expected to be a String if not null.
    private JSONArray convert(JSONArray jsonArray) {
        JSONArray convertedObjects = new JSONArray();
        for (int i = 0; i < jsonArray.length(); ++i)
            convertedObjects.put(convert(jsonArray.get(i)));
        return convertedObjects;
    }

    // Convert a nullable Object, expected to be a String if not null.
    private Object convert(Object object) {
        if (object == null || object.equals(JSONObject.NULL)) {
            return JSONObject.NULL;
        } else {
            isInstanceOf(String.class, object, "object must be an instance of String.");
            return convert((String)object);
        }
    }
}
