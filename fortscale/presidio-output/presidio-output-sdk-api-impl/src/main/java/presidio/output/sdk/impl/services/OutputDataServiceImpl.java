package presidio.output.sdk.impl.services;

import fortscale.domain.core.EnrichedRecordsMetadata;
import presidio.output.sdk.api.OutputDataServiceSDK;
import presidio.output.domain.records.events.*;

import java.util.List;

/**
 * Created by efratn on 19/07/2017.
 */
public class OutputDataServiceImpl implements OutputDataServiceSDK {

    @Override
    public void store(EnrichedRecordsMetadata metaData, List<? extends EnrichedEvent> records) {
        //TODO add here the actual storing
    }
}
