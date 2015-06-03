package fortscale.collection.tagging.service;

import fortscale.domain.ad.AdGroup;
import fortscale.services.UserService;
import fortscale.services.cache.CacheHandler;
import org.apache.hadoop.classification.InterfaceAudience;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

/**
 * Created by tomerd on 27/05/2015.
 */
public class ActiveDirectoryGroupsHelper implements InitializingBean {

	@Autowired @Qualifier("adGroupsCache") private CacheHandler<String, String> adGroupsCache;

	@Value("${adgroups.resolver.cache.max.items:30000}") private int maxCacheSize;

	/**
	 * hold connection to the repository
	 */
	@Autowired private UserService userService;

	/**
	 * Flag to indicate whether to search for values in repository on cache miss
	 */
	private boolean searchInRepositoryOnCacheMiss;

	/**
	 * Create new instance of the cache handler.
	 * Warm up the cache by reading values from repository
	 */
	@Override public void afterPropertiesSet() throws Exception {

		// Read values from repository
		List<AdGroup> collection = readFromRepository();

		// Load values to cache
		loadAll(collection);

		// Set flag - whether the repository is bigger than the 'maxCacheSize'
		if (collection.size() > maxCacheSize) {
			searchInRepositoryOnCacheMiss = true;
		} else {
			searchInRepositoryOnCacheMiss = false;
		}
	}

	/**
	 * Get value from handler
	 *
	 * @param key
	 * @return
	 */
	public String get(String key) {
		String returnValue;

		returnValue = adGroupsCache.get(key);

		// In case of cache miss and the flag is set, read value from repository
		if (returnValue == null) {
			if (searchInRepositoryOnCacheMiss) {
				returnValue = userService.findAdMembers(key);

				// In case we had a cache miss, and the key's value exists in the repository, add it to cache
				if (returnValue != null) {
					put(key, returnValue);
				}
			}
		}

		return returnValue;
	}

	/**
	 * Put a single value in cache
	 *
	 * @param key
	 * @param value
	 */
	public void put(String key, String value) {
		adGroupsCache.put(key, value);
	}

	/**
	 * Load a collection of values into cache
	 * In case the collection has more values than 'maxCacheSize', read only the first 'maxCacheSize' values
	 *
	 * @param collection
	 */
	public void loadAll(List<AdGroup> collection) {

		for (AdGroup adGroup : collection) {
			put(adGroup.getDistinguishedName(), adGroup.getMember());
		}
	}

	/**
	 * Read values from repository
	 *
	 * @return
	 */
	private List<AdGroup> readFromRepository() {
		return userService.getActiveDirectoryGroups(maxCacheSize + 1);
	}
}
