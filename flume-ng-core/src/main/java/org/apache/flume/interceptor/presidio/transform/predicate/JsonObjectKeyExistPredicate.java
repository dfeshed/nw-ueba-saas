package org.apache.flume.interceptor.presidio.transform.predicate;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import static org.apache.commons.lang3.Validate.notBlank;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class JsonObjectKeyExistPredicate extends AbstractJsonObjectPredicate {

    public static final String TYPE = "key_exist";

    private String key;



    @JsonCreator
    public JsonObjectKeyExistPredicate(@JsonProperty("name")String name, @JsonProperty("key")String key){
        super(name);
        this.key = notBlank(key, "key cannot be blank, empty or null.");
    }

    @Override
    public boolean test(JSONObject jsonObject) {
        return jsonObject.has(key);
    }
}
