package org.apache.flume.interceptor.presidio.regexcaptureandformat;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Configures a regular expression and a format string whose arguments are input sub-sequences captured by groups during
 * the last match operation on the regular expression. The {@link #capturingGroupConfigurations} configure (in order)
 * the arguments referenced by the format specifiers in the format string.
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
public class CaptureAndFormatConfiguration {
    private Pattern pattern;
    private String format;
    private List<CapturingGroupConfiguration> capturingGroupConfigurations;

    /**
     * C'tor.
     *
     * @param pattern                      The regular expression.
     * @param format                       The format string.
     * @param capturingGroupConfigurations The configurations of the {@link #format}'s arguments.
     */
    @JsonCreator
    public CaptureAndFormatConfiguration(
            @JsonProperty("pattern") String pattern,
            @JsonProperty("format") String format,
            @JsonProperty("capturingGroupConfigurations") List<CapturingGroupConfiguration> capturingGroupConfigurations) {

        this.pattern = Pattern.compile(Validate.notEmpty(pattern, "pattern cannot be empty or null."));
        this.format = Validate.notNull(format, "format cannot be null.");
        // Can be empty or null if there are no capturing groups.
        this.capturingGroupConfigurations = capturingGroupConfigurations;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getFormat() {
        return format;
    }

    public List<CapturingGroupConfiguration> getCapturingGroupConfigurations() {
        return capturingGroupConfigurations;
    }
}
