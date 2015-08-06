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
public class HistogramPair implements Serializable, Comparable<HistogramPair>{

	private HistogramKey key;
	private Double value;
	private boolean is_anomaly;

	public HistogramPair(){}

	public HistogramPair(HistogramKey key, Double count){
		this.key = key;
		this.value = count;
	}

	public HistogramKey getKey() {
		return key;
	}

	public void setKey(HistogramKey key) {
		this.key = key;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public void setIsAnomaly(boolean isAnomaly){
		is_anomaly = isAnomaly;
	}

	public boolean isAnomaly(){
		return is_anomaly;
	}

	/**
	 * comapare two histogram pairs according to their value.
	 * HistogramPair a < HistogramPair b iff a.value < b.value
	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(HistogramPair other){
		return value.compareTo(other.value);

	}

	public boolean equals(HistogramPair other){
		return (key.equals(other.key) && value.equals(other.value));

	}
}







