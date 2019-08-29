package presidio.input.core.services.transformation.transformer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.utils.logging.Logger;
import fortscale.utils.reflection.PresidioReflectionUtils;
import presidio.sdk.api.domain.AbstractInputDocument;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("regex-transformer")
public class RegexTransformer extends AbstractInputDocumentTransformer {
    private static final Logger logger = Logger.getLogger(RegexTransformer.class);

    private final String inputFieldName;
    private final String outputFieldName;
    private final Pattern pattern;

    @JsonCreator
    public RegexTransformer(@JsonProperty("inputFieldName") String inputFieldName,
                            @JsonProperty("outputFieldName") String outputFieldName,
                            @JsonProperty("regex") String regex) {
        this.inputFieldName = inputFieldName;
        this.outputFieldName = outputFieldName;
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public AbstractInputDocument transform(AbstractInputDocument document) {
        String filePathValue = (String) PresidioReflectionUtils.getFieldValue(document, inputFieldName);
        if (isNotBlank(filePathValue)) {
            String outputFieldData = null;
            Matcher matcher = pattern.matcher(filePathValue);
            if (matcher.find()) {
                outputFieldData = matcher.group();
            }
            try {
                PresidioReflectionUtils.setFieldValue(document, outputFieldName, outputFieldData);
            } catch (IllegalAccessException e) {
                logger.error("error setting {} field value", outputFieldData, e);
            }
        }
        return document;
    }
}
