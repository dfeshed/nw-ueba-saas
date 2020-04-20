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
public class NegateAdeRecordReaderPredicate implements AdeRecordReaderPredicate {
    public static final String ADE_RECORD_READER_PREDICATE_TYPE = "not";

    private final AdeRecordReaderPredicate predicate;

    @JsonCreator
    public NegateAdeRecordReaderPredicate(@JsonProperty("predicate") AdeRecordReaderPredicate predicate) {
        Assert.notNull(predicate, "predicate cannot be null.");
        this.predicate = predicate;
    }

    @Override
    public boolean test(AdeRecordReader adeRecordReader) {
        return !predicate.test(adeRecordReader);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NegateAdeRecordReaderPredicate)) return false;
        NegateAdeRecordReaderPredicate that = (NegateAdeRecordReaderPredicate)o;
        return new EqualsBuilder()
                .append(predicate, that.predicate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(predicate)
                .toHashCode();
    }
}
