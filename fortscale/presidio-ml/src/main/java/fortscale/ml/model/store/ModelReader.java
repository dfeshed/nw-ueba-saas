package fortscale.ml.model.store;

import fortscale.ml.model.ModelConf;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.store.StoreManagerAware;

import java.time.Instant;
import java.util.*;


public interface ModelReader {

    Instant getLatestEndInstantLt(ModelConf modelConf, String sessionId, Instant instant);

    List<String> getContextIdsWithModels(ModelConf modelConf, String sessionId, Instant endInstant);

    Collection<ModelDAO> getAllContextsModelDaosWithLatestEndTimeLte(ModelConf modelConf, Instant eventEpochtime);

    List<ModelDAO> getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(
            ModelConf modelConf, String contextId, Instant eventTime, Instant oldestAllowedModelTime, int limit);

    /**
     * Get distinct num of contextIds
     * @param modelConf modelConf
     * @param endInstant endInstant
     * @return contextIds list
     */
    List<String> getDistinctNumOfContextIds(ModelConf modelConf, Instant endInstant);

    /**
     * Read records
     *
     * @param modelConf        modelConf
     * @param eventEpochTime   eventEpochTime
     * @param contextIds       contextIds
     * @param numOfItemsToSkip numOfItemsToSkip
     * @param numOfItemsToRead numOfItemsToRead
     * @return List<ModelDAO>
     */
    List<ModelDAO> readRecords(ModelConf modelConf, Instant eventEpochTime, Set<String> contextIds, int numOfItemsToSkip, int numOfItemsToRead);

}
