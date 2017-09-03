package presidio.output.sdk.impl.services;

import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import fortscale.utils.mongodb.util.ToCollectionNameTranslator;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.events.FileEnrichedEvent;
import presidio.output.sdk.api.OutputDataServiceSDK;
import presidio.output.sdk.impl.spring.OutputDataServiceConfig;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by efratn on 02/08/2017.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {MongodbTestConfig.class, OutputDataServiceConfig.class})
public class OutputDataServiceTest {

    @Autowired
    private OutputDataServiceSDK outputDataServiceSDK;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ToCollectionNameTranslator<Schema> toCollectionNameTranslator;

    @Before
    public void before() {
        mongoTemplate.dropCollection(toCollectionNameTranslator.toCollectionName(Schema.FILE));
    }

    @Test
    public void contextLoads() throws Exception {

        Assert.assertNotNull(outputDataServiceSDK);
        Assert.assertNotNull(mongoTemplate);
    }

    @Test
    public void testStoreEvents() {
        Instant eventDate = Instant.now();
        FileEnrichedEvent event = new FileEnrichedEvent(eventDate, eventDate, "eventId", Schema.FILE.toString(),
                "userId", "username", "userDisplayName", "dataSource", "oppType", new ArrayList<String>(),
                EventResult.FAILURE, "resultCode", new HashMap<String, String>(), "absoluteSrcFilePath", "absoluteDstFilePath",
                "absoluteSrcFolderFilePath", "absoluteDstFolderFilePath", 20L, true, true);
        List<FileEnrichedEvent> events = new ArrayList<>();
        events.add(event);

        try {
            outputDataServiceSDK.store(Schema.FILE, events);
        } catch (Exception e) {
            Assert.fail();
        }
        String collectionName = toCollectionNameTranslator.toCollectionName(Schema.FILE);
        List<FileEnrichedEvent> eventsFound = mongoTemplate.findAll(FileEnrichedEvent.class, collectionName);
        Assert.assertTrue("retrieved event number", eventsFound.size() == 1);
        Assert.assertEquals(event.getId(), eventsFound.get(0).getId());
    }

}
