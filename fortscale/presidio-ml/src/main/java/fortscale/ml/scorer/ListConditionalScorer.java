package fortscale.ml.scorer;

import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.Arrays;
import java.util.List;

public class ListConditionalScorer extends ConditionalScorer {
    private static final Logger logger = Logger.getLogger(ListConditionalScorer.class);
    public static final String CONDITIONAL_VALUE_CHAR_SPLIT = ",";

    private String conditionalField;
    private List<String> conditionalValues;


    public ListConditionalScorer(String name, Scorer scorer, String conditionalField, String conditionalValue) {
        super(name, scorer);
        Assert.hasText(conditionalField, "condition field should not be blank");
        Assert.hasText(conditionalValue, "condition value should not be blank");
        this.conditionalField = conditionalField;
        this.conditionalValues = Arrays.asList(conditionalValue.split(CONDITIONAL_VALUE_CHAR_SPLIT));
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
