package presidio.output.manager.services;

import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.FileEnrichedEvent;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;
import presidio.output.manager.spring.OutputManagerTestConfig;

import java.sql.Array;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static java.time.Instant.now;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {OutputManagerTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OutputManagerServiceTest {
    private static final String ENTITY_ID_TEST_ENTITY = "entityId#testEntity";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private OutputManagerService outputManagerService;

    @Before
    public void setup() {
        String outputFileEnrichedEventCollectionName = new OutputToCollectionNameTranslator().toCollectionName(Schema.FILE);

        mongoTemplate.dropCollection(outputFileEnrichedEventCollectionName);

        FileEnrichedEvent event = new FileEnrichedEvent(Instant.now(), Instant.now(), "eventId", Schema.FILE.toString(),
                ENTITY_ID_TEST_ENTITY, "username", "userDisplayName", "dataSource", "oppType", new ArrayList<>(),
                EventResult.FAILURE, "resultCode", new HashMap<>(), "absoluteSrcFilePath", "absoluteDstFilePath",
                "absoluteSrcFolderFilePath", "absoluteDstFolderFilePath", 20L, true, true);
        FileEnrichedEvent event2 = new FileEnrichedEvent(Instant.now().minus(Duration.ofDays(5)), Instant.now().minus(Duration.ofDays(3)), "eventId", Schema.FILE.toString(),
                ENTITY_ID_TEST_ENTITY, "username", "userDisplayName", "dataSource", "oppType", new ArrayList<>(),
                EventResult.FAILURE, "resultCode", new HashMap<>(), "absoluteSrcFilePath", "absoluteDstFilePath",
                "absoluteSrcFolderFilePath", "absoluteDstFolderFilePath", 20L, true, true);


        mongoTemplate.insert(event, outputFileEnrichedEventCollectionName);
        mongoTemplate.insert(event2, outputFileEnrichedEventCollectionName);
    }

    @Test
    public void testCleanDocuments() {
        try {
            String outputFileEnrichedEventCollectionName = new OutputToCollectionNameTranslator().toCollectionName(Schema.FILE);
            Assert.assertEquals(2, mongoTemplate.findAll(EnrichedEvent.class, outputFileEnrichedEventCollectionName).size());
            outputManagerService.cleanDocuments(now().plus(Duration.ofDays(1)), Arrays.asList(Schema.FILE));
            // 1 enriched event should have been deleted
            Assert.assertEquals(1, mongoTemplate.findAll(EnrichedEvent.class, outputFileEnrichedEventCollectionName).size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testCleanDocumentsForNonExistingSchema() {
        try {
            String outputFileEnrichedEventCollectionName = new OutputToCollectionNameTranslator().toCollectionName(Schema.PRINT);
            Assert.assertEquals(0, mongoTemplate.findAll(EnrichedEvent.class, outputFileEnrichedEventCollectionName).size());
            outputManagerService.cleanDocuments(now().plus(Duration.ofDays(1)), Arrays.asList(Schema.PRINT));
            Assert.assertEquals(0, mongoTemplate.findAll(EnrichedEvent.class, outputFileEnrichedEventCollectionName).size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
