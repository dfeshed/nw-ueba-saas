package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class LinearScoreReducerConf extends AbstractScorerConf {
	public static final String SCORER_TYPE = "linear-score-reducer";

	private IScorerConf reducedScorer;
	private double reducingWeight;

	@JsonCreator
	public LinearScoreReducerConf(
			@JsonProperty("name") String name,
			@JsonProperty("reduced-scorer") IScorerConf reducedScorer,
			@JsonProperty("reducing-weight") double reducingWeight) {

		super(name);
		Assert.notNull(reducedScorer, "Reduced scorer configuration cannot be null.");
		Assert.isTrue(0 < reducingWeight && reducingWeight < 1, "Reducing weight must be in the range (0,1).");
		this.reducedScorer = reducedScorer;
		this.reducingWeight = reducingWeight;
	}

	@Override
	public String getFactoryName() {
		return SCORER_TYPE;
	}

	public IScorerConf getReducedScorer() {
		return reducedScorer;
	}

	public double getReducingWeight() {
		return reducingWeight;
	}
}
