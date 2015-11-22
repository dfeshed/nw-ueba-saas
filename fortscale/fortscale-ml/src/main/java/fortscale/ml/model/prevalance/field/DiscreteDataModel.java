package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.Model;
import fortscale.ml.model.prevalance.calibration.FeatureCalibration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class DiscreteDataModel implements Model {
	private FeatureCalibration featureCalibration;
	private Pattern ignoreValues;

	public DiscreteDataModel(Pattern ignoreValues) {
		this.ignoreValues = ignoreValues;
		featureCalibration = new FeatureCalibration();
	}

	/**
	 * @param featureValueToCountMap a mapping from features to counters. It's assumed all features are not null.
	 */
	public void setFeatureCounts(Map<String, Double> featureValueToCountMap) throws Exception {
		featureCalibration.init(featureValueToCountMap);
	}

	@Override
	public double calculateScore(Object value) {
		Pair<Object, Double> featureAndCount = (Pair<Object, Double>) value;
		String featureValue = getFeatureValue(featureAndCount.getKey());
		if (featureValue == null) {
			return 0;
		}
		return featureCalibration.scoreFeatureCount(featureAndCount.getValue());
	}
	
	public String getFeatureValue(Object value){
		if (value == null) {
			return null;
		}
		String s = value.toString();
		if (StringUtils.isBlank(s) || (ignoreValues != null && ignoreValues.matcher(s).matches())) {
			return null;
		}
		return s;
	}
}
