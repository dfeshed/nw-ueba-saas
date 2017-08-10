package presidio.input.core.services.transformation;

import fortscale.utils.replacement.PatternReplacement;
import fortscale.utils.replacement.PatternReplacementConf;
import presidio.sdk.api.domain.AbstractPresidioDocument;
import presidio.sdk.api.utils.ReflectionUtils;

import java.util.List;

public class PatternReplacementTransformer implements Transformer {

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

        PatternReplacementConf patternReplacementConf = new PatternReplacementConf(pattern, replacement);
        patternReplacementConf.setPostReplacementCondition(postReplacementCondition);
        patternReplacementConf.setPreReplacementCondition(preReplacementCondition);

        this.patternReplacement = new PatternReplacement(patternReplacementConf);
    }

    @Override
    public List<AbstractPresidioDocument> transform(List<AbstractPresidioDocument> documents) {
        documents.forEach(document -> {
            Object fieldValue = ReflectionUtils.getFieldValue(document, inputFieldName);
            String replacedPattern = this.patternReplacement.replacePattern((String) fieldValue);
            try {
                ReflectionUtils.setFieldValue(document, outputFieldName, replacedPattern);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return documents;
    }
}
