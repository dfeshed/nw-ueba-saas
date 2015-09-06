package fortscale.web.rest.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * represent histogram pair in front-end format: have a bean with the following structure:
 * {
 *     key: someKey
 *     count: someNumber
 * }
 * Created by galiar on 21/07/2015.
 */
public class SupportingInformationEntry<T extends Comparable> implements Serializable, Comparable<SupportingInformationEntry>{

	private List<String> keys;
	private Map additionalInformation;
	private T value;
	private boolean anomaly;


	public SupportingInformationEntry(){}

	public SupportingInformationEntry(List<String> keys, T value){

		this.value = value;
		this.keys = keys;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
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
	public int compareTo(SupportingInformationEntry other){
		// sort by value, i.e. the natural order of histogram entries
		return value.compareTo(other.value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SupportingInformationEntry that = (SupportingInformationEntry) o;

		if (anomaly != that.anomaly) return false;
		if (keys != null ? !keys.equals(that.keys) : that.keys != null) return false;
		if (additionalInformation != null ? !additionalInformation.equals(that.additionalInformation) :
				that.additionalInformation != null) return false;
		return !(value != null ? !value.equals(that.value) : that.value != null);

	}

	@Override
	public int hashCode() {
		int result = keys != null ? keys.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		result = 31 * result + (additionalInformation != null ? additionalInformation.hashCode() : 0);
		result = 31 * result + (anomaly ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "SupportingInformationEntry{" +
				"keys=" + keys +
				", value=" + value +
				", anomaly=" + anomaly +
				", additionalInformation=" + additionalInformation +
				'}';
	}

	public Map getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(Map additionalInformation) {
		this.additionalInformation = additionalInformation;
	}
}







