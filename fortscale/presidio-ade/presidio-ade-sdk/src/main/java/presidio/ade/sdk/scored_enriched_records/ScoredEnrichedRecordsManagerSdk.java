package presidio.ade.sdk.scored_enriched_records;

import fortscale.utils.time.TimeRange;
import org.springframework.data.util.Pair;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.util.List;

/**
 * Created by barak_schuster on 15/08/2017.
 */
public interface ScoredEnrichedRecordsManagerSdk {

    /**
     *
     * @param eventIds {@link EnrichedRecord#eventId}
     * @param adeEventType type of {@link AdeScoredEnrichedRecord} - symbolize the scored feature name
     * @param scoreThreshold
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
    List<String> findScoredEnrichedRecordsDistinctFeatureValues(String adeEventType, Pair<String,String> contextFieldAndValue, TimeRange timeRange, String distinctFieldName, Double scoreThreshold);
}
