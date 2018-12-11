package fortscale.ml.model.pagination;

import fortscale.ml.model.ModelConf;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelReader;
import fortscale.utils.data.Pair;
import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.BasePaginationService;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.pagination.PageIterator;

import java.time.Instant;
import java.util.*;

/**
 * Used by "JoinPartitionsHistogramModelsRetriever" to retrieve pageIterators of latest endTime model for each contextId.
 */
public class PriorModelPaginationService extends BasePaginationService<ModelDAO> {

    private static final Logger logger = Logger.getLogger(PriorModelPaginationService.class);
    protected ModelReader reader;

    public PriorModelPaginationService(ModelReader reader, int pageSize, int maxGroupSize) {
        super(pageSize, maxGroupSize);
        this.reader = reader;
    }


    /**
     * Create pageIterators:
     * Get map of contextId and number of records for each contextId.
     * Get groups by the map, the group list contains pairs of records amount and contextIds.
     * Create pageIterator for each group.
     *
     * @param modelConf
     * @param eventEpochTime
     * @return list of PageIterators
     */
    public List<PageIterator<ModelDAO>> getPageIterators(ModelConf modelConf, Instant eventEpochTime) {

        List<ContextIdToNumOfItems> contextIdToNumOfItemsList = getContextIdToNumOfItemsList(modelConf, eventEpochTime);
        //groups is a list, where each group contains pair of total num of events and set of contextId.
        List<Pair<Integer, Set<String>>> groups = getGroups(contextIdToNumOfItemsList);
        List<PageIterator<ModelDAO>> pageIteratorList = new ArrayList<>(groups.size());

        //create pageIterator of each group
        for (Pair<Integer, Set<String>> group : groups) {
            Set<String> contextIds = group.getValue();
            int totalNumOfItems = group.getKey();
            PageIterator<ModelDAO> pageIterator = createPageIterator(modelConf, eventEpochTime, contextIds, totalNumOfItems);
            pageIteratorList.add(pageIterator);
        }

        logger.debug("Num of page iterators is: {}", pageIteratorList.size());
        return pageIteratorList;
    }


    /**
     * Get distinct contextIds and set 1 as num of items as a result that the
     * JoinPartitionsHistogramModelsRetriever retrieve latest model for each contextId.
     * @param modelConf      modelConf
     * @param eventEpochTime eventEpochtime
     * @return List<ContextIdToNumOfItems>
     */
    private List<ContextIdToNumOfItems> getContextIdToNumOfItemsList(ModelConf modelConf, Instant eventEpochTime) {
        List<String> contextIds = this.reader.getDistinctNumOfContextIds(modelConf, eventEpochTime);
        List<ContextIdToNumOfItems> contextIdToNumOfItems = new ArrayList<>();
        contextIds.forEach(contextId -> contextIdToNumOfItems.add(new ContextIdToNumOfItems(contextId, 1)));
        return contextIdToNumOfItems;
    }


    /**
     * @param modelConf
     * @param eventEpochTime
     * @param contextIds
     * @param totalNumOfItems
     * @return PageIterator
     */
    private PageIterator<ModelDAO> createPageIterator(ModelConf modelConf, Instant eventEpochTime, Set<String> contextIds, int totalNumOfItems) {
        int totalAmountOfPages = (int) Math.ceil((double) totalNumOfItems / this.getPageSize());
        logger.debug("Num of pages is: {}", totalAmountOfPages);
        return new PriorModelPageIterator(modelConf, eventEpochTime, contextIds, this.getPageSize(), totalAmountOfPages, this.reader);
    }


}
