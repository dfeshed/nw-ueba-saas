package fortscale.utils.pagination;

/**
 * ContextIdToNumOfEvents contains contextId and totalNumOfEvents fields.
 * Example of usage: see EnrichedDataStoreImplMongo
 */
public class ContextIdToNumOfEvents {

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

