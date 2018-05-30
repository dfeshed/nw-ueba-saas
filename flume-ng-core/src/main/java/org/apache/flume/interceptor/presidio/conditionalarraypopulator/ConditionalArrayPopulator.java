package org.apache.flume.interceptor.presidio.conditionalarraypopulator;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notEmpty;

/**
 * Takes from a given {@link JSONObject} the string associated with {@link #sourceKey} and looks for the first
 * {@link ConditionAndArrayValues} x, such that the string matches x's pattern (the configurations are traversed in
 * order). If a match is found, the {@link ConditionalArrayPopulator} adds all the values configured in x to the
 * {@link JSONObject}'s array associated with {@link #destinationKey}. If it exists, the array can be overwritten
 * beforehand (i.e. before starting the look up) by turning on the {@link #overwriteArray} flag.
 *
 * @author Lior Govrin.
 */
@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class ConditionalArrayPopulator {
    private String sourceKey;
    private String destinationKey;
    private boolean overwriteArray;
    private List<ConditionAndArrayValues> conditionAndArrayValuesList;

    @JsonCreator
    public ConditionalArrayPopulator(
            @JsonProperty("sourceKey") String sourceKey,
            @JsonProperty("destinationKey") String destinationKey,
            @JsonProperty("overwriteArray") Boolean overwriteArray,
            @JsonProperty("conditionAndArrayValuesList") List<ConditionAndArrayValues> conditionAndArrayValuesList) {

        notBlank(sourceKey, "sourceKey cannot be blank, empty or null.");
        notBlank(destinationKey, "destinationKey cannot be blank, empty or null.");
        // A null overwriteArray is interpreted as false.
        notEmpty(conditionAndArrayValuesList, "conditionAndArrayValuesList cannot be empty or null.");

        this.sourceKey = sourceKey;
        this.destinationKey = destinationKey;
        this.overwriteArray = overwriteArray == null ? false : overwriteArray;
        this.conditionAndArrayValuesList = conditionAndArrayValuesList;
    }

    public JsonObject checkAndPopulateArray(JsonObject jsonObject) {
        if (!jsonObject.has(sourceKey) || jsonObject.get(sourceKey).isJsonNull()) return jsonObject;
        String sourceValue = jsonObject.get(sourceKey).getAsString();
        JsonArray jsonArray = overwriteArray || !jsonObject.has(destinationKey) ? new JsonArray() : jsonObject.getAsJsonArray(destinationKey);

        for (ConditionAndArrayValues conditionAndArrayValues : conditionAndArrayValuesList) {
            Matcher matcher = conditionAndArrayValues.getPattern().matcher(sourceValue);

            if (matcher.matches()) {
                conditionAndArrayValues.getValues().forEach(value -> jsonArray.add(new JsonPrimitive(value)));
                break;
            }
        }

        jsonObject.add(destinationKey, jsonArray);
        return jsonObject;
    }
}
