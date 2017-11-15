package fortscale.ml.scorer;

import org.springframework.util.Assert;

public class ScoreExponentialMapper extends AbstractScoreMapper {
    private ScoreExponentialMapping.ScoreExponentialMappingConf scoreMappingConf;

    public ScoreExponentialMapper(String name, Scorer baseScorer, ScoreExponentialMapping.ScoreExponentialMappingConf scoreMappingConf) {
        super(name, baseScorer);
        Assert.notNull(scoreMappingConf, "Score mapping conf cannot be null.");
        this.scoreMappingConf = scoreMappingConf;
    }

    @Override
    protected double mapScore(double score){
        return ScoreExponentialMapping.mapScore(score, scoreMappingConf);
    }
}
