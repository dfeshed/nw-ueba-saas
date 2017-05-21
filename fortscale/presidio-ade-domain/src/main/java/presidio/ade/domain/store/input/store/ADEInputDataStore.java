package presidio.ade.domain.store.input.store;

import presidio.ade.domain.store.input.ADEInputCleanupParams;
import presidio.ade.domain.store.input.ADEInputRecord;
import presidio.ade.domain.store.input.ADEInputRecordsMetaData;

import java.util.List;

/**
 * ADE input data CRUD operations
 * Created by barak_schuster on 5/21/17.
 */
public interface ADEInputDataStore {

    /**
     * stores the given records in mongodb
     *
     * @param recordsMetaData describing the records (which data source, etc)
     * @param records         to be stored
     */
    void store(ADEInputRecordsMetaData recordsMetaData, List<ADEInputRecord> records);

    /**
     * cleanup store by filtering params
     * @param cleanupParams to build remove query from
     */
    void cleanup(ADEInputCleanupParams cleanupParams);
}
