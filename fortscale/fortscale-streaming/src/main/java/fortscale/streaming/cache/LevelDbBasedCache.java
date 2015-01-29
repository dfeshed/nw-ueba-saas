package fortscale.streaming.cache;

import fortscale.services.cache.CacheHandler;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolving cached value which is based on leveldb keyvalue store provided by streaming
 * tasks. This CacheHandler implementation is meant to be used by streaming tasks that accepts cache update
 * messages from input topic and update the values stored in the cache.
 */
public class LevelDbBasedCache<K,T> extends CacheHandler<K,T> {

    private KeyValueStore<K, T> store;

    // property that allow to avoid a out of memory problems, in clear method.
    // Can be initialized in 4 ways: default value, spring property value, constructor, setter method.
    // not using only spring, since this class is currently only initialized by new and not spring context.
    @Value("${level-db.cache.record.num.in.memory.for.clear:100000}")
    private Long recordsNumInMemoryForClear = 100000l;

    public LevelDbBasedCache(KeyValueStore<K, T> store, Class<T> clazz, long recordsNumInMemoryForClear) {
        super(clazz);
        this.store = store;
        this.recordsNumInMemoryForClear = recordsNumInMemoryForClear;
    }

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

    @Override
    public long size() {
        throw new UnsupportedOperationException();
    }

    /*
     *  Since keyValueIterator.remove isn't support and store.delete while iterating is not safe
     *  First going over all the store data entries add them into a list, when done delete them from the store itself.
     *  all of this is wrapped with another loop limited by the number of records holds in memory, to avoid a out of memory problems
     *
     */
    @Override
    public void clear() {
        boolean keepCleaning = true;
        while (keepCleaning) {
            keepCleaning = false;
            KeyValueIterator<K, T> keyValueIterator = store.all();
            List<Entry<K, T>> entryList = new ArrayList<Entry<K, T>>();
            while (keyValueIterator.hasNext()) {
                if (entryList.size() < recordsNumInMemoryForClear) {
                    entryList.add(keyValueIterator.next());
                } else {
                    keepCleaning = true;
                    break;
                }
            }
            keyValueIterator.close();

            // remove from store all records
            for (Entry<K, T> entry : entryList) {
                store.delete(entry.getKey());
            }
            entryList.clear();
        }
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

    public Long getRecordsNumInMemoryForClear() {
        return recordsNumInMemoryForClear;
    }

    public void setRecordsNumInMemoryForClear(Long recordsNumInMemoryForClear) {
        this.recordsNumInMemoryForClear = recordsNumInMemoryForClear;
    }
}
