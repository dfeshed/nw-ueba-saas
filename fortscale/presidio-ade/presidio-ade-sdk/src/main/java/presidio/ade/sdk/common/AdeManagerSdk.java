package presidio.ade.sdk.common;

import fortscale.common.SDK.DataPipelineSDK;
import presidio.ade.sdk.aggregation_records.AggregationRecordsManagerSdk;
import presidio.ade.sdk.cleanup.CleanupManagerSdk;
import presidio.ade.sdk.enriched_records.EnrichedRecordsManagerSdk;
import presidio.ade.sdk.historical_runs.HistoricalRunsManagerSdk;
import presidio.ade.sdk.online_run.OnlineRunManagerSdk;
import presidio.ade.sdk.scored_enriched_records.ScoredEnrichedRecordsManagerSdk;
import presidio.ade.sdk.smart_records.SmartRecordsManagerSdk;
import presidio.ade.sdk.ttl.TtlServiceSdk;

import java.util.Set;

/**
 * Combines the SDK signatures from all ADE managers.
 * Adds APIs that are not run-type nor module specific.
 *
 * @author Barak Schuster
 * @author Lior Govrin
 */
public interface AdeManagerSdk extends
        HistoricalRunsManagerSdk,
        OnlineRunManagerSdk,
        CleanupManagerSdk,
        EnrichedRecordsManagerSdk,
        ScoredEnrichedRecordsManagerSdk,
        AggregationRecordsManagerSdk,
        SmartRecordsManagerSdk,
        TtlServiceSdk
{

    /**
     * @return The data characteristics that mark records as dirty.
     *         Dirty means that the data is not part of the models.
     *         The ADE will not learn from it, because it's corrupted.
     */
    Set<DirtyDataMarker> getDirtyDataMarkers();

    /**
     * @see #getDirtyDataMarkers().
     */
    void setDirtyDataMarkers(Set<DirtyDataMarker> dirtyDataMarkers);
}
