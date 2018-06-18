package org.apache.flume.interceptor.presidio.transform;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;

import java.util.regex.Pattern;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class FindAndReplaceTransformer extends AbstractJsonObjectTransformer{

    public static final String TYPE = "find_and_replace";


    private String key;
    private String regex;
    private String replacement;
    @JsonIgnore
    private Pattern pattern;



    @JsonCreator
    public FindAndReplaceTransformer(@JsonProperty("name") String name,
                                     @JsonProperty("key") String key,
                                     @JsonProperty("regex") String regex,
                                     @JsonProperty("replacement") String replacement) {
        super(name);
        this.key = Validate.notBlank(key, "key cannot be blank, empty or null.");
        this.regex = Validate.notBlank(regex, "regex cannot be blank, empty or null.");
        this.replacement = Validate.notNull(replacement, "replacement cannot be null.");
        pattern = Pattern.compile(regex);
    }


    @Override
    public JSONObject transform(JSONObject jsonObject) {
        if (!jsonObject.has(key)) return jsonObject;

        Object value = jsonObject.get(key);
        if(value == null || !(value instanceof String)) return jsonObject;

        String newValue = pattern.matcher((String) value).replaceAll(replacement);
        jsonObject.put(key, newValue);

        return jsonObject;
    }
}
