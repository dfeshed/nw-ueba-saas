package fortscale.ml.scorer;

import fortscale.ml.scorer.config.ScoreExponentialStepsMappingConf;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.TreeMap;

public class ScoreExponentialStepsMapper extends AbstractScoreMapper {
    private ScoreExponentialStepsMappingConf scoreMappingConf;

    public ScoreExponentialStepsMapper(String name, Scorer baseScorer, ScoreExponentialStepsMappingConf scoreMappingConf) {
        super(name, baseScorer);
        Assert.notNull(scoreMappingConf, "Score mapping conf cannot be null.");
        this.scoreMappingConf = scoreMappingConf;
    }

    @Override
    protected double mapScore(double score){
        TreeMap<Double, Double> scoreToMappedScoreMap = scoreMappingConf.getScoreToMappedScoreMap();
        if (score <= scoreToMappedScoreMap.firstKey()) {
            return scoreMappingConf.getMinScore();
        }

        if (score > scoreToMappedScoreMap.lastKey()) {
            return scoreMappingConf.getMaxScore();
        }

        double fromMappedScore = scoreMappingConf.getMinScore();
        double fromScore = (1 - scoreMappingConf.getMaxProbability()) * 100;
        double toScore = fromScore;

        for (Map.Entry<Double, Double> entry : scoreToMappedScoreMap.entrySet()) {
            if (score <= entry.getKey()) {
                toScore = entry.getKey();
                break;
            }
            fromScore = entry.getKey();
            fromMappedScore = entry.getValue();
        }

        double ret = fromMappedScore + scoreMappingConf.getScoreStep() *
                (Math.pow(scoreMappingConf.getBase(), score) - Math.pow(scoreMappingConf.getBase(), fromScore)) /
                (Math.pow(scoreMappingConf.getBase(), toScore) - Math.pow(scoreMappingConf.getBase(), fromScore));


        ret = Math.floor(ret + scoreMappingConf.getScoreFactor());
        ret = ret > scoreMappingConf.getMaxScore() ? scoreMappingConf.getMaxScore() : ret;

        return ret;
    }
}
