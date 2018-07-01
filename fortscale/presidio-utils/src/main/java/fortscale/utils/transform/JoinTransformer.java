package fortscale.utils.transform;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class JoinTransformer extends AbstractJsonObjectTransformer {

    public static final String TYPE = "join";



    private String destinationKey;
    private List<Object> values;
    private String separator;

    @JsonIgnore
    private List<JoinValue> joinValues;

    @JsonCreator
    public JoinTransformer(@JsonProperty("name") String name, @JsonProperty("destinationKey") String destinationKey,
                          @JsonProperty("values") List<Object> values, @JsonProperty("separator") String separator){
        super(name);
        this.destinationKey = Validate.notBlank(destinationKey, "destinationKey cannot be blank, empty or null.");
        this.separator = Validate.notNull(separator, "separator cannot be null.");
        Validate.notEmpty(values, "values cannot be empty or null.");
        values.forEach(Validate::notNull);
        this.values = values;
        this.joinValues = new ArrayList<>();
        for(Object value: values){
            this.joinValues.add(new JoinValue(value));
        }
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        StringBuilder builder = null;
        for(JoinValue joinValue: joinValues){
            if(builder == null){
                builder = new StringBuilder();
            } else {
                builder.append(separator);
            }
            Object val = joinValue.getValue(jsonObject);
            if(val == null){
                return jsonObject;
            }
            builder.append(joinValue.getValue(jsonObject).toString());
        }

        jsonObject.put(destinationKey, builder.toString());
        return jsonObject;
    }


    private static class JoinValue {
        private Object value;
        private JsonPointer jsonPointer;


        public JoinValue(Object value){
            this.value = value;

            if(value != null && value instanceof String && ((String)value).startsWith("${") && ((String)value).endsWith("}")) {
                String pointerPath = ((String)value).substring(2, ((String)value).length() - 1);
                jsonPointer = new JsonPointer(pointerPath);
            }
        }

        public Object getValue(JSONObject jsonObject) {
            if(jsonPointer == null){
                return value;
            } else {
                return jsonPointer.get(jsonObject);
            }
        }
    }
}
