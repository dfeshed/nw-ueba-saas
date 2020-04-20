package presidio.ade.domain.record.predicate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class BooleanAdeRecordReaderPredicate implements AdeRecordReaderPredicate {
    public static final String ADE_RECORD_READER_PREDICATE_TYPE = "boolean";

    private final String fieldName;
    private final boolean expectedValue;

    @JsonCreator
    public BooleanAdeRecordReaderPredicate(
            @JsonProperty("fieldName") String fieldName,
            @JsonProperty("expectedValue") boolean expectedValue) {

        Assert.hasText(fieldName, "fieldName cannot be blank, empty or null.");
        this.fieldName = fieldName;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean test(AdeRecordReader adeRecordReader) {
        Boolean actualValue = adeRecordReader.get(fieldName, Boolean.class);
        return actualValue != null && actualValue.equals(expectedValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BooleanAdeRecordReaderPredicate)) return false;
        BooleanAdeRecordReaderPredicate that = (BooleanAdeRecordReaderPredicate)o;
        return new EqualsBuilder()
                .append(fieldName, that.fieldName)
                .append(expectedValue, that.expectedValue)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fieldName)
                .append(expectedValue)
                .toHashCode();
    }
}
