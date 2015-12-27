package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.Model;

import java.util.Collection;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class DiscreteDataModel implements Model {
	private OccurrencesHistogram occurrencesHistogram;

	public DiscreteDataModel(Collection<Double> featureCounts) {
		occurrencesHistogram = new OccurrencesHistogram(featureCounts);
	}

	@Override
	public Double calculateScore(Object value) {
		return occurrencesHistogram.score((Double)value);
	}
}
