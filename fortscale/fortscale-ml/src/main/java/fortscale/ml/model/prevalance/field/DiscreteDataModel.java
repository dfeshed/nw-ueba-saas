package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.Model;

import java.util.List;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class DiscreteDataModel implements Model {
	private OccurrencesHistogram occurrencesHistogram;

	public DiscreteDataModel(List<Double> featureCounts) {
		occurrencesHistogram = new OccurrencesHistogram(featureCounts);
	}

	@Override
	public double calculateScore(Object value) {
		return occurrencesHistogram.score((Double) value);
	}
}
