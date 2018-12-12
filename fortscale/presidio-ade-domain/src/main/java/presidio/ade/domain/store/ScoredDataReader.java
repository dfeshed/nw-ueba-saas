package presidio.ade.domain.store;

import fortscale.common.feature.MultiKeyFeature;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeScoredRecord;

import java.time.Instant;
import java.util.List;

/**
 * An API to read {@link AdeScoredRecord}s from the database.
 *
 * @param <T> The type of {@link AdeScoredRecord}s
 *            (e.g. scored enriched records, or scored feature aggregation records).
 */
public interface ScoredDataReader<T extends AdeRecord & AdeScoredRecord> {
    /**
     * Count the number of records from the given time range, with the given context and of the given ADE event type.
     *
     * @param timeRange                  The time range to query.
     * @param contextFieldNameToValueMap The context to query (e.g. userId = Bob, machineId = BOB-PC1).
     * @param scoreThreshold             Include only records with a score greater than this threshold.
     * @param adeEventType               The specific type of T to query (e.g. scored source machine ID,
     *                                   or scored number of distinct source machine IDs).
     * @return The number of records in the database that answer the query.
     */
    long countScoredRecords(
            TimeRange timeRange,
            MultiKeyFeature contextFieldNameToValueMap,
            int scoreThreshold,
            String adeEventType);

    /**
     * Read the records from the given time range, with the given context and of the given ADE event type.
     *
     * @param timeRange                  The time range to query.
     * @param contextFieldNameToValueMap The context to query (e.g. userId = Bob, machineId = BOB-PC1).
     * @param scoreThreshold             Include only records with a score greater than this threshold.
     * @param adeEventType               The specific type of T to query (e.g. scored source machine ID,
     *                                   or scored number of distinct source machine IDs).
     * @param skip                       The number of records to skip.
     * @param limit                      The maximum number of records to read.
     * @return The records in the database that answer the query.
     */
    List<T> readScoredRecords(
            TimeRange timeRange,
            MultiKeyFeature contextFieldNameToValueMap,
            int scoreThreshold,
            String adeEventType,
            int skip,
            int limit);

    /**
     * Read the start instant of the first record from the given time range,
     * with the given context and of the given ADE event type.
     *
     * @param timeRange                     The time range to query.
     * @param adeEventType                  The specific type of T to query (e.g. scored source machine ID,
     *                                      or scored number of distinct source machine IDs).
     * @param contextFieldNameToValueMap    The context to query (e.g. userId = Bob, machineId = BOB-PC1).
     * @param additionalFieldNameToValueMap Additional fields to query (e.g. operationType = FILE_OPENED).
     * @param scoreThreshold                Include only records with a score greater than this threshold.
     * @return The start instant of the first record that answers the query.
     */
    Instant readFirstStartInstant(
            TimeRange timeRange,
            String adeEventType,
            MultiKeyFeature contextFieldNameToValueMap,
            MultiKeyFeature additionalFieldNameToValueMap,
            int scoreThreshold);

    /**
     * Read the start instant of the last record from the given time range,
     * with the given context and of the given ADE event type.
     *
     * @param timeRange                     The time range to query.
     * @param adeEventType                  The specific type of T to query (e.g. scored source machine ID,
     *                                      or scored number of distinct source machine IDs).
     * @param contextFieldNameToValueMap    The context to query (e.g. userId = Bob, machineId = BOB-PC1).
     * @param additionalFieldNameToValueMap Additional fields to query (e.g. operationType = FILE_OPENED).
     * @param scoreThreshold                Include only records with a score greater than this threshold.
     * @return The start instant of the last record that answers the query.
     */
    Instant readLastStartInstant(
            TimeRange timeRange,
            String adeEventType,
            MultiKeyFeature contextFieldNameToValueMap,
            MultiKeyFeature additionalFieldNameToValueMap,
            int scoreThreshold);
}
