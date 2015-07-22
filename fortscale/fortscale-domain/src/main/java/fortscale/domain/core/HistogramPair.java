package fortscale.domain.core;

import java.io.Serializable;

/**
 * represent histogram pair in front-end format: have a bean with the following structure:
 * {
 *     key: someKey
 *     count: someNumber
 * }
 * Created by galiar on 21/07/2015.
 */
public class HistogramPair implements Serializable {

	private String key;
	private Number count;

	public HistogramPair(){}

	public HistogramPair(String key, Number count){
		this.key = key;
		this.count = count;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Number getCount() {
		return count;
	}

	public void setCount(Number count) {
		this.count = count;
	}

}







