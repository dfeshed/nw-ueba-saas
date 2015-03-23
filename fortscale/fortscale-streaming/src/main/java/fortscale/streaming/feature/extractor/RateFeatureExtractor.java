package fortscale.streaming.feature.extractor;

import static fortscale.utils.ConversionUtils.*;
import net.minidev.json.JSONObject;

public class RateFeatureExtractor implements FeatureExtractor {
	
	private int durationAdditionInMin;
	private String  originalFieldName;
	private String normalizedFieldName;
	private String durationFieldName;
	
	
	public RateFeatureExtractor(){}
	
	public RateFeatureExtractor(int durationAdditionInMin, String originalFieldName, String normalizedFieldName, String durationFieldName) {
		this.durationAdditionInMin = durationAdditionInMin;
		this.originalFieldName = originalFieldName;
		this.normalizedFieldName = normalizedFieldName;
		this.durationFieldName = durationFieldName;
	}

	@Override
	public Object extract(JSONObject message) {
		Integer  originalFieldValue = convertToInteger(message.get(originalFieldName));
		Double duration = convertToDouble(message.get("duration"));
		Double normalized_count = null;
		if(duration != null && originalFieldValue != null){
			double durationForRate = duration + durationAdditionInMin/60;
			normalized_count = originalFieldValue / durationForRate;
		}
		if(normalizedFieldName != null){
			message.put(normalizedFieldName, normalized_count);
		}
		
		return normalized_count;
	}

	public int getDurationAdditionInMin() {
		return durationAdditionInMin;
	}

	public void setDurationAdditionInMin(int durationAdditionInMin) {
		this.durationAdditionInMin = durationAdditionInMin;
	}

	public String getOriginalFieldName() {
		return originalFieldName;
	}

	public void setOriginalFieldName(String originalFieldName) {
		this.originalFieldName = originalFieldName;
	}

	public String getNormalizedFieldName() {
		return normalizedFieldName;
	}

	public void setNormalizedFieldName(String normalizedFieldName) {
		this.normalizedFieldName = normalizedFieldName;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		RateFeatureExtractor that = (RateFeatureExtractor) o;

		if (durationAdditionInMin != that.durationAdditionInMin)
			return false;
		if (normalizedFieldName != null ? !normalizedFieldName.equals(that.normalizedFieldName) : that.normalizedFieldName != null)
			return false;
		if (originalFieldName != null ? !originalFieldName.equals(that.originalFieldName) : that.originalFieldName != null)
			return false;

		return true;
	}

	@Override public int hashCode() {
		int result = durationAdditionInMin;
		result = 31 * result + (originalFieldName != null ? originalFieldName.hashCode() : 0);
		result = 31 * result + (normalizedFieldName != null ? normalizedFieldName.hashCode() : 0);
		return originalFieldName.hashCode();
	}

	public String getDurationFieldName() {
		return durationFieldName;
	}

	public void setDurationFieldName(String durationFieldName) {
		this.durationFieldName = durationFieldName;
	}
	
}
