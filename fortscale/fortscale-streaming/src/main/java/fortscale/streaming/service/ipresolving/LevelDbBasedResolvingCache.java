package fortscale.streaming.service.ipresolving;

import fortscale.services.ipresolving.cache.ResolvingCache;
import org.apache.samza.storage.kv.KeyValueStore;

/**
 * Resolving cache for ip to hostname resolution which is based on leveldb keyvalue store provided by streaming
 * tasks. This ResolvingCache implementation is meant to be used by streaming tasks that accepts cache update
 * messages from input topic and update the values stored in the cache.
 */
public class LevelDbBasedResolvingCache<T> implements ResolvingCache<T> {

    private KeyValueStore<String, T> store;

    public LevelDbBasedResolvingCache(KeyValueStore<String, T> store) {
        this.store = store;
    }

    @Override
    public T get(String ip) {
        return store.get(ip);
    }

    @Override
    public void put(String ip, T event) {
        store.put(ip, event);
    }


    /**
     * Process cache update messages that arrive from the streaming topic, we use this overload
     * that accepts Object and cached value since we cannot cast the value to the concrete held value by
     * the cache in the ip resolving streaming task (since there are several types of caches with different classes,
     * which will require us to write specific code for each one)
     */
    public void update(String ip, Object event) {
        // just call the put into cache method and cast the event value in order to update the cache
        put(ip, (T)event);
    }

    /**
     * Close the underlying leveldb store and force flush of all pending disk writes.
     * The close method should be called upon streaming task shutdown.
     */
    public void close() {
        if (store!=null)
            store.close();
    }
}
