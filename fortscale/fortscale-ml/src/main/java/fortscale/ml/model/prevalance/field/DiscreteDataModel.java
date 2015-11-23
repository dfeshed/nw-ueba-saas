package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.Model;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.regex.Pattern;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class DiscreteDataModel implements Model {
	private OccurrencesHistogram occurrencesHistogram;
	private Pattern ignoreValues;

	public DiscreteDataModel(Pattern ignoreValues) {
		this.ignoreValues = ignoreValues;
		occurrencesHistogram = null;
	}

	public void setFeatureCounts(List<Double> featureCounts) throws Exception {
		occurrencesHistogram = new OccurrencesHistogram(featureCounts);
	}

	@Override
	public double calculateScore(Object value) {
		Pair<Object, Double> featureAndCount = (Pair<Object, Double>) value;
		String featureValue = getFeatureValue(featureAndCount.getKey());
		if (featureValue == null) {
			return 0;
		}
		return occurrencesHistogram.score(featureAndCount.getValue());
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
