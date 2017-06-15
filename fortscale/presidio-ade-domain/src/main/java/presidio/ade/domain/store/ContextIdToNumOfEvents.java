package presidio.ade.domain.store;

import presidio.ade.domain.store.enriched.EnrichedDataStoreImplMongo;

/**
 * ContextIdToNumOfEvents contains contextId and totalNumOfEvents fields.
 * Example of usage: see {@link EnrichedDataStoreImplMongo}
 */
public class ContextIdToNumOfEvents {

    public static String CONTEXT_ID_FIELD = "contextId";
    public static String TOTAL_NUM_OF_EVENTS_FIELD = "totalNumOfEvents";
    private String contextId;
    private int totalNumOfEvents;

    public ContextIdToNumOfEvents(String contextId, int totalNumOfEvents) {
        this.contextId = contextId;
        this.totalNumOfEvents = totalNumOfEvents;
    }

    public int getTotalNumOfEvents() {
        return totalNumOfEvents;
    }

    public String getContextId() {
        return contextId;
    }

}

