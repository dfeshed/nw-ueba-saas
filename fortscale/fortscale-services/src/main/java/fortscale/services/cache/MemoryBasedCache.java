package fortscale.services.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Resolving cache that is based on guava's in memory cache implementation
 */
public class MemoryBasedCache<K,T> extends CacheHandler<K,T> {

    private Cache<K, T> cache;

    /**
     * Initialize a new memory based cache with maximum size and no time expiration of items
     * @param maxSize if greater than 0, will restrict the cache size
     */
    public MemoryBasedCache(int maxSize, Class<T> clazz) {
        this(maxSize, 0, clazz);
    }

    /**
     * initialize a new memory based cache with maximum size and time to expire items after write
     * @param maxSize if greater than 0, will restrict the cache size
     * @param timeToExpire if greater than 0, will evict entries from cache after write. the value is in seconds
     */
    public MemoryBasedCache(int maxSize, int timeToExpire, Class<T> clazz) {
        super(clazz);
        CacheBuilder builder = CacheBuilder.newBuilder();
        if (maxSize>0)
            builder.maximumSize(maxSize);
        if (timeToExpire>0)
            builder.expireAfterWrite(timeToExpire, TimeUnit.SECONDS);
        cache = builder.build();
    }

    public Cache<K, T> getCache() {
        return cache;
    }

    public void setCache(Cache<K, T> cache) {
        this.cache = cache;
    }

    @Override
    public T get(K key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void put(K key, T value) {
        cache.put(key, value);
    }

    @Override
    public void remove(K key) {
        cache.invalidate(key);
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public void close() throws IOException {
        cache.cleanUp();
    }

    @Override
    public Map<K, T> getAll() {
        return cache.asMap();
    }

}
