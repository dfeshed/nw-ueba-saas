package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.Collections;
import java.util.List;

/**
 * @author Yaron DL
 * @author Lior Govrin
 */
abstract public class ConditionalScorer extends AbstractScorer {
    private Scorer scorer;

    public ConditionalScorer(String name, Scorer scorer) {
        super(name);
        Assert.notNull(scorer, "scorer cannot be null.");
        this.scorer = scorer;
    }

    @Override
    public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
        FeatureScore featureScore = null;

        if (isTrue(adeRecordReader)) {
            FeatureScore baseFeatureScore = scorer.calculateScore(adeRecordReader);

            if (baseFeatureScore != null) {
                List<FeatureScore> featureScores = Collections.singletonList(baseFeatureScore);
                featureScore = new FeatureScore(getName(), baseFeatureScore.getScore(), featureScores);
            }
        }

        return featureScore;
    }

    /**
     * Check if conditionalValue exist in conditionalField according to conditionalValue type (e.g list, boolean)
     *
     * @return boolean
     */
    public abstract boolean isTrue(AdeRecordReader adeRecordReader);
}
