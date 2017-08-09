package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.List;

/**
 * Created by YaronDL on 8/6/2017.
 */
public class ConditionalScorer extends AbstractScorer{
    private static final Logger logger = Logger.getLogger(ConditionalScorer.class);

    private Scorer scorer;
    private String conditionalField;
    private String conditionalValue;


    public ConditionalScorer(String name, Scorer scorer, String conditionalField, String conditionalValue) {
        super(name);
        Assert.notNull(scorer, "scorer cannot be null.");
        Assert.hasText(conditionalField, "condition field should not be blank");
        Assert.hasText(conditionalValue, "condition value should not be blank");
        this.scorer = scorer;
        this.conditionalField = conditionalField;
        this.conditionalValue = conditionalValue;
    }


    @Override
    public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
        FeatureScore featureScore = null;
        List<String> values = adeRecordReader.get(conditionalField, List.class);
        if(values != null && values.contains(conditionalValue)) {
            featureScore = scorer.calculateScore(adeRecordReader);
        }
        return featureScore;
    }
}
