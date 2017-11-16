package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.domain.feature.score.CertaintyFeatureScore;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.Collections;

public class ScoreMapper extends AbstractScoreMapper {
	private ScoreMapping.ScoreMappingConf scoreMappingConf;

	public ScoreMapper(String name, Scorer baseScorer, ScoreMapping.ScoreMappingConf scoreMappingConf) {
		super(name, baseScorer);
		Assert.notNull(scoreMappingConf, "Score mapping conf cannot be null.");
		this.scoreMappingConf = scoreMappingConf;
	}

	@Override
	protected double mapScore(double score){
		return ScoreMapping.mapScore(score, scoreMappingConf);
	}
}
