package presidio.ade.domain.record.predicate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.List;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class ContainedInListAdeRecordReaderPredicate implements AdeRecordReaderPredicate {
    public static final String ADE_RECORD_READER_PREDICATE_TYPE = "contained-in-list";

    private final String fieldName;
    private final List<String> expectedValues;
    private final Operator operator;

    @JsonCreator
    public ContainedInListAdeRecordReaderPredicate(
            @JsonProperty("fieldName") String fieldName,
            @JsonProperty("expectedValues") List<String> expectedValues,
            @JsonProperty("operator") String operator) {

        Assert.hasText(fieldName, "fieldName cannot be blank, empty or null.");
        Assert.notEmpty(expectedValues, "expectedValues cannot be empty or null.");
        expectedValues.forEach(expectedValue -> Assert.hasText(expectedValue, "expectedValues cannot contain blank, empty or null elements."));
        this.fieldName = fieldName;
        this.expectedValues = expectedValues;
        this.operator = toOperator(operator);
    }

    @Override
    public boolean test(AdeRecordReader adeRecordReader) {
        @SuppressWarnings("unchecked")
        List<String> actualValues = adeRecordReader.get(fieldName, List.class);
        if (actualValues == null || actualValues.isEmpty()) return false;

        if (operator.equals(Operator.AND)) {
            return actualValues.containsAll(expectedValues);
        } else {
            return expectedValues.stream().anyMatch(actualValues::contains);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContainedInListAdeRecordReaderPredicate)) return false;
        ContainedInListAdeRecordReaderPredicate that = (ContainedInListAdeRecordReaderPredicate)o;
        return new EqualsBuilder()
                .append(fieldName, that.fieldName)
                .append(expectedValues, that.expectedValues)
                .append(operator, that.operator)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fieldName)
                .append(expectedValues)
                .append(operator)
                .toHashCode();
    }

    private static Operator toOperator(String operator) {
        // Default value is AND.
        if (operator == null) return Operator.AND;
        if (operator.equals("&") || operator.equals("&&") || operator.equalsIgnoreCase("and")) return Operator.AND;
        if (operator.equals("|") || operator.equals("||") || operator.equalsIgnoreCase("or")) return Operator.OR;
        throw new IllegalArgumentException(String.format("Unknown operator %s.", operator));
    }

    private enum Operator {
        AND,
        OR
    }
}
