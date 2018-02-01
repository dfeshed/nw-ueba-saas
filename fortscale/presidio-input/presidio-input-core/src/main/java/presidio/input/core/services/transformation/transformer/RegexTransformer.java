package presidio.input.core.services.transformation.transformer;

import fortscale.utils.logging.Logger;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.utils.ReflectionUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class RegexTransformer implements Transformer {
    private static final Logger logger = Logger.getLogger(RegexTransformer.class);

    private final String inputFieldName;
    private final String outputFieldName;
    private final Pattern pattern;

    public RegexTransformer(String inputFieldName, String outputFieldName, String regex) {
        this.inputFieldName = inputFieldName;
        this.outputFieldName = outputFieldName;
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public List<AbstractInputDocument> transform(List<AbstractInputDocument> documents) {

        documents.forEach((AbstractInputDocument document) -> {

                    String filePathValue = (String) ReflectionUtils.getFieldValue(document, inputFieldName);

                    if (isNotBlank(filePathValue)) {
                        String outputFieldData = null;
                        Matcher matcher = pattern.matcher(filePathValue);
                        if (matcher.find()) {
                            outputFieldData = matcher.group();
                        }
                        try {
                            ReflectionUtils.setFieldValue(document, outputFieldName, outputFieldData);
                        } catch (IllegalAccessException e) {
                            logger.error("error setting {} field value", outputFieldData, e);
                        }
                    }
                }
        );

        return documents;
    }
}
