package fortscale.ml.scorer;

import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.List;

public class ListConditionalScorer extends ConditionalScorer {
    private static final Logger logger = Logger.getLogger(ListConditionalScorer.class);

    private String conditionalField;
    private List<String> conditionalValues;


    public ListConditionalScorer(String name, Scorer scorer, String conditionalField, List<String> conditionalValue) {
        super(name, scorer);
        Assert.hasText(conditionalField, "condition field should not be blank");
        Assert.notEmpty(conditionalValue, "condition value should not be empty");
        conditionalValue.forEach(v-> Assert.hasText(v,"condition value should not be blank"));
        this.conditionalField = conditionalField;
        this.conditionalValues = conditionalValue;
    }


    @Override
    public boolean isTrue(AdeRecordReader adeRecordReader) {
        List<String> values = adeRecordReader.get(conditionalField, List.class);
        if(values == null || values.isEmpty()) {
            return false;
        }

        return values.containsAll(conditionalValues);
    }
}
