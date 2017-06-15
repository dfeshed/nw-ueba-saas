package presidio.ade.domain.store.enriched.groups;

import java.util.Set;

/**
 * Create groups for test results
 * Create set of context ids, num of events and num of pages
 */
public class EnrichedRecordPaginationServiceGroup {

    private Set<String> contextIds;
    private int numOfEvents;
    private int numOfPages;

    public Set<String> getContextIds() {
        return contextIds;
    }

    public int getNumOfEvents() {
        return numOfEvents;
    }

    public int getNumOfPages() {
        return numOfPages;
    }

    public EnrichedRecordPaginationServiceGroup(int numOfEvents, int numOfPages, Set<String> contextIds) {
        this.numOfEvents = numOfEvents;
        this.numOfPages = numOfPages;
        this.contextIds = contextIds;
    }

}
