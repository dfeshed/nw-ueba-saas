package fortscale.streaming.service;

import java.util.List;

import org.apache.samza.storage.kv.Entry;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.KeyValueStore;

public class NullKeyValueStore<K, V> implements KeyValueStore<K, V>{

	@Override
	public KeyValueIterator<K, V> all() {
		throw new SamzaStoreWasNotSetException();
	}

	@Override
	public void close() {
		throw new SamzaStoreWasNotSetException();
	}

	@Override
	public void delete(K arg0) {
		throw new SamzaStoreWasNotSetException();
	}

	@Override
	public void flush() {
		throw new SamzaStoreWasNotSetException();
	}

	@Override
	public V get(K arg0) {
		throw new SamzaStoreWasNotSetException();
	}

	@Override
	public void put(K arg0, V arg1) {
		throw new SamzaStoreWasNotSetException();
	}

	@Override
	public void putAll(List<Entry<K, V>> arg0) {
		throw new SamzaStoreWasNotSetException();
	}

	@Override
	public KeyValueIterator<K, V> range(K arg0, K arg1) {
		throw new SamzaStoreWasNotSetException();
	}

}
