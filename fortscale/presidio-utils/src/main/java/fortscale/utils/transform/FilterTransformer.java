package fortscale.utils.transform;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import fortscale.utils.transform.predicate.IJsonObjectPredicate;
import org.json.JSONObject;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class FilterTransformer extends AbstractJsonObjectTransformer{

    public static final String TYPE = "filter";

    private IJsonObjectPredicate predicate;
    private boolean filterIn;

    @JsonCreator
    public FilterTransformer(@JsonProperty("name") String name, @JsonProperty("predicate") IJsonObjectPredicate predicate,
                             @JsonProperty("filterIn") Boolean filterIn){
        super(name);
        this.predicate = Validate.notNull(predicate, "the predicate shouldn't be null.");
        this.filterIn = filterIn == null ? true : filterIn;
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        JSONObject ret = jsonObject;
        boolean predicateTest = predicate.test(jsonObject);
        if((predicateTest && !filterIn) || (!predicateTest && filterIn)){
            ret = null;
        }

        return ret;
    }
}
