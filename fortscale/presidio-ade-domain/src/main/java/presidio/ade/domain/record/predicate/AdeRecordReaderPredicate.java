package presidio.ade.domain.record.predicate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.function.Predicate;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BooleanAdeRecordReaderPredicate.class, name = BooleanAdeRecordReaderPredicate.ADE_RECORD_READER_PREDICATE_TYPE),
        @JsonSubTypes.Type(value = ContainedInListAdeRecordReaderPredicate.class, name = ContainedInListAdeRecordReaderPredicate.ADE_RECORD_READER_PREDICATE_TYPE),
        @JsonSubTypes.Type(value = RegexAdeRecordReaderPredicate.class, name = RegexAdeRecordReaderPredicate.ADE_RECORD_READER_PREDICATE_TYPE),
        @JsonSubTypes.Type(value = OrAdeRecordReaderPredicate.class, name = OrAdeRecordReaderPredicate.ADE_RECORD_READER_PREDICATE_TYPE)
})
public interface AdeRecordReaderPredicate extends Predicate<AdeRecordReader> {
}
