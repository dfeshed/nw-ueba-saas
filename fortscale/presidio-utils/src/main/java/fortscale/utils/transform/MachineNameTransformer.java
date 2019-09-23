package fortscale.utils.transform;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.utils.logging.Logger;
import fortscale.utils.replacement.PatternReplacement;
import fortscale.utils.replacement.PatternReplacementConf;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("machine-name-transformer")
public class MachineNameTransformer extends AbstractJsonObjectTransformer {

    private static final Logger logger = Logger.getLogger(MachineNameTransformer.class);

    private final String outputFieldName;
    private final String inputFieldName;
    private final PatternReplacement patternReplacement;

    public final static Pattern ipPattern = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    @JsonCreator
    public MachineNameTransformer(@JsonProperty("name") String name,
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
            if (JSONObject.NULL != fieldValue) {
                String fieldValueStr = (String) fieldValue;
                if (StringUtils.isNotEmpty(fieldValueStr)) {

                    // IP address is transformed to empty string
                    String replacedPattern;
                    Matcher matcher = ipPattern.matcher(fieldValueStr);
                    if (matcher.matches()) {
                        replacedPattern = StringUtils.EMPTY;
                    } else {
                        replacedPattern = this.patternReplacement.replacePattern(fieldValueStr);
                    }
                    document.put(outputFieldName, replacedPattern);
                }
            }
        } catch (Exception e) {
            logger.error("error setting the {} field value", outputFieldName, e);
        }

        return document;
    }
}
