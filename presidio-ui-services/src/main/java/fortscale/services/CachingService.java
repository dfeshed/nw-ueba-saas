package fortscale.services;

import fortscale.services.cache.CacheHandler;

/**
 * All services that handle inner cache and want to allow direct access to the cache.
 * Main use is for streaming task updates of the cache after update have arrived to kafka update topic.
 *
 */
public interface CachingService<K,T> {

	CacheHandler<K,T> getCache();

	void setCache(CacheHandler<K,T> cache);

	void handleNewValue(String key, String value) throws Exception;
}
