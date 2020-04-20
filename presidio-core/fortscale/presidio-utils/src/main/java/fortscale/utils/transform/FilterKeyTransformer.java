package fortscale.utils.transform;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("filter_field")
public class FilterKeyTransformer extends AbstractJsonObjectTransformer{

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
