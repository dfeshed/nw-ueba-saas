package presidio.ade.domain.store;

/**
 * ContextIdToNumOfEvents contains contextId and totalNumOfEvents.
 * Example of class usage:
 * Output class of mongoTemplate.aggregate.
 * It creates list of ContextIdToNumOfEvents.
 */
public class ContextIdToNumOfEvents {

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

