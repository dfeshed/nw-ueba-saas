package fortscale.ml.scorer;

import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

public class StringConditionalScorer extends ConditionalScorer{
    private static final Logger logger = Logger.getLogger(StringConditionalScorer.class);

    private String conditionalField;
    private String conditionalValue;


    public StringConditionalScorer(String name, Scorer scorer, String conditionalField, String conditionalValue) {
        super(name, scorer);
        Assert.hasText(conditionalField, "condition field should not be blank");
        Assert.hasText(conditionalValue, "condition value should not be blank");
        this.conditionalField = conditionalField;
        this.conditionalValue = conditionalValue;
    }


    @Override
    public boolean isTrue(AdeRecordReader adeRecordReader) {
        boolean result = false;
        String value = adeRecordReader.get(conditionalField, String.class);
        if (value != null && value.equals(conditionalValue)) {
            result = true;
        }
        return result;
    }
}
