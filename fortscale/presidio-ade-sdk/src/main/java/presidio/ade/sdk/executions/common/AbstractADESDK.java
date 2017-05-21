package presidio.ade.sdk.executions.common;

import presidio.ade.domain.store.input.store.ADEInputDataStore;
import presidio.ade.domain.store.input.ADEInputRecord;
import presidio.ade.domain.store.input.ADEInputRecordsMetaData;
import presidio.ade.domain.store.input.ADEInputCleanupParams;

import java.util.List;

/**
 * abstract class the implements common ADE SDK functionality regardless to the execution type
 * Created by barak_schuster on 5/18/17.
 */
public abstract class AbstractADESDK<ADERunParams> implements ADECommonSDK<ADERunParams>{
    ADEInputDataStore store;

    public AbstractADESDK(ADEInputDataStore store) {
        this.store = store;
    }

    @Override
    public void store(ADEInputRecordsMetaData metaData, List<ADEInputRecord> records)
    {
        store.store(metaData, records);
    }

    @Override
    public void cleanup(ADEInputCleanupParams params)
    {
        store.cleanup(params);
    }
}
