package fortscale.entity.event.translator;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by barak_schuster on 10/9/16.
 */
public class EntityEventTranslationService {
    private static final String COLLECTION_NAME_PREFIX = "scored_";
    private static final String COLLECTION_NAME_SEPARATOR = "__";
    private final String eventTypeFieldValue;

    public EntityEventTranslationService(String eventTypeFieldValue)
    {
        this.eventTypeFieldValue=eventTypeFieldValue;
    }

    public String toCollectionName(String entityEventType) {
        return StringUtils.join(
                COLLECTION_NAME_PREFIX, COLLECTION_NAME_SEPARATOR,
                eventTypeFieldValue, COLLECTION_NAME_SEPARATOR,
                entityEventType);
    }

    public String getEventTypeFieldValue() {
        return eventTypeFieldValue;
    }
}
