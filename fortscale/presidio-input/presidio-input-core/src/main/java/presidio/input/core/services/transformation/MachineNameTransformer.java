package presidio.input.core.services.transformation;

import fortscale.utils.logging.Logger;
import fortscale.utils.replacement.PatternReplacement;
import fortscale.utils.replacement.PatternReplacementConf;
import org.apache.commons.lang.StringUtils;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.utils.ReflectionUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MachineNameTransformer implements Transformer {

    private static final Logger logger = Logger.getLogger(MachineNameTransformer.class);

    private final String outputFieldName;
    private final String inputFieldName;
    private final PatternReplacement patternReplacement;

    public final static Pattern ipPattern = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

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
            Matcher matcher = ipPattern.matcher(fieldValue1Str);
            if(matcher.matches()) {
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
