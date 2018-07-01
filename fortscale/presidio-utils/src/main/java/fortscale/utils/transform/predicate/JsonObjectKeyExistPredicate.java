package fortscale.utils.transform.predicate;


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
    private boolean testNull;



    @JsonCreator
    public JsonObjectKeyExistPredicate(@JsonProperty("name")String name,
                                       @JsonProperty("key")String key,
                                       @JsonProperty("testNull") boolean testNull){
        super(name);
        this.key = notBlank(key, "key cannot be blank, empty or null.");
        this.testNull = testNull;
    }

    public JsonObjectKeyExistPredicate(String name, String key){
        this(name, key, false);
    }

    @Override
    public boolean test(JSONObject jsonObject) {
        boolean ret = jsonObject.has(key);
        if(ret && testNull){
            if(jsonObject.get(key) == JSONObject.NULL || jsonObject.get(key) == null){
                ret = false;
            }
        }

        return ret;
    }
}
