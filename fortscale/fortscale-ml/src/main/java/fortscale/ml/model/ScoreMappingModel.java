package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.scorer.ScoreMapping;

import java.util.Map;

@JsonAutoDetect(
		fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
		setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class ScoreMappingModel implements Model {
	private ScoreMapping.ScoreMappingConf scoreMappingConf;

	public ScoreMappingModel() {
		scoreMappingConf = new ScoreMapping.ScoreMappingConf();
	}

	public void init(Map<Double, Double> mapping) {
		scoreMappingConf.setMapping(mapping);
	}

	public ScoreMapping.ScoreMappingConf getScoreMappingConf() {
		return scoreMappingConf;
	}

	@Override
	public long getNumOfSamples() {
		return 0;
	}
}
