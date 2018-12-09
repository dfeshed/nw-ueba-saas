package fortscale.ml.model.store;

import fortscale.ml.model.ModelConf;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.store.StoreManagerAware;

import java.time.Instant;
import java.util.*;


public interface ModelReader extends StoreManagerAware {


    Instant getLatestEndInstantLt(ModelConf modelConf, String sessionId, Instant instant);

    List<String> getContextIdsWithModels(ModelConf modelConf, String sessionId, Instant endInstant);

    Collection<ModelDAO> getAllContextsModelDaosWithLatestEndTimeLte(ModelConf modelConf, Instant eventEpochtime);

    List<ModelDAO> getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(
            ModelConf modelConf, String contextId, Instant eventTime, Instant oldestAllowedModelTime, int limit);


    /**
     * find contexts with lte eventEpochTime model and count 1 as num of events
     * @param modelConf modelConf
     * @param eventEpochTime eventEpochTime
     * @return ContextIdToNumOfItems
     */
    List<ContextIdToNumOfItems> aggregateContextToNumOfEvents(ModelConf modelConf, Instant eventEpochTime);

    /**
     * Read records
     * @param modelConf modelConf
     * @param eventEpochTime eventEpochTime
     * @param contextIds contextIds
     * @param numOfItemsToSkip numOfItemsToSkip
     * @param numOfItemsToRead numOfItemsToRead
     * @return List<ModelDAO>
     */
    List<ModelDAO> readRecords(ModelConf modelConf, Instant eventEpochTime, Set<String> contextIds,  int numOfItemsToSkip, int numOfItemsToRead);

}
