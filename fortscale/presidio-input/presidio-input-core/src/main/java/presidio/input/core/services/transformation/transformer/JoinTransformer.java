package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.utils.logging.Logger;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("join-transformer")
public class JoinTransformer extends AbstractJsonObjectTransformer {

    private static final Logger logger = Logger.getLogger(JoinTransformer.class);

    private List<String> inputFieldNames;
    private String outputFieldName;
    private String delimiter;

    @JsonCreator
    public JoinTransformer(@JsonProperty("name") String name,
                           @JsonProperty("inputFieldNames") List<String> inputFieldNames,
                           @JsonProperty("outputFieldName") String outputFieldName,
                           @JsonProperty("delimiter") String delimiter) {
        super(name);
        this.inputFieldNames = inputFieldNames;
        this.outputFieldName = outputFieldName;
        this.delimiter = delimiter;
    }

    @Override
    public JSONObject transform(JSONObject document) {
        try {
            Map<String, Object> requiredFieldNameToValueMap = getInputFields(document);
            if (requiredFieldNameToValueMap.size() == inputFieldNames.size()) {
                String outputFieldValue = inputFieldNames.stream().map(fieldName -> requiredFieldNameToValueMap.get(fieldName).toString()).collect(Collectors.joining(delimiter));
                document.put(outputFieldName, outputFieldValue);
            }
        } catch (Exception e) {
            logger.error("error setting the {} field value", outputFieldName, e);
        }
        return document;
    }

    private Map<String, Object> getInputFields(JSONObject document) {
        Map<String, Object> requiredFieldNameToValueMap = new HashMap<>();
        for (String requiredFieldName : inputFieldNames) {
            requiredFieldNameToValueMap.put(requiredFieldName, document.get(requiredFieldName));
        }
        return requiredFieldNameToValueMap;
    }
}
