package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import presidio.ade.domain.record.AdeRecordReader;


public class ConstantScorer extends AbstractScorer {
    private double constantScore;

    public ConstantScorer(String name, double constantScore) {
        super(name);
        this.constantScore = constantScore;
    }

    @Override
    public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
        return new FeatureScore(getName(), constantScore);
    }
}