package fortscale.utils.data;

import java.io.Serializable;

/**
 * <p>A convenience class to represent name-value pairs.</p>
 */
public class Pair<K,V> implements Serializable {

    /**
     * Key of this <code>Pair</code>.
     */
    private K key;

    /**
     * Gets the key for this pair.
     * @return key for this pair
     */
    public K getKey() { return key; }

    /**
     * Value of this this <code>Pair</code>.
     */
    private V value;

    /**
     * Gets the value for this pair.
     * @return value for this pair
     */
    public V getValue() { return value; }

    /**
     * Creates a new pair
     * @param key The key for this pair
     * @param value The value to use for this pair
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

    @Override
    public int hashCode() {
        return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Pair) {
            Pair pair = (Pair) o;
            if (key != null ? !key.equals(pair.key) : pair.key != null) return false;
            if (value != null ? !value.equals(pair.value) : pair.value != null) return false;
            return true;
        }
        return false;
    }
}