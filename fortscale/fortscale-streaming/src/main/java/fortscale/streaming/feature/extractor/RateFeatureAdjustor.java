package fortscale.streaming.feature.extractor;

import static fortscale.utils.ConversionUtils.convertToDouble;
import net.minidev.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonTypeName;





@JsonTypeName(RateFeatureAdjustor.RATE_FEATURE_ADJUSTOR_TYPE)
public class RateFeatureAdjustor implements FeatureAdjustor {
	protected static final String RATE_FEATURE_ADJUSTOR_TYPE = "rate_feature_adjustor";
	
	private int durationAdditionInMin;
	private String durationFieldName;
	
	
	public RateFeatureAdjustor(){}
	
	public RateFeatureAdjustor(int durationAdditionInMin, String durationFieldName) {
		this.durationAdditionInMin = durationAdditionInMin;
		this.durationFieldName = durationFieldName;
	}

	@Override
	public Object adjust(Object feature, JSONObject message) {
		Double  originalFieldValue = convertToDouble(feature);
		Double duration = convertToDouble(message.get(durationFieldName));
		Double normalized_count = null;
		if(duration != null && originalFieldValue != null){
			double durationForRate = duration + durationAdditionInMin/60.0;
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
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		RateFeatureAdjustor that = (RateFeatureAdjustor) o;

		if (durationAdditionInMin != that.durationAdditionInMin)
			return false;
		if (!durationFieldName.equals(that.durationFieldName))
			return false;

		return true;
	}
	
	@Override
	public int hashCode() {
		return durationFieldName.hashCode();
	}
}
