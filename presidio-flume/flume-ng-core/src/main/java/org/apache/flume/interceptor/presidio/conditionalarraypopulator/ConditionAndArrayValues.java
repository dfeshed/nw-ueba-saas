package org.apache.flume.interceptor.presidio.conditionalarraypopulator;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

/**
 * A configuration holding a regular expression {@link #pattern} and a list of potential array values. If the value
 * associated with {@link ConditionalArrayPopulator#sourceKey} in a given {@link JSONObject} matches the regular
 * expression, all the values in the list should be added to the {@link JSONObject}'s array associated with
 * {@link ConditionalArrayPopulator#destinationKey}.
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
public class ConditionAndArrayValues {
    private Pattern pattern;
    private List<String> values;

    /**
     * C'tor.
     *
     * @param pattern The regular expression that should be compiled to a {@link Pattern}.
     * @param values  The list of potential array values.
     */
    @JsonCreator
    public ConditionAndArrayValues(
            @JsonProperty("pattern") String pattern,
            @JsonProperty("values") List<String> values) {

        this.pattern = Pattern.compile(Validate.notEmpty(pattern, "pattern cannot be empty or null."));
        this.values = Validate.notEmpty(values, "values cannot be empty or null.");
    }

    public Pattern getPattern() {
        return pattern;
    }

    public List<String> getValues() {
        return values;
    }
}
