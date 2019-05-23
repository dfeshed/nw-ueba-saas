package fortscale.utils.transform;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.InternetDomainName;
import fortscale.utils.json.IJsonValueExtractor;
import fortscale.utils.json.JsonPointerValueExtractor;
import fortscale.utils.logging.Logger;
import org.json.JSONObject;

import static org.apache.commons.lang3.Validate.notBlank;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class TopLevelDomainTransformer extends AbstractJsonObjectTransformer{
    private static final Logger logger = Logger.getLogger(TopLevelDomainTransformer.class);

    public static final String TYPE = "top_level_domain";
    private static final boolean IS_REMOVE_SOURCE_KEY_DEFAULT = false;

    private String sourceKey;
    private boolean isRemoveSourceKey;
    private String targetKey;

    @JsonIgnore
    private IJsonValueExtractor jsonValueExtractor;
    @JsonIgnore
    private SetterTransformer targetValueSetter;

    @JsonCreator
    public TopLevelDomainTransformer(
            @JsonProperty("name") String name,
            @JsonProperty("sourceKey") String sourceKey,
            @JsonProperty("isRemoveSourceKey") Boolean isRemoveSourceKey,
            @JsonProperty("targetKey") String targetKey) {

        super(name);
        this.sourceKey = notBlank(sourceKey, "sourceKey cannot be blank, empty or null.");
        this.isRemoveSourceKey = isRemoveSourceKey == null ? IS_REMOVE_SOURCE_KEY_DEFAULT : isRemoveSourceKey;
        this.targetKey = notBlank(targetKey, "targetKey cannot be blank, empty or null.");
        this.jsonValueExtractor = new JsonPointerValueExtractor(sourceKey);
        this.targetValueSetter = SetterTransformer.forKey(targetKey);
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        Object sourceValue = jsonValueExtractor.getValue(jsonObject);
        if (sourceValue == null || !(sourceValue instanceof String)) return jsonObject;

        try {
            InternetDomainName internetDomainName = InternetDomainName.from((String) sourceValue);
            String topLevelDomain = internetDomainName.topPrivateDomain().toString();
            targetValueSetter.setValue(topLevelDomain);
            targetValueSetter.transform(jsonObject);
            // Currently the removal of the source key is only supported for non-hierarchical keys.
            if (isRemoveSourceKey) jsonObject.remove(sourceKey);
        } catch (Exception e){
            logger.debug("got an exception while trying to extract top level domain",e);
        }
        return jsonObject;
    }
}
