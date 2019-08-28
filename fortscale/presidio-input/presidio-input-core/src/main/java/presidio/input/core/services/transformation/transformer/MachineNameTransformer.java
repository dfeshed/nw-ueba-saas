package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.utils.logging.Logger;
import fortscale.utils.reflection.PresidioReflectionUtils;
import fortscale.utils.replacement.PatternReplacement;
import fortscale.utils.replacement.PatternReplacementConf;
import org.apache.commons.lang.StringUtils;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("machine-name-transformer")
public class MachineNameTransformer implements InputDocumentTransformer {

    private static final Logger logger = Logger.getLogger(MachineNameTransformer.class);

    private final String outputFieldName;
    private final String inputFieldName;
    private final PatternReplacement patternReplacement;

    public final static Pattern ipPattern = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    @JsonCreator
    public MachineNameTransformer(@JsonProperty("inputFieldName") String inputFieldName,
                                  @JsonProperty("outputFieldName") String outputFieldName,
                                  @JsonProperty("pattern") String pattern,
                                  @JsonProperty("replacement") String replacement,
                                  @JsonProperty("preReplacementCondition") String preReplacementCondition,
                                  @JsonProperty("postReplacementCondition") String postReplacementCondition) {
        this.inputFieldName = inputFieldName;
        this.outputFieldName = outputFieldName;
        PatternReplacementConf patternReplacementConf = new PatternReplacementConf(pattern, replacement, preReplacementCondition, postReplacementCondition);
        this.patternReplacement = new PatternReplacement(patternReplacementConf);
    }

    @Override
    public AbstractInputDocument transform(AbstractInputDocument document) {
        String fieldValue1Str = (String) PresidioReflectionUtils.getFieldValue(document, inputFieldName);
        if (StringUtils.isNotEmpty(fieldValue1Str)) {

            //IP address is transformed to empty string
            String replacedPattern;
            Matcher matcher = ipPattern.matcher(fieldValue1Str);
            if (matcher.matches()) {
                replacedPattern = StringUtils.EMPTY;
            } else {
                replacedPattern = this.patternReplacement.replacePattern(fieldValue1Str);
            }
            try {
                PresidioReflectionUtils.setFieldValue(document, outputFieldName, replacedPattern);
            } catch (IllegalAccessException e) {
                logger.error("error setting the {} field value", outputFieldName, e);
            }
        }
        return document;
    }
}
