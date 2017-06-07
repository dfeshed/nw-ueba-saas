package presidio.ade.sdk.executions.common;

import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedDataStoreCleanupParams;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;

import java.util.List;
import java.util.Set;

/**
 * abstract class that implements common ADE SDK functionality regardless to the execution type
 * Created by barak_schuster on 5/18/17.
 */
public abstract class AbstractADESDK<ADERunParams> implements ADECommonSDK<ADERunParams> {
    protected EnrichedDataStore store;

    public AbstractADESDK(EnrichedDataStore store) {
        this.store = store;
    }

    @Override
    public void store(EnrichedRecordsMetadata metaData, List<? extends EnrichedRecord> records) {
        store.store(metaData, records);
    }

    @Override
    public void cleanup(EnrichedDataStoreCleanupParams params) {
        store.cleanup(params);
    }

    @Override
    public void processNextTimeRange(RunId runId) {

    }

    @Override
    public RunStatus getRunStatus(RunId runId) {
        return null;
    }

    @Override
    public void pause(RunId runId) {

    }

    @Override
    public void unpause(RunId runId) {

    }

    @Override
    public void stop(RunId runId) {

    }

    @Override
    public void stopForcefully(RunId runId) {

    }

    @Override
    public Set<DirtyDataMarker> getDirtyDataMarkers() {
        return null;
    }

    @Override
    public void setDirtyDataMarkers(Set<DirtyDataMarker> dirtyDataMarkers) {

    }
}
