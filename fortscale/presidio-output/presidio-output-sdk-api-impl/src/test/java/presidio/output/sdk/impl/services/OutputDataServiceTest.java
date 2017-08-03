package presidio.output.sdk.impl.services;

import fortscale.domain.core.AbstractPresidioDocument;
import fortscale.domain.core.EnrichedRecordsMetadata;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.events.FileEnrichedEvent;
import presidio.output.sdk.api.OutputDataServiceSDK;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by efratn on 02/08/2017.
 */
@RunWith(SpringRunner.class)
public class OutputDataServiceTest {

    @Autowired
    private OutputDataServiceSDK outputDataServiceSDK;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void storeEvents() {
        //creating meta data
        Instant startDate = Instant.now();
        Instant endDate = Instant.now().plus(1, ChronoUnit.HOURS);
        EnrichedRecordsMetadata metaData = new EnrichedRecordsMetadata("File", startDate, endDate);

        //create meta data
        List<? extends AbstractPresidioDocument> events = new ArrayList<FileEnrichedEvent>();

        outputDataServiceSDK.store(metaData, events);

    }

}
