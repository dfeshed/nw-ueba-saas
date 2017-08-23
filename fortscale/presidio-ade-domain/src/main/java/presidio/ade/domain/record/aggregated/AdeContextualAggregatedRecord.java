package presidio.ade.domain.record.aggregated;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;
import presidio.ade.domain.record.AdeRecord;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by maria_dorohin on 8/1/17.
 */
public abstract class AdeContextualAggregatedRecord extends AdeRecord {
    private static final String CONTEXT_ID_SEPARATOR = "#";
    public static final String END_INSTANT_FIELD = "endInstant";
    public static final String CONTEXT_ID_FIELD = "contextId";

    @Indexed
    @Field(END_INSTANT_FIELD)
    private Instant endInstant;
    @Indexed
    @Field(CONTEXT_ID_FIELD)
    private String contextId;

    public AdeContextualAggregatedRecord(){
        super();
    }

    public AdeContextualAggregatedRecord(Instant startInstant, Instant endInstant, String contextId){
        super(startInstant);
        this.endInstant = endInstant;
        this.contextId = contextId;
    }

    /**
     * create contextId. i.e. contextType#contextId
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
     * @param contextId
     */
    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

}
