package fortscale.ml.scorer;

import org.springframework.util.Assert;

public class ScoreExponentialStepsMapper extends AbstractScoreMapper {
    private ScoreExponentialStepsMapping.ScoreExponentialStepsMappingConf scoreMappingConf;

    public ScoreExponentialStepsMapper(String name, Scorer baseScorer, ScoreExponentialStepsMapping.ScoreExponentialStepsMappingConf scoreMappingConf) {
        super(name, baseScorer);
        Assert.notNull(scoreMappingConf, "Score mapping conf cannot be null.");
        this.scoreMappingConf = scoreMappingConf;
    }

    @Override
    protected double mapScore(double score){
        return ScoreExponentialStepsMapping.mapScore(score, scoreMappingConf);
    }
}
