package fortscale.utils.transform;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.utils.json.IJsonValueExtractor;
import fortscale.utils.json.JsonValueExtractorFactory;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class SwitchCaseTransformer extends AbstractJsonObjectTransformer{
    public static final String TYPE = "switch_case";
    private static final boolean IS_REMOVE_SOURCE_KEY_DEFAULT = false;


    private String sourceKey;
    private String destinationKey;
    private Object destinationDefaultValue;
    private List<SwitchCase> cases;
    @JsonIgnore
    private SetterTransformer setterTransformer;

    @JsonCreator
    public SwitchCaseTransformer(@JsonProperty("name") String name, @JsonProperty("sourceKey") String sourceKey,
                                 @JsonProperty("destinationKey") String destinationKey,
                                 @JsonProperty("destinationDefaultValue") Object destinationDefaultValue,
                                 @JsonProperty("cases") List<SwitchCase> cases){
        super(name);
        this.sourceKey = Validate.notBlank(sourceKey, "sourceKey cannot be blank, empty or null.");
        this.destinationKey = Validate.notBlank(destinationKey, "sourceKey cannot be blank, empty or null.");
        this.destinationDefaultValue = destinationDefaultValue;
        this.cases = Validate.notEmpty(cases,"cases should not be empty.");

        this.setterTransformer = new SetterTransformer("key-value-setter", destinationKey, destinationDefaultValue);
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        if (!jsonObject.has(sourceKey)) return jsonObject;
        Object sourceValue = jsonObject.get(sourceKey);
        Object destinationValue = destinationDefaultValue;
        if(sourceValue != null && sourceValue instanceof String){
            String sourceValueStr = (String) sourceValue;
            for(SwitchCase switchCase: cases){
                if(switchCase.test(sourceValueStr)){
                    destinationValue = switchCase.getCaseValue(jsonObject);
                    break;
                }
            }
        }

        setterTransformer.setValue(destinationValue);
        setterTransformer.transform(jsonObject);

        return jsonObject;
    }

    @JsonAutoDetect(
            creatorVisibility = JsonAutoDetect.Visibility.ANY,
            fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
            setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class SwitchCase{
        private String caseKey;
        // this is member of the class only for serialization and deserialization.
        private Object caseValue;
        private Boolean isRegex;
        @JsonIgnore
        private Pattern pattern;
        @JsonIgnore
        private IJsonValueExtractor jsonValueExtractor;



        public SwitchCase(String caseKey,  Object caseValue){
            this(caseKey, caseValue, false);
        }

        @JsonCreator
        public SwitchCase(@JsonProperty("caseKey") String caseKey, @JsonProperty("caseValue") Object caseValue,
                          @JsonProperty("isRegex") Boolean isRegex){
            this.caseKey = Validate.notBlank(caseKey, "caseKey cannot be blank, empty or null.");
            this.caseValue = caseValue;

            jsonValueExtractor = (new JsonValueExtractorFactory()).create(caseValue);

            this.isRegex = isRegex == null ? false : isRegex;
            if(this.isRegex){
                pattern = Pattern.compile(caseKey);
            }
        }

        public Object getCaseValue(JSONObject jsonObject) {
            return jsonValueExtractor.getValue(jsonObject);
        }

        public boolean isRegex() {
            return isRegex;
        }

        public boolean test(String val){
            if(isRegex){
                return pattern.matcher(val).matches();
            } else {
                return caseKey.equals(val);
            }
        }

        @Override
        public String toString(){
            return "caseKey: " + caseKey + " caseValue: " + caseValue + " isRegex: " + isRegex;
        }
    }


}
