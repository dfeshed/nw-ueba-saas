package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.List;

/**
 * Created by YaronDL on 8/6/2017.
 */
abstract public class ConditionalScorer extends AbstractScorer{
    private static final Logger logger = Logger.getLogger(ConditionalScorer.class);

    private Scorer scorer;


    public ConditionalScorer(String name, Scorer scorer) {
        super(name);
        Assert.notNull(scorer, "scorer cannot be null.");
        this.scorer = scorer;
    }

    @Override
    public FeatureScore calculateScore(AdeRecordReader adeRecordReader){
        FeatureScore featureScore = null;
        if(isTrue(adeRecordReader)) {
            featureScore = scorer.calculateScore(adeRecordReader);
        }
        return featureScore;
    }

    /**
     * Check if conditionalValue exist in conditionalField according to conditionalValue type (e.g list, boolean)
      * @param adeRecordReader
     * @return boolean
     */
    public abstract boolean isTrue(AdeRecordReader adeRecordReader);

}
