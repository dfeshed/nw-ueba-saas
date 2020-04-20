package fortscale.ml.scorer.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.Assert;
import presidio.ade.domain.record.predicate.AdeRecordReaderPredicate;

import java.util.List;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class ConditionalScorerConf extends AbstractScorerConf {
    public static final String SCORER_TYPE = "conditional";

    private final List<AdeRecordReaderPredicate> predicates;
    private final IScorerConf scorerConf;

    @JsonCreator
    public ConditionalScorerConf(
            @JsonProperty("name") String name,
            @JsonProperty("predicates") List<AdeRecordReaderPredicate> predicates,
            @JsonProperty("scorerConf") IScorerConf scorerConf) {

        super(name);
        Assert.notEmpty(predicates, "predicates cannot be empty or null.");
        predicates.forEach(predicate -> Assert.notNull(predicate, "predicates cannot contain null elements."));
        Assert.notNull(scorerConf, "scorerConf cannot be null.");
        this.predicates = predicates;
        this.scorerConf = scorerConf;
    }

    @Override
    public String getFactoryName() {
        return SCORER_TYPE;
    }

    public List<AdeRecordReaderPredicate> getPredicates() {
        return predicates;
    }

    public IScorerConf getScorerConf() {
        return scorerConf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConditionalScorerConf)) return false;
        ConditionalScorerConf that = (ConditionalScorerConf)o;
        return new EqualsBuilder()
                .append(getName(), that.getName())
                .append(getPredicates(), that.getPredicates())
                .append(getScorerConf(), that.getScorerConf())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getName())
                .append(getPredicates())
                .append(getScorerConf())
                .toHashCode();
    }
}
