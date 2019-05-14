package fortscale.utils.transform;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.utils.json.IJsonValueExtractor;
import fortscale.utils.json.JsonPointerValueExtractor;
import fortscale.utils.transform.regexcaptureandformat.CaptureAndFormatConfiguration;
import org.json.JSONArray;
import org.json.JSONObject;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class FirstArrayValueRegexCaptorAndFormatter extends AbstractJsonObjectTransformer{

    public static final String TYPE = "first_array_value_regex_captor_and_formatter";

    private String sourceArrayKey;
    private String targetKey;
    private CaptureAndFormatConfiguration captureAndFormatConfiguration;

    @JsonIgnore
    private IJsonValueExtractor jsonValueExtractor;
    @JsonIgnore
    private SetterTransformer targetValueSetter;

    @JsonCreator
    public FirstArrayValueRegexCaptorAndFormatter(
            @JsonProperty("name") String name,
            @JsonProperty("sourceArrayKey") String sourceArrayKey,
            @JsonProperty("targetKey") String targetKey,
            @JsonProperty("captureAndFormatConfiguration") CaptureAndFormatConfiguration captureAndFormatConfiguration) {

        super(name);
        this.sourceArrayKey = notBlank(sourceArrayKey, "sourceArrayKey cannot be blank, empty or null.");
        this.targetKey = notBlank(targetKey, "targetKey cannot be blank, empty or null.");
        this.jsonValueExtractor = new JsonPointerValueExtractor(sourceArrayKey);
        this.targetValueSetter = SetterTransformer.forKey(targetKey);
        this.captureAndFormatConfiguration = notNull(captureAndFormatConfiguration);
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        Object sourceValue = jsonValueExtractor.getValue(jsonObject);
        if (sourceValue == null || !(sourceValue instanceof JSONArray)) return jsonObject;

        Object destinationValue = JSONObject.NULL;
        JSONArray jsonArray = (JSONArray)sourceValue;
        for (int i = 0; i < jsonArray.length(); i++){
            String value = captureAndFormatConfiguration.captureAndFormat(jsonArray.getString(i));
            if(value != null){
                destinationValue = value;
                break;
            }
        }
        targetValueSetter.setValue(destinationValue);
        return targetValueSetter.transform(jsonObject);
    }
}
