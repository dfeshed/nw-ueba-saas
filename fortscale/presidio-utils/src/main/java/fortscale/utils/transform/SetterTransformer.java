package fortscale.utils.transform;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.utils.json.JsonPointer;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;




@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class SetterTransformer extends AbstractJsonObjectTransformer {

    public static final String TYPE = "set_value";


    private String key;
    private Object value;

    @JsonIgnore
    private JsonPointer keyJsonPointer;
    @JsonIgnore
    private String lastKey;


    @JsonCreator
    public SetterTransformer(@JsonProperty("name") String name,
                             @JsonProperty("key") String key,
                             @JsonProperty("value") Object value){
        super(name);
        this.key = Validate.notBlank(key, "key cannot be blank, empty or null.");
        this.value = value;

        int indexOfLastDotOfKey = key.lastIndexOf('.');
        if(indexOfLastDotOfKey != -1){
            keyJsonPointer = new JsonPointer(key.substring(0, indexOfLastDotOfKey));
            lastKey = key.substring(indexOfLastDotOfKey+1);
        }
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        value = value == null ? JSONObject.NULL : value;
        if(keyJsonPointer == null) {
            jsonObject.put(key, value);
        } else {
            keyJsonPointer.set(jsonObject, lastKey, value, true);
        }

        return jsonObject;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public static SetterTransformer forKey(String key) {
        String name = String.format("%s-setter-transformer", key);
        return new SetterTransformer(name, key, null);
    }
}
