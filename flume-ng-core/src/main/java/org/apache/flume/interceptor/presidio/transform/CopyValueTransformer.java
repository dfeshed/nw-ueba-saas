package org.apache.flume.interceptor.presidio.transform;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;

import java.util.List;


@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class CopyValueTransformer extends AbstractJsonObjectTransformer {

    public static final String TYPE = "copy_value";
    private static final boolean IS_REMOVE_SOURCE_KEY_DEFAULT = false;


    private String sourceKey;
    private boolean isRemoveSourceKey;
    private List<String> destinationKeys;

    @JsonCreator
    public CopyValueTransformer(@JsonProperty("name") String name, @JsonProperty("sourceKey") String sourceKey, @JsonProperty("isRemoveSourceKey") Boolean isRemoveSourceKey, @JsonProperty("destinationKeys") List<String> destinationKeys){
        super(name);
        this.sourceKey = Validate.notBlank(sourceKey, "sourceKey cannot be blank, empty or null.");
        this.isRemoveSourceKey = isRemoveSourceKey == null ? IS_REMOVE_SOURCE_KEY_DEFAULT : isRemoveSourceKey;
        this.destinationKeys = Validate.notEmpty(destinationKeys, "destinationKeys cannot be empty or null.");
        destinationKeys.forEach(Validate::notBlank);
    }

    @Override
    public JSONObject transform(JSONObject jsonObject) {
        if (!jsonObject.has(sourceKey)) return jsonObject;
        Object sourceValue = jsonObject.get(sourceKey);
        for(String destinationKey: destinationKeys) {
            jsonObject.put(destinationKey, sourceValue);
        }

        if(isRemoveSourceKey){
            jsonObject.remove(sourceKey);
        }

        return jsonObject;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public boolean isRemoveSourceKey() {
        return isRemoveSourceKey;
    }

    public List<String> getDestinationKeys() {
        return destinationKeys;
    }
}
