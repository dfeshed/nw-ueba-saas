package fortscale.utils.data;

import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LfuCache<K, V> {
    private final int maximumSize;
    private final double entriesToRemovePercentage;
    private final Map<K, Countable<V>> map;

    public LfuCache(int maximumSize, double entriesToRemovePercentage) {
        Validate.isTrue(maximumSize > 0, "maximumSize must be greater than 0 but is %d.", maximumSize);
        Validate.isTrue(entriesToRemovePercentage > 0.0 && entriesToRemovePercentage <= 100.0,
            "entriesToRemovePercentage must be in the interval (0.0, 100.0] but is %f.", entriesToRemovePercentage);
        this.maximumSize = maximumSize;
        this.entriesToRemovePercentage = entriesToRemovePercentage;
        this.map = new HashMap<>();
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public V get(K key) {
        Countable<V> countable = map.get(key);
        if (countable == null) return null;
        V element = countable.getElement();
        countable.incrementCount();
        return element;
    }

    public Map<K, V> put(K key, V value) {
        Countable<V> countable = map.get(key);

        if (countable == null) {
            Map<K, V> lfuEntries = map.size() == maximumSize ? removeLfuEntries() : Collections.emptyMap();
            map.put(key, new Countable<>(value, 1));
            return lfuEntries;
        } else {
            countable.setElement(value);
            countable.incrementCount();
            return Collections.emptyMap();
        }
    }

    public Map<K, V> clear() {
        Map<K, V> entries = new HashMap<>();
        // Collect all the entries to a map from keys to values (do not expose the counts).
        map.forEach((key, value) -> entries.put(key, value.getElement()));
        // Remove all the entries from this LFU cache.
        map.clear();
        return entries;
    }

    private Map<K, V> removeLfuEntries() {
        Map<K, V> lfuEntries = new HashMap<>();
        map.entrySet().stream()
                // Sort according to the number of times each entry was referenced (in ascending order).
                .sorted(Entry.comparingByValue())
                // Truncate the stream and keep only the first specified percentage of entries.
                .limit((long)Math.ceil(map.size() * entriesToRemovePercentage / 100.0))
                // Collect the entries to a map from keys to values (do not expose the counts).
                .forEach(entry -> lfuEntries.put(entry.getKey(), entry.getValue().getElement()));
        // Remove the entries from this LFU cache.
        lfuEntries.keySet().forEach(map::remove);
        return lfuEntries;
    }
}
