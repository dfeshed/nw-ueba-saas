package presidio.input.core.services.transformation;

import fortscale.utils.logging.Logger;
import fortscale.utils.replacement.PatternReplacement;
import fortscale.utils.replacement.PatternReplacementConf;
import org.apache.commons.lang.StringUtils;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.utils.ReflectionUtils;

import java.util.List;

public class MachineNameTransformer implements Transformer {

    private static final Logger logger = Logger.getLogger(MachineNameTransformer.class);

    private final String outputFieldName;
    private final String inputFieldName;
    private final PatternReplacement patternReplacement;

    public static final String IP_ADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";


    public MachineNameTransformer(String inputFieldName, String outputFieldName,
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
            String fieldValue1Str = (String) fieldValue;

            //IP address is transformed to empty string
            String replacedPattern;
            if(fieldValue1Str.matches(IP_ADDRESS_PATTERN)) {
                replacedPattern = StringUtils.EMPTY;
            }
            else {
                replacedPattern = this.patternReplacement.replacePattern(fieldValue1Str);
            }

            try {
                ReflectionUtils.setFieldValue(document, outputFieldName, replacedPattern);
            } catch (IllegalAccessException e) {
                logger.error("error setting the {} field value", outputFieldName, e);
            }
        });
        return documents;
    }
}
