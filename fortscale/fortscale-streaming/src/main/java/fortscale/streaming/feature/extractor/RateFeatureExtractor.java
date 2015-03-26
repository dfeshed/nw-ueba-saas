package fortscale.streaming.feature.extractor;

import static fortscale.utils.ConversionUtils.*;
import net.minidev.json.JSONObject;

public class RateFeatureExtractor extends MessageFeatureExtractor {
	
	private int durationAdditionInMin;
	private String durationFieldName;
	
	
	public RateFeatureExtractor(){}
	
	public RateFeatureExtractor(int durationAdditionInMin, String originalFieldName, String normalizedFieldName, String durationFieldName) {
		super(originalFieldName, normalizedFieldName);
		this.durationAdditionInMin = durationAdditionInMin;
		this.durationFieldName = durationFieldName;
	}

	@Override
	protected Object extractValue(JSONObject message) {
		Integer  originalFieldValue = convertToInteger(message.get(originalFieldName));
		Double duration = convertToDouble(message.get("duration"));
		Double normalized_count = null;
		if(duration != null && originalFieldValue != null){
			double durationForRate = duration + durationAdditionInMin/60;
			normalized_count = originalFieldValue / durationForRate;
		}
		
		return normalized_count;
	}

	public int getDurationAdditionInMin() {
		return durationAdditionInMin;
	}

	public void setDurationAdditionInMin(int durationAdditionInMin) {
		this.durationAdditionInMin = durationAdditionInMin;
	}

	public String getDurationFieldName() {
		return durationFieldName;
	}

	public void setDurationFieldName(String durationFieldName) {
		this.durationFieldName = durationFieldName;
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
	
}
