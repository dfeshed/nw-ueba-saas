package fortscale.utils.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A "Least Frequently Used" cache implementation, that keeps track of the number of times an entry is referenced:
 * Each time {@link #get(K)} or {@link #put(K, V)} are called, this class increments by one the number of times
 * the relevant entry was referenced - This does not apply to {@link #containsKey(K)} or the rest of the methods.
 *
 * The {@link LfuCache} has a maximum size (given upon instantiation), and the user can call {@link #isFull()} to check
 * whether this size was reached. The user is also responsible to free up some space once the cache is full by calling
 * {@link #removeLfuEntries(double)}, otherwise calling {@link #put(K, V)} with a new key will throw an exception.
 *
 * @param <K> The class of the keys.
 * @param <V> The class of the values.
 * @author Lior Govrin.
 */
public class LfuCache<K, V> {
    private final int maximumSize;
    private final Map<K, Countable<V>> map;

    /**
     * Constructor.
     *
     * @param maximumSize The maximum size of this {@link LfuCache}.
     * @throws IllegalArgumentException If the specified maximum size is illegal.
     */
    public LfuCache(int maximumSize) {
        if (maximumSize <= 0) {
            String s = String.format("maximumSize must be greater than zero but is %d.", maximumSize);
            throw new IllegalArgumentException(s);
        }

        this.maximumSize = maximumSize;
        this.map = new HashMap<>();
    }

    /**
     * Checks whether this {@link LfuCache} contains a mapping for the specified key.
     *
     * @param key The key whose presence is to be checked.
     * @return True if there is a mapping for the specified key, false otherwise.
     */
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this {@link LfuCache} does not contain a
     * mapping for the key. Note that the value can also be null if the key is mapped to null: To distinguish between
     * the two cases, {@link #containsKey(K)} should be called. If there is a mapping for the key, the number of times
     * this mapping was referenced is incremented by one.
     *
     * @param key The key whose associated value is to be returned.
     * @return The associated value or null if there isn't a mapping for the specified key.
     */
    public V get(K key) {
        Countable<V> countable = map.get(key);
        if (countable == null) return null;
        V element = countable.getElement();
        countable.incrementCount();
        return element;
    }

    /**
     * Associates the specified value with the specified key. If this {@link LfuCache} previously contained a mapping
     * for the key, the old value is replaced, and the number of times this mapping was referenced is incremented by
     * one. Otherwise, the value is associated with the key, and the number of times this new mapping was referenced is
     * set to one. If this is a new mapping, and {@link #maximumSize} was already reached, an exception will be thrown.
     *
     * @param key   The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     * @throws IllegalStateException If this is a new mapping, and there is no more space in the cache.
     */
    public void put(K key, V value) {
        Countable<V> countable = map.get(key);

        if (countable == null) {
            if (isFull()) {
                String s = String.format("The LFU cache reached its maximum size of %s entries.", maximumSize);
                throw new IllegalStateException(s);
            }

            map.put(key, new Countable<>(value, 1));
        } else {
            countable.setElement(value);
            countable.incrementCount();
        }
    }

    /**
     * Removes from this {@link LfuCache} the specified number of mappings that were least frequently used. For example:
     * If the cache contains 50 mappings, and number is 10, then the 10 mappings that were referenced the least number
     * of times will be removed.
     *
     * @param number The number of mappings to remove.
     * @return The least frequently used mappings, that were removed from the cache.
     * @throws IllegalArgumentException If the specified number is illegal.
     */
    public Map<K, V> removeLfuEntries(int number) {
        assertNumber(number);
        Map<K, V> lfuEntries = new HashMap<>();
        map.entrySet().stream()
                // Sort according to the number of times each entry was referenced (in ascending order).
                .sorted(Entry.comparingByValue())
                // Truncate the stream and keep only the first specified number of entries.
                .limit(number)
                // Collect the entries to a map from keys to values (do not expose the counts).
                .forEach(entry -> lfuEntries.put(entry.getKey(), entry.getValue().getElement()));
        // Remove the entries from this LFU cache.
        lfuEntries.keySet().forEach(map::remove);
        return lfuEntries;
    }

    /**
     * Removes from this {@link LfuCache} the specified percentage of mappings that were least frequently used. For
     * example: If the cache contains 50 mappings, and percentage is 10.0, then the 5 mappings that were referenced the
     * least number of times will be removed. The number of mappings to remove is rounded up when calculated.
     *
     * @param percentage The percentage of mappings to remove.
     * @return The least frequently used mappings, that were removed from the cache.
     * @throws IllegalArgumentException If the specified percentage is illegal.
     */
    public Map<K, V> removeLfuEntries(double percentage) {
        assertPercentage(percentage);
        return removeLfuEntries((int)Math.ceil(map.size() * percentage / 100.0));
    }

    /**
     * This method is equivalent to {@link #removeLfuEntries(double)}, where percentage is equal to 100.0.
     *
     * @see #removeLfuEntries(double).
     */
    public Map<K, V> removeAllLfuEntries() {
        return removeLfuEntries(100.0);
    }

    /**
     * Checks whether the maximum size of this {@link LfuCache} was reached.
     *
     * @return True if the cache is full, false otherwise.
     */
    public boolean isFull() {
        return map.size() == maximumSize;
    }

    /**
     * Calculates the number of new mappings that can be put in this {@link LfuCache} before it exceeds its maximum
     * size. For example: If the maximum size is 10, and the cache contains 3 mappings, then this method will return 7.
     *
     * @return The amount of free space in the cache.
     */
    public int calculateFreeSpace() {
        return maximumSize - map.size();
    }

    public static void assertNumber(int number) {
        if (number <= 0) {
            String s = String.format("number must be greater than 0 but is %d.", number);
            throw new IllegalArgumentException(s);
        }
    }

    public static void assertPercentage(double percentage) {
        if (percentage <= 0.0 || 100.0 < percentage) {
            String s = String.format("percentage must be in the range (0,100] but is %f.", percentage);
            throw new IllegalArgumentException(s);
        }
    }
}
