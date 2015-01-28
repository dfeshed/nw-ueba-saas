package fortscale.streaming.cache;

import fortscale.services.cache.CacheHandler;
import org.apache.samza.storage.kv.KeyValueStore;

import java.io.IOException;

/**
 * Resolving cached value which is based on leveldb keyvalue store provided by streaming
 * tasks. This CacheHandler implementation is meant to be used by streaming tasks that accepts cache update
 * messages from input topic and update the values stored in the cache.
 */
public class LevelDbBasedCache<K,T> extends CacheHandler<K,T> {

    private KeyValueStore<K, T> store;

    public LevelDbBasedCache(KeyValueStore<K, T> store, Class<T> clazz) {
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

    /**
     * Flush the underlying leveldb store pending disk writes.
     * The flush method should be called upon streaming task shutdown or periodically when required.
     */
    @Override
    public void close() throws IOException {
        if (store!=null) {
            store.flush();
        }
    }
}
