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
public class OrAdeRecordReaderPredicate implements AdeRecordReaderPredicate {
    public static final String ADE_RECORD_READER_PREDICATE_TYPE = "or";

    private final List<AdeRecordReaderPredicate> predicates;

    @JsonCreator
    public OrAdeRecordReaderPredicate(@JsonProperty("predicates") List<AdeRecordReaderPredicate> predicates) {
        Assert.notEmpty(predicates, "predicates cannot be empty or null.");
        predicates.forEach(predicate -> Assert.notNull(predicate, "predicates cannot contain null elements."));
        this.predicates = predicates;
    }

    @Override
    public boolean test(AdeRecordReader adeRecordReader) {
        for (AdeRecordReaderPredicate predicate : predicates) {
            if (predicate.test(adeRecordReader)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrAdeRecordReaderPredicate)) return false;
        OrAdeRecordReaderPredicate that = (OrAdeRecordReaderPredicate)o;
        return new EqualsBuilder()
                .append(predicates, that.predicates)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(predicates)
                .toHashCode();
    }
}
