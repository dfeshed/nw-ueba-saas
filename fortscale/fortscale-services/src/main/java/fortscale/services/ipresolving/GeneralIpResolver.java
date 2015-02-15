package fortscale.services.ipresolving;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fortscale.domain.events.IpToHostname;
import fortscale.services.CachingService;
import fortscale.services.cache.CacheHandler;
import org.apache.commons.lang3.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by danal on 15/02/2015.
 */
public abstract class GeneralIpResolver<T extends IpToHostname>  implements CachingService<String,T>{


	private Class<T> clazz;

	// json serializer to serialize and deserialize cache values to json
	protected ObjectMapper mapper;

	private static Logger logger = LoggerFactory.getLogger(GeneralIpResolver.class);


	// blackIpHashSetCache is used to keep track of ip addresses that couldn't not be resolved into hostname using
	// the dhcp events. We keep track of those ip addresses to prevent us from looking them up over and over again
	// for each ip we keep the latest know time range in which there isn't resolving
	protected CacheHandler<String,Range<Long>> ipBlackListCache;

	protected boolean shouldUseBlackList;

	public GeneralIpResolver(boolean shouldUseBlackList, CacheHandler<String,Range<Long>> ipBlackListCache, Class<T> clazz ) {
		if (clazz != null) {
			this.clazz = clazz;

			mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		} else {
			logger.error("CacheHandler created with null clazz");
		}
	}

	//for testing
	protected GeneralIpResolver ()
	{

	}



	// for unit testing
	public void setShouldUseBlackList(boolean shouldUseBlackList) {
		this.shouldUseBlackList = shouldUseBlackList;
	}

	/**
	 * Process cache update that come as strings.
	 * Notice that the updates should be json serialized format of the value type.
	 */
	public void removeFromBlackList(String stringValue) throws Exception {
		T value = convertStringToValue(stringValue);
		// add the value to the cache
		this.removeFromBlackList(value);
	}

	protected abstract void removeFromBlackList(T event);

	protected T convertStringToValue(String stringValue) throws Exception{
		if (mapper != null) {
			// deserialize the event to relevant class
			return mapper.readValue(stringValue, clazz);
		}
		return null;
	}

	protected abstract List<T> getNextEvents(String ip, Long upperTsLimit);

	// we only save in the blacklist the last time range we know there is no resolving for.
	// since every time we add a time range to the blacklist we get the biggest time range possible from mongo, according to the queried ts
	// the only scenarios in which we won't update the blacklist is in which the existing value is regarding a newer time period.
	protected void addToBlackList(String ip, long lowerLimitTs, long upperTsLimit){
		if (shouldUseBlackList) {
			List<T> nextEvents = getNextEvents(ip, upperTsLimit);
			Long startTimestamp = lowerLimitTs;
			Long endTimestamp = Long.MAX_VALUE;
			if (!nextEvents.isEmpty()) {
				T nextEvent = nextEvents.get(0);
				endTimestamp = nextEvent.getTimestampepoch();
			}
			Range<Long> newBlacklistTimeRange = Range.between(startTimestamp, endTimestamp);
			if (ipBlackListCache.containsKey(ip)){
				Range<Long> blacklistTimeRange = ipBlackListCache.get(ip);
				if (!newBlacklistTimeRange.isBeforeRange(blacklistTimeRange)) {
					ipBlackListCache.put(ip, newBlacklistTimeRange);
				}
			}
			else {
				ipBlackListCache.put(ip, newBlacklistTimeRange);
			}
		}
	}

	//TODO: improve the logic of the blacklist time range
	// currently we support only 2 scenarios
	// 1. the new resolving event limits the end of the current time range saved in the black list.
	//    in this case we keep the blacklist record but limits its end time to the time epoch of the new resolving event
	// 2. the current time range saved in the black list and the new resolving event intersect with it in some manner
	//    in this case we remove the blacklist record completely
	public void removeFromBlackList(String ip, Long newResolveLowerTs, Long newResolveUpperTs ){
		if (shouldUseBlackList) {
			if (ipBlackListCache.containsKey(ip)){
				Range<Long> blacklistTimeRange = ipBlackListCache.get(ip);
				Range<Long> resolvedTimeRange = Range.between(newResolveLowerTs,newResolveUpperTs);
				//if the current resolving limits the time range in the black list, update the end timestamp saved in the black list
				if (newResolveLowerTs != null && blacklistTimeRange.contains(newResolveLowerTs)){
					blacklistTimeRange = Range.between(blacklistTimeRange.getMinimum(),newResolveLowerTs);
					ipBlackListCache.put(ip, blacklistTimeRange);
				}
				//if there is an intersection between the new resolving timestamp and the know black list range remove the black list entry
				else if(blacklistTimeRange.isOverlappedBy(resolvedTimeRange)){
					ipBlackListCache.remove(ip);
				}
			}
		}
	}

	@Override
	public void handleNewValue(String key, String value) throws Exception{
		if(value == null){
			getCache().remove(key);
		}
		else {
			getCache().putFromString(key, value);
			removeFromBlackList(value);
		}
	}
}
