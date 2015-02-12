package fortscale.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * All caching services that also handle a black list to avoid repeating miss-cached calls to repository
 */
public abstract class CachingServiceWithBlackList<K,T> implements CachingService<K,T> {

	private static Logger logger = LoggerFactory.getLogger(CachingServiceWithBlackList.class);

	private Class<T> clazz;

	// json serializer to serialize and deserialize cache values to json
	protected ObjectMapper mapper;

	public CachingServiceWithBlackList(Class<T> clazz) {
		if (clazz != null) {
			this.clazz = clazz;

			mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		} else {
			logger.error("CacheHandler created with null clazz");
		}
	}

	public abstract void removeFromBlackList(T value);

	/**
	 * Process cache update that come as strings.
	 * Notice that the updates should be json serialized format of the value type.
	 */
	public void removeFromBlackList(String stringValue) throws Exception {
		T value = convertStringToValue(stringValue);
		// add the value to the cache
		this.removeFromBlackList(value);
	}

	protected T convertStringToValue(String stringValue) throws Exception{
		if (mapper != null) {
			// deserialize the event to relevant class
			return mapper.readValue(stringValue, clazz);
		}
		return null;
	}
}
