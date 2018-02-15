package presidio.input.core.services.transformation.transformer;

import fortscale.utils.logging.Logger;
import fortscale.utils.replacement.PatternReplacement;
import fortscale.utils.replacement.PatternReplacementConf;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.utils.ReflectionUtils;

import java.util.List;

public class PatternReplacementTransformer implements Transformer {

    private static final Logger logger = Logger.getLogger(PatternReplacementTransformer.class);

    private final String outputFieldName;
    private final String inputFieldName;
    private final PatternReplacement patternReplacement;

    public PatternReplacementTransformer(String inputFieldName, String outputFieldName,
                                         String pattern,
                                         String replacement,
                                         String preReplacementCondition,
                                         String postReplacementCondition) {
        this.inputFieldName = inputFieldName;
        this.outputFieldName = outputFieldName;

        PatternReplacementConf patternReplacementConf = new PatternReplacementConf(pattern, replacement, preReplacementCondition, postReplacementCondition);

        this.patternReplacement = new PatternReplacement(patternReplacementConf);
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {
        documents.forEach(document -> {
            Object fieldValue = ReflectionUtils.getFieldValue(document, inputFieldName);
            String replacedPattern = this.patternReplacement.replacePattern((String) fieldValue);
            try {
                ReflectionUtils.setFieldValue(document, outputFieldName, replacedPattern);
            } catch (IllegalAccessException e) {
                logger.error("error setting the {} field value", outputFieldName, e);
            }
        });
        return documents;
    }
}
