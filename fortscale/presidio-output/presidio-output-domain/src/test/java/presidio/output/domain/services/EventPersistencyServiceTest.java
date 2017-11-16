package presidio.output.domain.services;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
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
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.domain.spring.TestConfig;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {MongodbTestConfig.class, EventPersistencyServiceConfig.class, TestConfig.class})
public class EventPersistencyServiceTest {

    @Autowired
    private EventPersistencyService eventPersistencyService;
    @Autowired
    private OutputToCollectionNameTranslator toCollectionNameTranslator;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void before() {
        mongoTemplate.dropCollection(toCollectionNameTranslator.toCollectionName(Schema.FILE));
    }

    @Test
    public void contextLoads() throws Exception {
        Assert.assertNotNull(eventPersistencyService);
    }

    @Test
    public void testSave() {
        //creating event Pojo
        Instant eventDate = Instant.now();
        FileEnrichedEvent event = new FileEnrichedEvent(eventDate, eventDate, "eventId", Schema.FILE.toString(),
                "userId", "username", "userDisplayName", "dataSource", "oppType", new ArrayList<String>(),
                EventResult.FAILURE, "resultCode", new HashMap<String, String>(), "absoluteSrcFilePath", "absoluteDstFilePath",
                "absoluteSrcFolderFilePath", "absoluteDstFolderFilePath", 20L, true, true);
        List<FileEnrichedEvent> events = new ArrayList<>();
        events.add(event);

        //store the events into mongp
        try {
            eventPersistencyService.store(Schema.FILE, events);
        } catch (Exception e) {
            Assert.fail();
        }

        //check that data was stored
        String collectionName = toCollectionNameTranslator.toCollectionName(Schema.FILE);

        List<FileEnrichedEvent> insertedRecords = mongoTemplate.findAll(FileEnrichedEvent.class, collectionName);
        Assert.assertTrue("output enriched events exists", insertedRecords.size() == 1);
        DBCollection collection = mongoTemplate.getCollection(collectionName);
        List<DBObject> indexInfo = collection.getIndexInfo();
        // 1 index is always created for _id_ field. because of that reason we need to check that are at least 2
        Assert.assertTrue("more than one index created", indexInfo.size() >= 2);

    }
}