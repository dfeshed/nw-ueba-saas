package fortscale.utils.transform;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.utils.json.IJsonValueExtractor;
import fortscale.utils.json.JsonValueExtractorFactory;
import fortscale.utils.json.JsonValueExtractorJoiner;
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
    private List<IJsonValueExtractor> jsonValueExtractors;

    @JsonCreator
    public JoinTransformer(@JsonProperty("name") String name, @JsonProperty("destinationKey") String destinationKey,
                          @JsonProperty("values") List<Object> values, @JsonProperty("separator") String separator){
        super(name);
        this.destinationKey = Validate.notBlank(destinationKey, "destinationKey cannot be blank, empty or null.");
        this.separator = Validate.notNull(separator, "separator cannot be null.");
        Validate.notEmpty(values, "values cannot be empty or null.");
        values.forEach(Validate::notNull);
        this.values = values;
        this.jsonValueExtractors = new ArrayList<>();
        JsonValueExtractorFactory factory = new JsonValueExtractorFactory();
        for(Object value: values){
            IJsonValueExtractor extractor = factory.create(value);
            this.jsonValueExtractors.add(extractor);
        }
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        String joinedValues = JsonValueExtractorJoiner.joining(separator, jsonObject, jsonValueExtractors);
        if(joinedValues != null) {
            jsonObject.put(destinationKey, joinedValues);
        }
        return jsonObject;
    }
}
