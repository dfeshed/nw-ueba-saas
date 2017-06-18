package fortscale.utils.pagination;

/**
 * ContextIdToNumOfEvents contains contextId and totalNumOfEvents fields.
 * Example of usage: see EnrichedDataStoreImplMongo
 */
public class ContextIdToNumOfEvents {

    public static String CONTEXT_ID_FIELD = "contextId";
    public static String TOTAL_NUM_OF_EVENTS_FIELD = "totalNumOfEvents";
    private String contextId;
    private int totalNumOfEvents;

    public static final String Total_Num_Of_Events = "totalNumOfEvents";

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

