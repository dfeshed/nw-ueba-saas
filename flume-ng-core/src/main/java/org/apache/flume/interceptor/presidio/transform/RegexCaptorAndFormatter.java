package org.apache.flume.interceptor.presidio.transform;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flume.interceptor.presidio.regexcaptureandformat.CaptureAndFormatConfiguration;
import org.apache.flume.interceptor.presidio.regexcaptureandformat.CapturingGroupConfiguration;
import org.apache.flume.interceptor.presidio.regexcaptureandformat.JsonRegexCaptorAndFormatter;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;


/**
 * Takes from a given {@link JSONObject} the string associated with {@link #sourceKey} and looks for the first
 * {@link CaptureAndFormatConfiguration} x, such that the string matches x's pattern (the configurations are traversed
 * in order). If a match is found, the {@link JsonRegexCaptorAndFormatter} creates a formatted string using x's format
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
public class RegexCaptorAndFormatter extends AbstractJsonObjectTransformer {

    public static final String TYPE = "regex_captor_and_formatter";

    private String sourceKey;
    private String destinationKey;
    private List<CaptureAndFormatConfiguration> captureAndFormatConfigurations;

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
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        Object sourceObj = jsonObject.opt(sourceKey);
        if (sourceObj == null || JSONObject.NULL.equals(sourceObj)) return jsonObject;
        String sourceValue = jsonObject.getString(sourceKey);
        Object destinationValue = JSONObject.NULL;

        for (CaptureAndFormatConfiguration captureAndFormatConfiguration : captureAndFormatConfigurations) {
            Matcher matcher = captureAndFormatConfiguration.getPattern().matcher(sourceValue);

            if (matcher.matches()) {
                Object[] args = getArgs(captureAndFormatConfiguration.getCapturingGroupConfigurations(), matcher);
                destinationValue = String.format(captureAndFormatConfiguration.getFormat(), args);
                break;
            }
        }

        jsonObject.put(destinationKey, destinationValue);
        return jsonObject;
    }

    private static Object[] getArgs(List<CapturingGroupConfiguration> capturingGroupConfigurations, Matcher matcher) {
        return isEmpty(capturingGroupConfigurations) ? null : capturingGroupConfigurations.stream()
                .map(capturingGroupConfiguration -> {
                    String group = matcher.group(capturingGroupConfiguration.getIndex());
                    CapturingGroupConfiguration.CaseFormat caseFormat = capturingGroupConfiguration.getCaseFormat();
                    return caseFormat == null ? group : caseFormat.convert(group);
                })
                .toArray(Object[]::new);
    }
}
