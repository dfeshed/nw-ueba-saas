package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureStringValue;
import fortscale.ml.model.Model;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class DiscreteDataModel implements Model {
	private OccurrencesHistogram occurrencesHistogram;
	private Map<String, Double> featureToCounterMap;

	public DiscreteDataModel(Collection<Double> featureCounts) {
		occurrencesHistogram = new OccurrencesHistogram(featureCounts);
		featureToCounterMap = new HashMap<>();
	}

	@Override
	public Double calculateScore(Object value) {
		return occurrencesHistogram.score((Double)value);
	}

	public Double getFeatureCounter(Feature feature) {
		return featureToCounterMap.get(getFeatureKey(feature));
	}

	public void setFeatureCounter(Feature feature, double counter) {
		String featureKey = getFeatureKey(feature);

		if (StringUtils.isNotBlank(featureKey) && counter >= 0) {
			featureToCounterMap.put(featureKey, counter);
		}
	}

	private String getFeatureKey(Feature feature) {
		if (feature != null && feature.getValue() instanceof FeatureStringValue) {
			return feature.getValue().toString();
		} else {
			return null;
		}
	}
}
