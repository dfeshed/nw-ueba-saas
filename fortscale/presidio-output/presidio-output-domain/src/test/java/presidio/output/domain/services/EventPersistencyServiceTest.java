package presidio.output.domain.services;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fortscale.common.general.EventResult;
import fortscale.common.general.Schema;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.FileEnrichedEvent;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.spring.EventPersistencyServiceConfig;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {MongodbTestConfig.class, EventPersistencyServiceConfig.class})
public class EventPersistencyServiceTest {

    @Autowired
    private EventPersistencyService eventPersistencyService;
    @Autowired
    private OutputToCollectionNameTranslator toCollectionNameTranslator;
    @Autowired
    private MongoTemplate mongoTemplate;
//
//    @Before
//    public void before() {
//        esTemplate.deleteIndex(Alert.class);
//        esTemplate.createIndex(Alert.class);
//        esTemplate.putMapping(Alert.class);
//        esTemplate.refresh(Alert.class);
//    }

    @Test
    public void testSave() {
        Instant eventDate = Instant.now();
        EnrichedEvent event = new EnrichedEvent(eventDate, eventDate, "eventId", Schema.FILE.toString(),
                "userId", "username", "userDisplayName", "dataSource", "oppType", new ArrayList<>(),
                EventResult.FAILURE, "resultCode", new HashMap<>());

        List<EnrichedEvent> events = new ArrayList<>();
        events.add(event);

        //store the events into mongp
        eventPersistencyService.store(Schema.FILE, events);

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