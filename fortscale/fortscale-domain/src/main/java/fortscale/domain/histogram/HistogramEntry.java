package fortscale.domain.histogram;

import java.io.Serializable;
import java.util.List;

/**
 * represent histogram pair in front-end format: have a bean with the following structure:
 * {
 *     key: someKey
 *     count: someNumber
 * }
 * Created by galiar on 21/07/2015.
 */
public class HistogramEntry implements Serializable, Comparable<HistogramEntry>{



	private List<String> keys;
	private Double value;
	private boolean anomaly;


	public HistogramEntry(){}

	public HistogramEntry(List<String> keys, Double count){

		this.value = count;
		this.keys = keys;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public void setIsAnomaly(boolean isAnomaly){
		anomaly = isAnomaly;
	}

	public boolean isAnomaly(){
		return anomaly;
	}

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

	/**
	 * comapare two histogram pairs according to their value.
	 * HistogramPair a < HistogramPair b iff a.value < b.value
	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(HistogramEntry other){
		return value.compareTo(other.value);

	}

	public boolean equals(HistogramEntry other){
		return ( value.equals(other.value));

	}
}







