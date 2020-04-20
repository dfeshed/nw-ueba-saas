package fortscale.utils.json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static org.apache.commons.lang3.Validate.notNull;

public class JsonUtils {
    private static final String NULL_ROOT_EXCEPTION_MESSAGE = "root cannot be null.";
    private static final String NULL_KEY_EXCEPTION_MESSAGE = "key cannot be null.";
    private static final String DELIMITING_REGEX = "\\.";

    /**
     * Get from the given {@link JSONObject} the value associated with the given key.
     *
     * @param root The {@link JSONObject} from which the value should be retrieved.
     * @param key  The key whose value should be retrieved.
     * @param <T>  The type of the value.
     * @return The value.
     * @throws BrokenHierarchyException If the {@link JSONObject} does not contain the key.
     */
    public static <T> T get(JSONObject root, String key) {
        return get(root, key, true, null);
    }

    /**
     * Get from the given {@link JSONObject} the value associated with the given key,
     * or the given default value if the {@link JSONObject} does not contain the key.
     *
     * @param root         The {@link JSONObject} from which the value should be retrieved.
     * @param key          The key whose value should be retrieved.
     * @param defaultValue The default value.
     * @param <T>          The type of the value.
     * @return The value.
     */
    public static <T> T get(JSONObject root, String key, T defaultValue) {
        return get(root, key, false, defaultValue);
    }

    /**
     * Associate the given value with the given key, in the given {@link JSONObject}.
     * If it is broken, the complete key hierarchy is computed, from the root to the value.
     *
     * @param root  The {@link JSONObject}.
     * @param key   The key.
     * @param value The value.
     * @param <T>   The type of the value.
     */
    public static <T> void set(JSONObject root, String key, T value) {
        JSONObject jsonObject = notNull(root, NULL_ROOT_EXCEPTION_MESSAGE);
        String[] subkeys = notNull(key, NULL_KEY_EXCEPTION_MESSAGE).split(DELIMITING_REGEX);
        int lastIndex = subkeys.length - 1;

        for (int i = 0; i < lastIndex; ++i) {
            jsonObject = computeIfAbsent(jsonObject, subkeys[i]);
        }

        jsonObject.put(subkeys[lastIndex], value == null ? JSONObject.NULL : value);
    }

    /**
     * Get a new {@link List} containing the elements in the given {@link JSONArray}.
     * If the {@link JSONArray} is null, an empty {@link List} is returned.
     *
     * @param jsonArray The {@link JSONArray}.
     * @param <T>       The type of the elements.
     * @return The new {@link List}.
     */
    public static <T> List<T> toList(JSONArray jsonArray) {
        return jsonArray == null ? Collections.emptyList() : IntStream.range(0, jsonArray.length()).boxed()
                .map(i -> {
                    Object element = jsonArray.get(i);
                    // noinspection unchecked - Class cast exception might be thrown.
                    return element == JSONObject.NULL ? null : (T)element;
                })
                .collect(Collectors.toList());
    }

    private static <T> T get(JSONObject root, String key, boolean validateCompleteHierarchy, T defaultValue) {
        JSONObject jsonObject = notNull(root, NULL_ROOT_EXCEPTION_MESSAGE);
        String[] subkeys = notNull(key, NULL_KEY_EXCEPTION_MESSAGE).split(DELIMITING_REGEX);
        int lastIndex = subkeys.length - 1;

        for (int i = 0; i < lastIndex; ++i) {
            if ((jsonObject = jsonObject.optJSONObject(subkeys[i])) == null) {
                if (validateCompleteHierarchy) {
                    throw new BrokenHierarchyException(root, key, subkeys[i], i);
                } else {
                    return defaultValue;
                }
            }
        }

        Object value = jsonObject.opt(subkeys[lastIndex]);
        // noinspection unchecked - Class cast exception might be thrown.
        return value == JSONObject.NULL ? null : (T)value;
    }

    private static JSONObject computeIfAbsent(JSONObject jsonObject, String key) {
        // Get the value associated with the given key as a JSON object.
        JSONObject value = jsonObject.optJSONObject(key);

        // Compute the value if it is absent, and put it in the given JSON object.
        if (value == null) {
            value = new JSONObject();
            jsonObject.put(key, value);
        }

        return value;
    }

    private static final class BrokenHierarchyException extends RuntimeException {
        public BrokenHierarchyException(JSONObject root, String key, String subkey, int i) {
            super(format("Broken hierarchy - root: %s, key: %s, subkey: %s, i: %d.", root.toString(), key, subkey, i));
        }
    }
}
