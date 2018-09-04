package fortscale.utils.transform;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.utils.data.Pair;
import fortscale.utils.json.IJsonValueExtractor;
import fortscale.utils.json.JsonValueExtractorFactory;
import fortscale.utils.transform.predicate.IJsonObjectPredicate;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class SwitchCasePredicatesTransformer extends AbstractJsonObjectTransformer{
    public static final String TYPE = "switch_case_predicat";


    private List<SwitchCasePredicatesTransformerPair> switchCasePairs;
    private IJsonObjectTransformer defaultTransformer;


    @JsonCreator
    public SwitchCasePredicatesTransformer(@JsonProperty("name") String name,
                                           @JsonProperty("switchCasePairs") List<SwitchCasePredicatesTransformerPair> switchCasePairs,
                                           @JsonProperty("defaultTransformer") IJsonObjectTransformer defaultTransformer){
        super(name);
        this.switchCasePairs = Validate.notEmpty(switchCasePairs, "switchCasePairs cannot be empty or null.");
        this.defaultTransformer = Validate.notNull(defaultTransformer, "defaultTransformer cannot be empty or null.");

    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        for (SwitchCasePredicatesTransformerPair pair:switchCasePairs){
            //Loop until first match, to the transform and finish
            if (pair.getPredicate().test(jsonObject)){
                pair.getTransformer().transform(jsonObject);
                return jsonObject;
            }
        }

        //Do the default only if no one triggered before
        if (defaultTransformer!=null){
            defaultTransformer.transform(jsonObject);
        }

        return jsonObject;
    }


    public static class SwitchCasePredicatesTransformerPair{
        private IJsonObjectPredicate predicate;
        private IJsonObjectTransformer transformer;

        public SwitchCasePredicatesTransformerPair() {
        }

        public SwitchCasePredicatesTransformerPair(IJsonObjectPredicate predicate, IJsonObjectTransformer transformer) {
            this.predicate = predicate;
            this.transformer = transformer;
        }

        public IJsonObjectPredicate getPredicate() {
            return predicate;
        }

        public void setPredicate(IJsonObjectPredicate predicate) {
            this.predicate = predicate;
        }

        public IJsonObjectTransformer getTransformer() {
            return transformer;
        }

        public void setTransformer(IJsonObjectTransformer transformer) {
            this.transformer = transformer;
        }
    }


}
