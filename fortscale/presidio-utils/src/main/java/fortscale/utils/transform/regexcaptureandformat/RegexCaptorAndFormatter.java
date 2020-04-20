package fortscale.utils.transform.regexcaptureandformat;

import com.fasterxml.jackson.annotation.*;
import fortscale.utils.json.IJsonValueExtractor;
import fortscale.utils.json.JsonPointerValueExtractor;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import org.json.JSONObject;

import java.util.List;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notEmpty;

/**
 * Takes from a given {@link JSONObject} the string associated with {@link #sourceKey} and looks for the first
 * {@link CaptureAndFormatConfiguration} x, such that the string matches x's pattern (the configurations are traversed
 * in order). If a match is found, the {@link RegexCaptorAndFormatter} creates a formatted string using x's format
 * and the arguments configured by x's {@link CaptureAndFormatConfiguration#capturingGroupConfigurations}. Then it puts
 * the key-value pair {@link #destinationKey}-{newly created formatted string} in the given {@link JSONObject}. If a
 * match isn't found, the {@link #destinationKey} is associated with null.
 *
 * @author Lior Govrin.
 */
@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonTypeName("regex_captor_and_formatter")
public class RegexCaptorAndFormatter extends AbstractJsonObjectTransformer {

    private String sourceKey;
    private String destinationKey;
    private List<CaptureAndFormatConfiguration> captureAndFormatConfigurations;

    @JsonIgnore
    private IJsonValueExtractor jsonValueExtractor;

    @JsonCreator
    public RegexCaptorAndFormatter(
            @JsonProperty("name") String name,
            @JsonProperty("sourceKey") String sourceKey,
            @JsonProperty("destinationKey") String destinationKey,
            @JsonProperty("captureAndFormatConfigurations") List<CaptureAndFormatConfiguration> captureAndFormatConfigurations) {

        super(name);
        this.sourceKey = notBlank(sourceKey, "sourceKey cannot be blank, empty or null.");
        this.destinationKey = notBlank(destinationKey, "destinationKey cannot be blank, empty or null.");
        this.captureAndFormatConfigurations = notEmpty(captureAndFormatConfigurations, "captureAndFormatConfigurations cannot be empty or null.");
        this.jsonValueExtractor = new JsonPointerValueExtractor(sourceKey);
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        Object sourceObj = jsonValueExtractor.getValue(jsonObject);
        if (sourceObj == null || JSONObject.NULL.equals(sourceObj)) return jsonObject;
        String sourceValue = sourceObj.toString();
        Object destinationValue = JSONObject.NULL;

        for (CaptureAndFormatConfiguration captureAndFormatConfiguration : captureAndFormatConfigurations) {
            String tmp = CaptureAndFormatUtil.captureAndFormat(captureAndFormatConfiguration, sourceValue);
            if (tmp != null) {
                destinationValue = tmp;
                break;
            }
        }

        jsonObject.put(destinationKey, destinationValue);
        return jsonObject;
    }


}
