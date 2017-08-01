package presidio.output.sdk.api;

import fortscale.domain.core.AbstractPresidioDocument;
import fortscale.domain.core.EnrichedRecordsMetadata;

import java.util.List;

/**
 * Created by efratn on 19/07/2017.
 */
public interface OutputDataServiceSDK {

    /**
     * persist given records into output db
     *
     * @param metaData some metadata considering the data to be stored. i.e. what is the data source, what is the time range etc...
     * @param records  data to be stored
     */
    public void store(EnrichedRecordsMetadata metaData, List<? extends AbstractPresidioDocument> records);
}
