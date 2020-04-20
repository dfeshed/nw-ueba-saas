package fortscale.utils.transform.regexcaptureandformat;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Configures a single argument referenced by a format specifier in the {@link CaptureAndFormatConfiguration#format}
 * string. This argument is an input sub-sequence captured by a group during the last match operation on the
 * {@link CaptureAndFormatConfiguration#pattern}.
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
public class CapturingGroupConfiguration {
    private int index;
    private CaseFormat caseFormat;

    /**
     * C'tor.
     *
     * @param index      The index of a capturing group in the {@link CaptureAndFormatConfiguration#pattern}.
     * @param caseFormat The case format to which the argument needs to be converted (null if no conversion is needed).
     */
    @JsonCreator
    public CapturingGroupConfiguration(
            @JsonProperty("index") int index,
            @JsonProperty("caseFormat") String caseFormat) {

        Validate.isTrue(index >= 0, "index must be greater than or equal to 0 (index = %d).", index);
        // caseFormat can be blank, empty or null (if the case format should not be changed).
        this.index = index;
        this.caseFormat = StringUtils.isBlank(caseFormat) ? null : CaseFormat.valueOf(caseFormat);
    }

    public int getIndex() {
        return index;
    }

    public CaseFormat getCaseFormat() {
        return caseFormat;
    }

    public enum CaseFormat {
        // All lower case characters.
        LOWER {
            @Override
            public String convert(String string) {
                return StringUtils.lowerCase(string);
            }
        },

        // All upper case characters.
        UPPER {
            @Override
            public String convert(String string) {
                return StringUtils.upperCase(string);
            }
        };

        public abstract String convert(String string);
    }
}
