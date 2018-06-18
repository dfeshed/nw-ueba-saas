package org.apache.flume.interceptor.presidio.transform;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.apache.flume.interceptor.presidio.transform.predicate.IJsonObjectPredicate;
import org.json.JSONObject;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class IfElseTransformer extends AbstractJsonObjectTransformer{
    public static final String TYPE = "if_else";


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
