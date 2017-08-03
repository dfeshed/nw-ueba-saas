package presidio.output.sdk.impl.services;

import presidio.output.sdk.api.OutputDataServiceSDK;
import java.util.List;
import fortscale.common.general.Schema;
import presidio.output.domain.records.events.EnrichedEvent;

/**
 * Created by efratn on 19/07/2017.
 */
public class OutputDataServiceImpl implements OutputDataServiceSDK {

    @Override
    public void store(Schema schema, List<? extends EnrichedEvent> records) {
        //TODO add here the actual storing
    }
}
