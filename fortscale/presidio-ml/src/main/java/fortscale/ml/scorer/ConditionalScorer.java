package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.predicate.AdeRecordReaderPredicate;

import java.util.Collections;
import java.util.List;

public class ConditionalScorer extends AbstractScorer {
    private final List<AdeRecordReaderPredicate> predicates;
    private final Scorer scorer;

    public ConditionalScorer(String name, List<AdeRecordReaderPredicate> predicates, Scorer scorer) {
        super(name);
        Assert.notEmpty(predicates, "predicates cannot be empty or null.");
        predicates.forEach(predicate -> Assert.notNull(predicate, "predicates cannot contain null elements."));
        Assert.notNull(scorer, "scorer cannot be null.");
        this.predicates = predicates;
        this.scorer = scorer;
    }

    @Override
    public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
        for (AdeRecordReaderPredicate predicate : predicates) {
            if (!predicate.test(adeRecordReader)) {
                return null;
            }
        }

        FeatureScore featureScore = scorer.calculateScore(adeRecordReader);
        if (featureScore == null) return null;
        return new FeatureScore(getName(), featureScore.getScore(), Collections.singletonList(featureScore));
    }
}
