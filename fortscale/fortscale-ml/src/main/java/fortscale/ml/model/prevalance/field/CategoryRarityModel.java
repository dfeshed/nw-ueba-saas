package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.Model;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class CategoryRarityModel implements Model {
	public static final String MODEL_TYPE = "category_rarity_model";
	private static final long serialVersionUID = 1683497340140662427L;

	private RarityScorer occurrencesHistogram;

	public CategoryRarityModel(int maxRareCount, int maxNumOfRareFeatures, Map<Integer, Double> occurrencesToNumOfFeatures) {
		occurrencesHistogram = new RarityScorer(maxRareCount, maxNumOfRareFeatures, occurrencesToNumOfFeatures);
	}

	@Override
	public double calculateScore(Object value) {
		return occurrencesHistogram.score((Integer) value);
	}
}
