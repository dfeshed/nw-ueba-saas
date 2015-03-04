package fortscale.geoip;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Base class the provides caching functionality for geo ip resolving services implementations
 */
public abstract class CachedGeoIPService implements GeoIPService, InitializingBean {

	private Cache<String, IGeoIPInfo> cache;
	
	@Value("${geolocation.cache.maximum.size:1000}")
	private long cacheSize;
	@Value("${geolocation.cache.expire.after.write.in.min:60}")
	private long cacheExpireAfterWriteInMin;

	@Override
	public void afterPropertiesSet() throws Exception {
		// initialize cache
		CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder().maximumSize(cacheSize);
		if(cacheExpireAfterWriteInMin>0){
			cacheBuilder.expireAfterWrite(cacheExpireAfterWriteInMin, TimeUnit.MINUTES);
		}
		cache = cacheBuilder.build();
	}

	protected abstract IGeoIPInfo doGetGeoIPInfo(String IPAddress) throws UnknownHostException;

	
	@Override
	public IGeoIPInfo getGeoIPInfo(String IPAddress) throws UnknownHostException {
		// lookup the cache if it was created
		if (cache!=null) {
			IGeoIPInfo geoInfo = cache.getIfPresent(IPAddress);
			if (geoInfo!=null)
				return geoInfo;
		}
		
		// perform the actual lookup if not found in cache
		IGeoIPInfo geoInfo = doGetGeoIPInfo(IPAddress);
		// save in cache
		if (cache!=null && geoInfo!=null)
			cache.put(IPAddress, geoInfo);
		
		return geoInfo;		
	}

}
