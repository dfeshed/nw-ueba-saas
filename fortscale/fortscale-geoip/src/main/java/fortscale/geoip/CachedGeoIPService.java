package fortscale.geoip;

import java.net.UnknownHostException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Base class the provides caching functionality for geo ip resolving services implementations
 */
public abstract class CachedGeoIPService implements GeoIPService, InitializingBean {

	private Cache<String, GeoIPInfo> cache;
	
	@Value("${geolocation.cache.maximum.size:1000}")
	private long cacheSize;

	@Override
	public void afterPropertiesSet() throws Exception {
		// initialize cache
		cache = CacheBuilder.newBuilder().maximumSize(cacheSize).build();
	}

	protected abstract GeoIPInfo doGetGeoIPInfo(String IPAddress) throws UnknownHostException;

	
	@Override
	public GeoIPInfo getGeoIPInfo(String IPAddress) throws UnknownHostException {
		// lookup the cache if it was created
		if (cache!=null) {
			GeoIPInfo geoInfo = cache.getIfPresent(IPAddress);
			if (geoInfo!=null)
				return geoInfo;
		}
		
		// perform the actual lookup if not found in cache
		GeoIPInfo geoInfo = doGetGeoIPInfo(IPAddress);
		// save in cache
		if (cache!=null && geoInfo!=null)
			cache.put(IPAddress, geoInfo);
		
		return geoInfo;		
	}

}
