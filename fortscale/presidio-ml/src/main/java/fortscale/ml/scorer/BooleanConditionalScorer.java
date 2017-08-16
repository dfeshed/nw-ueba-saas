package fortscale.ml.scorer;

import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;


public class BooleanConditionalScorer extends ConditionalScorer{
    private static final Logger logger = Logger.getLogger(BooleanConditionalScorer.class);

    private String conditionalField;
    private Boolean conditionalValue;


    public BooleanConditionalScorer(String name, Scorer scorer, String conditionalField, Boolean conditionalValue) {
        super(name, scorer);
        Assert.hasText(conditionalField, "condition field should not be blank");
        Assert.notNull(conditionalValue, "condition value should not be blank");
        this.conditionalField = conditionalField;
        this.conditionalValue = conditionalValue;
    }


    @Override
    public boolean isTrue(AdeRecordReader adeRecordReader) {
        boolean result = false;
        Boolean value = adeRecordReader.get(conditionalField, Boolean.class);
        if (value != null && value.equals(conditionalValue)) {
            result = true;
        }
        return result;
    }
}
