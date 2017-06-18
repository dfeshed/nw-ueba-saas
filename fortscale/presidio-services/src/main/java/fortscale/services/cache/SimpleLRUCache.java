package fortscale.services.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple LRU cache implementation based on linked hash map data structure
 *
 * @author gils
 * Date: 13/12/2015
 */
public class SimpleLRUCache<K, V> extends LinkedHashMap<K, V> {
    private int maxCacheElements;

    public SimpleLRUCache(int maxCacheElements) {
        super(16, 0.75f, true); // access order ==> LRU eviction policy

        this.maxCacheElements = maxCacheElements;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() >= maxCacheElements;
    }
}
