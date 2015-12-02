package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.Model;

import java.util.Collection;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class DiscreteDataModel implements Model {
	public static final String MODEL_TYPE = "discrete_data_model";
	private static final long serialVersionUID = 1683497340140662427L;

	private OccurrencesHistogram occurrencesHistogram;

	public DiscreteDataModel(Collection<Double> featureCounts) {
		occurrencesHistogram = new OccurrencesHistogram(featureCounts);
	}

	@Override
	public double calculateScore(Object value) {
		return occurrencesHistogram.score((Double) value);
	}
}
