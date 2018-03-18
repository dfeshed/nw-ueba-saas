package fortscale.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Created by idanp on 2/3/2015.
 * This class will implement Pair <K,V> that can be serialize by Jackson
 */
public class JksonSerilaizablePair<K,V> implements Map.Entry<K, V> {

	private K key;
	private V value;

	public JksonSerilaizablePair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public JksonSerilaizablePair() {

	}

	@Override
	@JsonIgnoreProperties
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		V old = this.value;
		this.value = value;
		return old;
	}


}
