package fortscale.ml.scorer;

import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.List;

public class ListConditionalScorer extends ConditionalScorer {
    private static final Logger logger = Logger.getLogger(ListConditionalScorer.class);

    private String conditionalField;
    private String conditionalValue;


    public ListConditionalScorer(String name, Scorer scorer, String conditionalField, String conditionalValue) {
        super(name, scorer);
        Assert.hasText(conditionalField, "condition field should not be blank");
        Assert.hasText(conditionalValue, "condition value should not be blank");
        this.conditionalField = conditionalField;
        this.conditionalValue = conditionalValue;
    }


    @Override
    public boolean isTrue(AdeRecordReader adeRecordReader) {
        boolean result = false;
        List<String> values = adeRecordReader.get(conditionalField, List.class);
        if (values != null && values.contains(conditionalValue)) {
            result = true;
        }
        return result;
    }
}
