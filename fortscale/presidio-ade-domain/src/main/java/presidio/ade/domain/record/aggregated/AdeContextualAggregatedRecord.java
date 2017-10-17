package presidio.ade.domain.record.aggregated;

import fortscale.utils.mongodb.index.DynamicIndexing;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.AdeRecord;

import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Maria Dorohin
 */
@DynamicIndexing(compoundIndexes = {
        @CompoundIndex(name = "ctxStart", def = "{'contextId': 1, 'startInstant': 1}", unique = true)
})
public abstract class AdeContextualAggregatedRecord extends AdeRecord {
    private static final String CONTEXT_ID_SEPARATOR = "#";
    public static final String END_INSTANT_FIELD = "endInstant";
    public static final String CONTEXT_ID_FIELD = "contextId";

    @Field(END_INSTANT_FIELD)
    private Instant endInstant;
    @Field(CONTEXT_ID_FIELD)
    private String contextId;

    public AdeContextualAggregatedRecord() {
        super();
    }

    public AdeContextualAggregatedRecord(Instant startInstant, Instant endInstant, String contextId) {
        super(startInstant);
        this.endInstant = endInstant;
        this.contextId = contextId;
    }

    /**
     * create contextId. i.e. contextType#contextId
     *
     * @return String
     */
    public static String getAggregatedFeatureContextId(Map<String, String> context) {
        return context.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(entry -> StringUtils.join(entry.getKey(), CONTEXT_ID_SEPARATOR, entry.getValue()))
                .collect(Collectors.joining(CONTEXT_ID_SEPARATOR));
    }

    /**
     * @return end Instant
     */
    public Instant getEndInstant() {
        return endInstant;
    }

    /**
     * @param endInstant Set end instant
     */
    public void setEndInstant(Instant endInstant) {
        this.endInstant = endInstant;
    }

    /**
     * @return Context id. i.e. contextType#contextId
     */
    public String getContextId() {
        return contextId;
    }

    /**
     * Set Context id
     *
     * @param contextId Context id
     */
    public void setContextId(String contextId) {
        this.contextId = contextId;
    }
}
