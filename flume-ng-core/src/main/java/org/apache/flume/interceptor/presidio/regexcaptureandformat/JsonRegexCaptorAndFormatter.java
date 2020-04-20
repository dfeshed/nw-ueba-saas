package org.apache.flume.interceptor.presidio.regexcaptureandformat;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import fortscale.utils.transform.regexcaptureandformat.CaptureAndFormatConfiguration;
import fortscale.utils.transform.regexcaptureandformat.CapturingGroupConfiguration;

import java.util.List;
import java.util.regex.Matcher;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Takes from a given {@link JsonObject} the string associated with {@link #sourceKey} and looks for the first
 * {@link CaptureAndFormatConfiguration} x, such that the string matches x's pattern (the configurations are traversed
 * in order). If a match is found, the {@link JsonRegexCaptorAndFormatter} creates a formatted string using x's format
 * and the arguments configured by x's {@link CaptureAndFormatConfiguration#capturingGroupConfigurations}. Then it puts
 * the key-value pair {@link #destinationKey}-{newly created formatted string} in the given {@link JsonObject}. If a
 * match isn't found, the {@link #destinationKey} is associated with null.
 *
 * @author Lior Govrin.
 */
@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class JsonRegexCaptorAndFormatter {
    private String sourceKey;
    private String destinationKey;
    private List<CaptureAndFormatConfiguration> captureAndFormatConfigurations;

    @JsonCreator
    public JsonRegexCaptorAndFormatter(
            @JsonProperty("sourceKey") String sourceKey,
            @JsonProperty("destinationKey") String destinationKey,
            @JsonProperty("captureAndFormatConfigurations") List<CaptureAndFormatConfiguration> captureAndFormatConfigurations) {

        notBlank(sourceKey, "sourceKey cannot be blank, empty or null.");
        notBlank(destinationKey, "destinationKey cannot be blank, empty or null.");
        notEmpty(captureAndFormatConfigurations, "captureAndFormatConfigurations cannot be empty or null.");

        this.sourceKey = sourceKey;
        this.destinationKey = destinationKey;
        this.captureAndFormatConfigurations = captureAndFormatConfigurations;
    }

    public JsonObject captureAndFormat(JsonObject jsonObject) {
        // destinationKey should be consistent with sourceKey:
        // If sourceKey is not present, destinationKey should not be present.
        // If sourceKey is null, destinationKey should be null.
        if (!jsonObject.has(sourceKey)) {
            return jsonObject;
        } else if (jsonObject.get(sourceKey).isJsonNull()) {
            jsonObject.add(destinationKey, JsonNull.INSTANCE);
            return jsonObject;
        }

        String sourceValue = jsonObject.get(sourceKey).getAsString();
        String destinationValue = null;

        for (CaptureAndFormatConfiguration captureAndFormatConfiguration : captureAndFormatConfigurations) {
            Matcher matcher = captureAndFormatConfiguration.getPattern().matcher(sourceValue);

            if (matcher.matches()) {
                Object[] args = getArgs(captureAndFormatConfiguration.getCapturingGroupConfigurations(), matcher);
                destinationValue = String.format(captureAndFormatConfiguration.getFormat(), args);
                break;
            }
        }

        jsonObject.addProperty(destinationKey, destinationValue);
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

