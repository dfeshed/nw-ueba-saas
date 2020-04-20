package fortscale.ml.scorer;

import fortscale.domain.feature.score.CertaintyFeatureScore;
import fortscale.domain.feature.score.FeatureScore;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.Collections;

public abstract class AbstractScoreMapper extends AbstractScorer {
    private Scorer baseScorer;

    public AbstractScoreMapper(String name, Scorer baseScorer) {
        super(name);
        Assert.notNull(baseScorer, "Base scorer cannot be null.");
        this.baseScorer = baseScorer;
    }

    protected abstract double mapScore(double score);

    @Override
    public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
        FeatureScore baseScore = baseScorer.calculateScore(adeRecordReader);
        double mappedScore = mapScore(baseScore.getScore());
        FeatureScore featureScore = new FeatureScore(getName(), mappedScore, Collections.singletonList(baseScore));

        if(baseScore instanceof CertaintyFeatureScore){
            double certainty = baseScore.getCertainty();
            featureScore.setScore(featureScore.getScore() * certainty);
        }

        return featureScore;
    }
}
