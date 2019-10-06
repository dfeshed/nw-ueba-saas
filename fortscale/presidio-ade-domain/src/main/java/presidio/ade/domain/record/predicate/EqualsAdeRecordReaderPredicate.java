package presidio.ade.domain.record.predicate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.Objects;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class EqualsAdeRecordReaderPredicate implements AdeRecordReaderPredicate {
    public static final String ADE_RECORD_READER_PREDICATE_TYPE = "equals";

    private final String fieldName;
    private final Object expectedValue;
    private final boolean negate;

    @JsonCreator
    public EqualsAdeRecordReaderPredicate(
            @JsonProperty("fieldName") String fieldName,
            @JsonProperty("expectedValue") Object expectedValue,
            @JsonProperty("negate") Boolean negate) {

        this.fieldName = Validate.notBlank(fieldName, "fieldName cannot be blank, empty or null.");
        this.expectedValue = expectedValue; // expectedValue can be null.
        this.negate = negate != null && negate; // Default negate value is false.
    }

    @Override
    public boolean test(AdeRecordReader adeRecordReader) {
        Object actualValue = adeRecordReader.get(fieldName);
        return negate != Objects.equals(actualValue, expectedValue);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof EqualsAdeRecordReaderPredicate)) return false;
        EqualsAdeRecordReaderPredicate that = (EqualsAdeRecordReaderPredicate)object;
        return new EqualsBuilder()
                .append(fieldName, that.fieldName)
                .append(expectedValue, that.expectedValue)
                .append(negate, that.negate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fieldName)
                .append(expectedValue)
                .append(negate)
                .toHashCode();
    }
}
