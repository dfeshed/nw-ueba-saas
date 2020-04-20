package fortscale.utils.transform;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fortscale.utils.logging.Logger;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("regex-transformer")
public class RegexTransformer extends AbstractJsonObjectTransformer {
    private static final Logger logger = Logger.getLogger(RegexTransformer.class);

    private final String inputFieldName;
    private final String outputFieldName;
    private final Pattern pattern;

    @JsonCreator
    public RegexTransformer(@JsonProperty("name") String name,
                            @JsonProperty("inputFieldName") String inputFieldName,
                            @JsonProperty("outputFieldName") String outputFieldName,
                            @JsonProperty("regex") String regex) {
        super(name);
        this.inputFieldName = inputFieldName;
        this.outputFieldName = outputFieldName;
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        String outputFieldData = null;
        try {
            Object filePathObj = jsonObject.get(inputFieldName);
            if (filePathObj != JSONObject.NULL) {
                String filePathValue = (String) filePathObj;
                if (isNotBlank(filePathValue)) {
                    Matcher matcher = pattern.matcher(filePathValue);
                    if (matcher.find()) {
                        outputFieldData = matcher.group();
                    }
                    jsonObject.put(outputFieldName, outputFieldData);
                }
            }
        } catch (Exception e) {
            logger.error("error getting/setting {} to {} with {} field value", inputFieldName, outputFieldName, outputFieldData, e);
        }
        return jsonObject;
    }
}
