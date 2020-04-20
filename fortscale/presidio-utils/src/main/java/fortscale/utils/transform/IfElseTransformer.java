package fortscale.utils.transform;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.utils.transform.predicate.IJsonObjectPredicate;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("if_else")
public class IfElseTransformer extends AbstractJsonObjectTransformer{

    private IJsonObjectPredicate predicate;
    private IJsonObjectTransformer ifTransformer;
    private IJsonObjectTransformer elseTransformer;

    @JsonCreator
    public IfElseTransformer(@JsonProperty("name") String name, @JsonProperty("predicate") IJsonObjectPredicate predicate,
                             @JsonProperty("ifTransformer") IJsonObjectTransformer ifTransformer,
                             @JsonProperty("elseTransformer") IJsonObjectTransformer elseTransformer){
        this(name,predicate,ifTransformer);
        this.elseTransformer = elseTransformer;
    }

    public IfElseTransformer(@JsonProperty("name") String name, @JsonProperty("predicate") IJsonObjectPredicate predicate,
                             @JsonProperty("ifTransformer") IJsonObjectTransformer ifTransformer){
        super(name);
        this.predicate = Validate.notNull(predicate, "predicate cannot be null.");
        this.ifTransformer = Validate.notNull(ifTransformer, "ifTransformer cannot be null.");
        this.elseTransformer = null;
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        JSONObject ret = jsonObject;
        if(predicate.test(jsonObject)){
            ret = ifTransformer.transform(jsonObject);
        } else{
            if(elseTransformer != null){
                ret = elseTransformer.transform(jsonObject);
            }
        }

        return ret;
    }
}
