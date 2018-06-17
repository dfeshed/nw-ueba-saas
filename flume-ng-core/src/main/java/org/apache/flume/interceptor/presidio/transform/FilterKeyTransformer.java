package org.apache.flume.interceptor.presidio.transform;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class FilterKeyTransformer extends AbstractJsonObjectTransformer{

    public static final String TYPE = "filter_field";


    private String keyToFilter;

    public FilterKeyTransformer(@JsonProperty("name") String name, @JsonProperty("keyToFilter") String keyToFilter) {
        super(name);
        this.keyToFilter = Validate.notBlank(keyToFilter, "keyToFilter should not be blank");
    }


    @Override
    public JSONObject transform(JSONObject jsonObject) {
        jsonObject.remove(keyToFilter);
        return jsonObject;
    }
}
