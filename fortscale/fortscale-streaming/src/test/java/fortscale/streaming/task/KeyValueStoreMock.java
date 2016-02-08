package fortscale.streaming.task;

import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implementation of KeyValueStore for unit tests.
 * This implementation supports the 'all()' operation (iterator).
 * Date: 1/14/2015.
 */
public class KeyValueStoreMock<K, V> implements KeyValueStore<K, V> {
	private Map<K, V> map = new HashMap<>();

	@Override
	public V get(K key) {
		return map.get(key);
	}

	@Override
	public void put(K key, V value) {
		map.put(key, value);
	}

	@Override
	public void putAll(List<Entry<K, V>> entries) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(K key) {
		map.remove(key);
	}

	@Override
	public KeyValueIterator<K, V> range(K from, K to) {
		throw new UnsupportedOperationException();
	}

	@Override
	public KeyValueIterator<K, V> all() {
		return new KeyValueIteratorMock<>(map);
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flush() {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return map.size();
	}

	private static final class KeyValueIteratorMock<K, V> implements KeyValueIterator<K, V> {
		Iterator<Map.Entry<K, V>> iterator;

		public KeyValueIteratorMock(Map<K, V> map) {
			iterator = map.entrySet().iterator();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Entry<K, V> next() {
			Map.Entry<K, V> next = iterator.next();
			return new Entry<>(next.getKey(), next.getValue());
		}

		@Override
		public void close() {}
	}
}
