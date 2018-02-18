package fortscale.ml.scorer;

import fortscale.ml.scorer.config.ScoreExponentialStepsMappingConf;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.TreeMap;

//todo: add description
public class ScoreExponentialStepsMapper extends AbstractScoreMapper {
    private ScoreExponentialStepsMappingConf scoreMappingConf;

    public ScoreExponentialStepsMapper(String name, Scorer baseScorer, ScoreExponentialStepsMappingConf scoreMappingConf) {
        super(name, baseScorer);
        Assert.notNull(scoreMappingConf, "Score mapping conf cannot be null.");
        this.scoreMappingConf = scoreMappingConf;
    }

    @Override
    protected double mapScore(double score){
        double mappedScoreStep = scoreMappingConf.MAX_MAPPED_SCORE_DEFAULT / scoreMappingConf.getAmountOfSteps();
        double numOfSteps = Math.log(scoreMappingConf.getProbabilityStartingPoint()/(1-(score/100))) / Math.log(scoreMappingConf.getProbabilityExponentialStep());
        if(numOfSteps < 0){
            return scoreMappingConf.MIN_MAPPED_SCORE_DEFAULT;
        }
        if(numOfSteps > scoreMappingConf.getAmountOfSteps()){
            return scoreMappingConf.MAX_MAPPED_SCORE_DEFAULT;
        }

        double fromMappedScore = scoreMappingConf.MIN_MAPPED_SCORE_DEFAULT + mappedScoreStep * Math.floor(numOfSteps);
        double fromScore = (1 - scoreMappingConf.getProbabilityStartingPoint()/Math.pow(scoreMappingConf.getProbabilityExponentialStep(),Math.floor(numOfSteps))) * 100;
        double toScore =(1 - scoreMappingConf.getProbabilityStartingPoint()/Math.pow(scoreMappingConf.getProbabilityExponentialStep(),Math.ceil(numOfSteps))) * 100;


        double ret = fromMappedScore + mappedScoreStep *
                (Math.pow(scoreMappingConf.getProbabilityExponentialStep(), score) - Math.pow(scoreMappingConf.getProbabilityExponentialStep(), fromScore)) /
                (Math.pow(scoreMappingConf.getProbabilityExponentialStep(), toScore) - Math.pow(scoreMappingConf.getProbabilityExponentialStep(), fromScore));

        return ret;
    }
}
