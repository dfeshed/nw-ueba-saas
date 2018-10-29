package presidio.ade.domain.record.predicate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.regex.Pattern;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class RegexAdeRecordReaderPredicate implements AdeRecordReaderPredicate {
    public static final String ADE_RECORD_READER_PREDICATE_TYPE = "regex";

    private final String fieldName;
    private final Pattern pattern;

    @JsonCreator
    public RegexAdeRecordReaderPredicate(
            @JsonProperty("fieldName") String fieldName,
            @JsonProperty("pattern") String pattern) {

        Assert.hasText(fieldName, "fieldName cannot be blank, empty or null.");
        Assert.notNull(pattern, "pattern cannot be null.");
        this.fieldName = fieldName;
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public boolean test(AdeRecordReader adeRecordReader) {
        String value = adeRecordReader.get(fieldName, String.class);
        return value != null && pattern.matcher(value).matches();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegexAdeRecordReaderPredicate)) return false;
        RegexAdeRecordReaderPredicate that = (RegexAdeRecordReaderPredicate)o;
        return new EqualsBuilder()
                .append(fieldName, that.fieldName)
                .append(pattern, that.pattern)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fieldName)
                .append(pattern)
                .toHashCode();
    }
}
