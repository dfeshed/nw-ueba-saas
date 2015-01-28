package fortscale.services;

import fortscale.domain.core.Computer;
import fortscale.services.cache.CacheHandler;

import java.io.IOException;

/**
 * All services that handle inner cache and want to allow direct access to the cache.
 * Main use is for streaming task updates of the cache after update have arrived to kafka update topic.
 *
 */
public interface CachingService {

	CacheHandler getCache();

	void setCache(CacheHandler cache);
}
