package fortscale.services.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.Closeable;

public abstract class CacheHandler<K,T> implements Closeable {

    private Class<T> clazz;

    // json serializer to serialize and deserialize cache values to json
    protected ObjectMapper mapper;

    public CacheHandler(Class<T> clazz) {
        if (clazz != null) {
            this.clazz = clazz;

            mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        }
    }

    public abstract T get(K key);
    public abstract void put(K key, T value);
    public abstract void remove(K key);

    /**
     * Process cache update that come as strings.
     * Notice that the updates should be json serialized format of the value type.
     */
    public void putFromString(K key, String stringValue) throws Exception{
        if (mapper != null) {
            // deserialize the event to relevant class
            T value = mapper.readValue(stringValue, clazz);

            // add the event to the cache
            this.put(key, value);
        }
    }
}
