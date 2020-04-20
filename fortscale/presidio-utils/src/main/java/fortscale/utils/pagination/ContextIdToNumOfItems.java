package fortscale.utils.pagination;

/**
 * {@link ContextIdToNumOfItems} contains the fields contextId and totalNumOfItems.
 * See EnrichedDataStoreImplMongo for an example.
 */
public class ContextIdToNumOfItems {
	public static final String CONTEXT_ID_FIELD = "contextId";
	public static final String TOTAL_NUM_OF_ITEMS_FIELD = "totalNumOfItems";

	private String contextId;
	private int totalNumOfItems;

	public ContextIdToNumOfItems(String contextId, int totalNumOfItems) {
		this.contextId = contextId;
		this.totalNumOfItems = totalNumOfItems;
	}

	public String getContextId() {
		return contextId;
	}

	public int getTotalNumOfItems() {
		return totalNumOfItems;
	}
}
