package fortscale.ml.model.prevalance.field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.Model;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class CategoryRarityModel implements Model {
	private RarityScorer rarityScorer;

	public CategoryRarityModel(int minEvents, int maxRareCount, int maxNumOfRareFeatures, Map<Integer, Double> occurrencesToNumOfFeatures) {
		rarityScorer = new RarityScorer(minEvents, maxRareCount, maxNumOfRareFeatures, occurrencesToNumOfFeatures);
	}

	@Override
	public Double calculateScore(Object value) {
		return rarityScorer.score((Integer) value);
	}
}
