package fortscale.streaming.task.enrichment;

import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of KeyValueStore for unit tests
 * Date: 1/14/2015.
 */
public class KeyValueStoreMock<K, V>  implements KeyValueStore<K, V>  {

	Map<K, V> map = new HashMap<>();


	@Override public V get(K key) {
		return map.get(key);
	}

	@Override public void put(K key, V value) {
		map.put(key, value);

	}

	@Override public void putAll(List<Entry<K, V>> entries) {
		throw new UnsupportedOperationException();

	}

	@Override public void delete(K key) {
		map.remove(key);
	}

	@Override public KeyValueIterator<K, V> range(K from, K to) {
		throw new UnsupportedOperationException();
	}

	@Override public KeyValueIterator<K, V> all() {
		throw new UnsupportedOperationException();
	}

	@Override public void close() {
		throw new UnsupportedOperationException();

	}

	@Override public void flush() {
		throw new UnsupportedOperationException();
	}
}
