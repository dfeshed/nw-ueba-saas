package fortscale.services.ipresolving.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Resolving cache that is based on guava's in memory cache implementation
 */
public class MemoryBasedCache<T> implements  ResolvingCache<T> {

    private Cache<String, T> cache;

    /**
     * Initialize a new memory based cache with maximum size and no time expiration of items
     * @param maxSize if greater than 0, will restrict the cache size
     */
    public MemoryBasedCache(int maxSize) {
        this(maxSize, 0);
    }

    /**
     * initialize a new memory based cache with maximum size and time to expire items after write
     * @param maxSize if greater than 0, will restrict the cache size
     * @param timeToExpire if greater than 0, will evict entries from cache after write. the value is in seconds
     */
    public MemoryBasedCache(int maxSize, int timeToExpire) {
        CacheBuilder builder = CacheBuilder.newBuilder();
        if (maxSize>0)
            builder.maximumSize(maxSize);
        if (timeToExpire>0)
            builder.expireAfterWrite(timeToExpire, TimeUnit.SECONDS);

        cache = builder.build();
    }

    public T get(String ip) {
        return cache.getIfPresent(ip);
    }

    public void put(String ip, T event) {
        cache.put(ip, event);
    }
}
