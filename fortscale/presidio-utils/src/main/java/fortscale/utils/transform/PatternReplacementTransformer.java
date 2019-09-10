package fortscale.utils.transform;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.utils.logging.Logger;
import fortscale.utils.replacement.PatternReplacement;
import fortscale.utils.replacement.PatternReplacementConf;
import org.json.JSONObject;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("pattern-replacement-transformer")
public class PatternReplacementTransformer extends AbstractJsonObjectTransformer {

    private static final Logger logger = Logger.getLogger(PatternReplacementTransformer.class);

    private final String outputFieldName;
    private final String inputFieldName;
    private final PatternReplacement patternReplacement;

    @JsonCreator
    public PatternReplacementTransformer(@JsonProperty("name") String name,
                                         @JsonProperty("inputFieldName") String inputFieldName,
                                         @JsonProperty("outputFieldName") String outputFieldName,
                                         @JsonProperty("pattern") String pattern,
                                         @JsonProperty("replacement") String replacement,
                                         @JsonProperty("preReplacementCondition") String preReplacementCondition,
                                         @JsonProperty("postReplacementCondition") String postReplacementCondition) {
        super(name);
        this.inputFieldName = inputFieldName;
        this.outputFieldName = outputFieldName;
        PatternReplacementConf patternReplacementConf = new PatternReplacementConf(pattern, replacement, preReplacementCondition, postReplacementCondition);
        this.patternReplacement = new PatternReplacement(patternReplacementConf);
    }

    @Override
    public JSONObject transform(JSONObject document) {
        try {
            Object fieldValue = document.get(inputFieldName);
            if (fieldValue != JSONObject.NULL) {
                String replacedPattern = this.patternReplacement.replacePattern((String) fieldValue);
                document.put(outputFieldName, replacedPattern);
            }
        } catch (Exception e) {
            logger.error("error setting the {} field value", outputFieldName, e);
        }
        return document;
    }
}
