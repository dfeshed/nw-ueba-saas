package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.utils.logging.Logger;
import fortscale.utils.reflection.PresidioReflectionUtils;
import fortscale.utils.replacement.PatternReplacement;
import fortscale.utils.replacement.PatternReplacementConf;
import presidio.sdk.api.domain.AbstractInputDocument;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("pattern-replacement-transformer")
public class PatternReplacementTransformer extends AbstractInputDocumentTransformer {

    private static final Logger logger = Logger.getLogger(PatternReplacementTransformer.class);

    private final String outputFieldName;
    private final String inputFieldName;
    private final PatternReplacement patternReplacement;

    @JsonCreator
    public PatternReplacementTransformer(@JsonProperty("inputFieldName") String inputFieldName,
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
        Object fieldValue = PresidioReflectionUtils.getFieldValue(document, inputFieldName);
        String replacedPattern = this.patternReplacement.replacePattern((String) fieldValue);
        try {
            PresidioReflectionUtils.setFieldValue(document, outputFieldName, replacedPattern);
        } catch (IllegalAccessException e) {
            logger.error("error setting the {} field value", outputFieldName, e);
        }
        return document;
    }
}
