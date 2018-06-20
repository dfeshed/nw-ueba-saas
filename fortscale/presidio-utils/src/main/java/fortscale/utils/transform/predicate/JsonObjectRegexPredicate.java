package fortscale.utils.transform.predicate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.Validate.notBlank;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class JsonObjectRegexPredicate extends AbstractJsonObjectPredicate {

    public static final String TYPE = "regex";

    private String sourceKey;
    private String regex;
    @JsonIgnore
    private Pattern pattern;

    @JsonCreator
    public JsonObjectRegexPredicate(@JsonProperty("name")String name, @JsonProperty("sourceKey")String sourceKey, @JsonProperty("regex")String regex){
        super(name);
        this.sourceKey = notBlank(sourceKey, "sourceKey cannot be blank, empty or null.");
        this.regex = notBlank(regex, "regex cannot be blank, empty or null.");
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public boolean test(JSONObject jsonObject) {
        boolean ret = false;
        if (jsonObject.has(sourceKey)) {
            Object value = jsonObject.get(sourceKey);
            if (value instanceof String) {
                ret = pattern.matcher((String)value).matches();
            }
        }
        return ret;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public String getRegex() {
        return regex;
    }
}
