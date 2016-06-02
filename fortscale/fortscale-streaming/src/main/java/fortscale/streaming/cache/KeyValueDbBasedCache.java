package fortscale.streaming.cache;

import java.io.IOException;

import fortscale.utils.monitoring.stats.StatsService;
import org.apache.samza.storage.kv.KeyValueStore;

import fortscale.services.cache.CacheHandler;

/**
 * Resolving cached value which is based on keyvalue store provided by streaming
 * tasks. This CacheHandler implementation is meant to be used by streaming tasks that accepts cache update
 * messages from input topic and update the values stored in the cache.
 */
public class KeyValueDbBasedCache<K,T> extends CacheHandler<K,T> {

    private KeyValueStore<K, T> store;

    // Metrics
    protected KeyValueDbBasedCacheMetrics metrics;

    /**
     * ctor without stats metrics
     *
     * @param store
     * @param clazz
     */
    public KeyValueDbBasedCache(KeyValueStore<K, T> store, Class<T> clazz) {
        this(store, clazz, "NAME-NOT-SET", null /* StatsService */);
    }

    /**
     * ctor with stats metrics
     *
     * @param store
     * @param clazz
     */
    public KeyValueDbBasedCache(KeyValueStore<K, T> store, Class<T> clazz, String name, StatsService statsService) {
        super(clazz);
        this.store = store;
        this.metrics = new KeyValueDbBasedCacheMetrics(statsService, name);
    }


    @Override
    public T get(K key) {
        metrics.get++;
        T result = store.get(key);
        if (result == null) {
            metrics.getNotFound++;
        }

        return result;
    }

    @Override
    public void put(K key, T value) {
        metrics.put++;
        store.put(key, value);
    }

    @Override
    public void remove(K key) {
        metrics.remove++;
        store.delete(key);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Flush the underlying key-value store pending disk writes.
     * The flush method should be called upon streaming task shutdown or periodically when required.
     */
    @Override
    public void close() throws IOException {
        if (store!=null) {
            store.flush();
        }
    }
}
