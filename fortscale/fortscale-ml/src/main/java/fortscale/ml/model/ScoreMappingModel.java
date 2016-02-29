package fortscale.ml.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.scorer.config.ScoreMappingConf;

import java.util.Map;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ScoreMappingModel implements Model {
	private ScoreMappingConf scoreMappingConf;

	public ScoreMappingModel() {
		scoreMappingConf = new ScoreMappingConf();
	}

	public void init(Map<Double, Double> mapping) {
		scoreMappingConf.setMapping(mapping);
	}

	public ScoreMappingConf getScoreMappingConf() {
		return scoreMappingConf;
	}

	@Override
	public long getNumOfSamples() {
		return 0;
	}
}
