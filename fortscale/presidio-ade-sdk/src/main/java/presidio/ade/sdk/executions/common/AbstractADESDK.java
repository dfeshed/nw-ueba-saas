package presidio.ade.sdk.executions.common;

import presidio.ade.domain.store.input.ADEInputCleanupParams;
import presidio.ade.domain.store.input.ADEInputRecord;
import presidio.ade.domain.store.input.ADEInputRecordsMetaData;
import presidio.ade.domain.store.input.store.ADEInputDataStore;

import java.util.List;
import java.util.Set;

/**
 * abstract class the implements common ADE SDK functionality regardless to the execution type
 * Created by barak_schuster on 5/18/17.
 */
public abstract class AbstractADESDK<ADERunParams> implements ADECommonSDK<ADERunParams>{
    protected ADEInputDataStore store;

    public AbstractADESDK(ADEInputDataStore store) {
        this.store = store;
    }

    @Override
    public void store(ADEInputRecordsMetaData metaData, List<? extends ADEInputRecord> records)
    {
        store.store(metaData, records);
    }

    @Override
    public void cleanup(ADEInputCleanupParams params)
    {
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
