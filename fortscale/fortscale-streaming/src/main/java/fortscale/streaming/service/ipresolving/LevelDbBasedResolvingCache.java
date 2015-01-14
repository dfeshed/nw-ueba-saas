package fortscale.streaming.service.ipresolving;

import fortscale.services.ipresolving.cache.ResolvingCache;
import org.apache.samza.storage.kv.KeyValueStore;

/**
 * Resolving cache for ip to hostname resolution which is based on leveldb keyvalue store provided by streaming
 * tasks. This ResolvingCache implementation is meant to be used by streaming tasks that accepts cache update
 * messages from input topic and update the values stored in the cache.
 */
public abstract class LevelDbBasedResolvingCache<T> implements ResolvingCache<T> {

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
     * which will require us to write specific code for each one).
     * Notice that the event should be json serialized format of the update message.
     */
    public abstract void update(String ip, String event) throws Exception;

    /**
     * Flush the underlying leveldb store pending disk writes.
     * The flush method should be called upon streaming task shutdown or periodically when required.
     */
    public void flush() {
        if (store!=null)
            store.flush();
    }
}
