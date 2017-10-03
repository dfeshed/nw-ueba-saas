package presidio.ade.domain.store.scored;

import fortscale.utils.time.TimeRange;
import org.springframework.data.util.Pair;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.util.List;

/**
 * Created by YaronDL on 6/13/2017.
 */
public interface ScoredEnrichedDataStore {

    /**
     * stores the given records
     *
     * @param records         to be stored
     */
    void store(List<? extends AdeScoredEnrichedRecord> records);

    /**
     * cleanup store by filtering params
     *
     * @param cleanupParams to build the remove query
     */
    void cleanup(AdeDataStoreCleanupParams cleanupParams);

    /**
     *
     * @param eventIds {@link EnrichedRecord#eventId}
     * @param adeEventType type of {@link AdeScoredEnrichedRecord} - symbolize the scored feature name
     * @param scoreThreshold retrieved events will have score greater then equal this param
     * @return a list of all the scored enriched records that originated from given params
     */
    List<AdeScoredEnrichedRecord> findScoredEnrichedRecords(List<String> eventIds, String adeEventType, Double scoreThreshold);

    /**
     * This method is a hack. Should be removed!!!
     * @param adeEventType type of {@link AdeScoredEnrichedRecord} - symbolize the scored feature name
     * @param contextFieldAndValue i.e. "userId","someUser"
     * @param timeRange time line filtering param
     * @param distinctFieldName field to retrieve distinct values on
     * @param scoreThreshold distinct values would be fetched only for records having score greater then this value
     * @return distinct feature values
     */
    List<String> findScoredEnrichedRecordsDistinctFeatureValues(String adeEventType, Pair<String,String> contextFieldAndValue, TimeRange timeRange,String distinctFieldName,Double scoreThreshold);
}
