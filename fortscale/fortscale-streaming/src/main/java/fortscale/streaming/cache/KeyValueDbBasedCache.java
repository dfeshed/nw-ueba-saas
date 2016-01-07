package fortscale.streaming.cache;

import java.io.IOException;

import org.apache.samza.storage.kv.KeyValueStore;

import fortscale.services.cache.CacheHandler;

/**
 * Resolving cached value which is based on keyvalue store provided by streaming
 * tasks. This CacheHandler implementation is meant to be used by streaming tasks that accepts cache update
 * messages from input topic and update the values stored in the cache.
 */
public class KeyValueDbBasedCache<K,T> extends CacheHandler<K,T> {

    private KeyValueStore<K, T> store;

    public KeyValueDbBasedCache(KeyValueStore<K, T> store, Class<T> clazz) {
        super(clazz);
        this.store = store;
    }

    @Override
    public T get(K key) {
        return store.get(key);
    }

    @Override
    public void put(K key, T value) {
        store.put(key, value);
    }

    @Override
    public void remove(K key) {
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
