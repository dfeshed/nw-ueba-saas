package presidio.ade.domain.store;

import fortscale.common.feature.MultiKeyFeature;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.AdeScoredRecord;

import java.util.List;

/**
 * An API to read {@link AdeScoredRecord}s from the database.
 *
 * @param <T> The type of {@link AdeScoredRecord}s (e.g. scored enriched records, or scored feature aggregation
 *            records).
 */
public interface ScoredDataReader<T extends AdeScoredRecord> {
    /**
     * Count the number of records from the given time range, with the given context and of the given ADE event type.
     *
     * @param timeRange                  The time range to query.
     * @param contextFieldNameToValueMap The context to query (e.g. userId = Bob, machineId = BOB-PC1).
     * @param adeEventType               The specific type of T to query (e.g. scored source machine ID, or scored
     *                                   number of distinct source machine IDs).
     * @return The number of records in the database that answer the query.
     */
    long countScoredRecords(TimeRange timeRange, MultiKeyFeature contextFieldNameToValueMap, String adeEventType);

    /**
     * Read the records from the given time range, with the given context and of the given ADE event type.
     *
     * @param timeRange                  The time range to query.
     * @param contextFieldNameToValueMap The context to query (e.g. userId = Bob, machineId = BOB-PC1).
     * @param adeEventType               The specific type of T to query (e.g. scored source machine ID, or scored
     *                                   number of distinct source machine IDs).
     * @param skip                       The number of records to skip.
     * @param limit                      The maximum number of records to read.
     * @return The records in the database that answer the query.
     */
    List<T> readScoredRecords(TimeRange timeRange, MultiKeyFeature contextFieldNameToValueMap, String adeEventType,
                              int skip, int limit);
}
