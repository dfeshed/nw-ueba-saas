package presidio.input.sdk.impl.services;


import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;
import fortscale.domain.core.EventResult;
import fortscale.utils.mongodb.util.ToCollectionNameTranslator;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.sdk.impl.repositories.DataSourceRepository;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.sdk.api.domain.rawevents.FileRawEvent;
import presidio.sdk.api.services.PresidioInputPersistencyService;
import presidio.sdk.api.utils.InputToCollectionNameTranslator;
import presidio.sdk.api.validation.InvalidInputDocument;
import presidio.sdk.api.validation.ValidationResults;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MongodbTestConfig.class, PresidioInputPersistencyServiceConfig.class})
@EnableMongoRepositories(basePackageClasses = DataSourceRepository.class)
public class PresidioInputPersistencyServiceMongoImplTest {

    @Autowired
    public ToCollectionNameTranslator toCollectionNameTranslator;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    Instant startTime;
    Instant endTime;

    @Before
    public void before() {
        mongoTemplate.dropCollection(toCollectionNameTranslator.toCollectionName(Schema.FILE));
        startTime = Instant.now().truncatedTo(ChronoUnit.HOURS).minus(1, ChronoUnit.HOURS);
        endTime = Instant.now().truncatedTo(ChronoUnit.HOURS).plus(1, ChronoUnit.HOURS);
    }

    @Test
    public void contextLoads() throws Exception {

        Assert.assertNotNull(presidioInputPersistencyService);
        Assert.assertNotNull(mongoTemplate);
    }

    @Test
    public void testReadBoundariesStartTime() throws Exception {
        List<AbstractAuditableDocument> list = new ArrayList<>();
        AbstractAuditableDocument doc = createEvent(Instant.now());

        list.add(doc);
        presidioInputPersistencyService.store(Schema.FILE, list);
        List<? extends AbstractAuditableDocument> all = presidioInputPersistencyService.find(Schema.FILE, startTime, endTime);
        Assert.assertEquals(doc.toString().trim(), all.get(0).toString().trim());
    }

    @Test
    public void testReadBoundariesEndTime() throws Exception {
        List<AbstractAuditableDocument> list = new ArrayList<>();
        AbstractAuditableDocument doc = createEvent(Instant.now());
        list.add(doc);
        presidioInputPersistencyService.store(Schema.FILE, list);
        List<? extends AbstractAuditableDocument> all = presidioInputPersistencyService.find(Schema.FILE, startTime, doc.getDateTime());
        Assert.assertEquals(0, all.size());

    }

    @Test
    public void storeOneEventToMongoAndReadEventFromMongo() {
        List<AbstractAuditableDocument> list = new ArrayList<>();
        AbstractAuditableDocument doc = createEvent(Instant.now());
        list.add(doc);
        presidioInputPersistencyService.store(Schema.FILE, list);
        List<FileRawEvent> all = mongoTemplate.findAll(FileRawEvent.class, toCollectionNameTranslator.toCollectionName(Schema.FILE));
        Assert.assertEquals(doc.toString().trim(), all.get(0).toString().trim());
    }

    @Test
    public void deleteAllEventsFromMongoCollectionFile() {
        mongoTemplate.dropCollection(FileRawEvent.class);
        List<AbstractAuditableDocument> list = new ArrayList<>();
        Instant currentTime = Instant.parse("2017-12-17T18:15:00Z");
        list.add(createEvent(currentTime.minus(5, ChronoUnit.MINUTES)));
        list.add(createEvent(currentTime.minus(5, ChronoUnit.HOURS)));
        presidioInputPersistencyService.store(Schema.FILE, list);
        int numberOfEventsDeleted = 0;
        try {
            numberOfEventsDeleted = presidioInputPersistencyService.clean(Schema.FILE, currentTime.truncatedTo(ChronoUnit.HOURS), currentTime.truncatedTo(ChronoUnit.HOURS).plus(1, ChronoUnit.HOURS));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        List<FileRawEvent> all = mongoTemplate.findAll(FileRawEvent.class, ((InputToCollectionNameTranslator) toCollectionNameTranslator).toCollectionName(Schema.FILE));
        Assert.assertEquals(1, numberOfEventsDeleted);
        Assert.assertEquals(1, all.size());
    }

    @Test
    public void storeFileEvent_isDriveSharedIsNull_shouldKeepNullValue() {
        FileRawEvent event = createEvent(Instant.now());
        event.setIsDstDriveShared(null);
        event.setIsSrcDriveShared(null);
        List<AbstractAuditableDocument> eventsList = new ArrayList<>();
        eventsList.add(event);
        presidioInputPersistencyService.store(Schema.FILE, eventsList);

        List<FileRawEvent> storedEvetns = mongoTemplate.findAll(FileRawEvent.class, ((InputToCollectionNameTranslator) toCollectionNameTranslator).toCollectionName(Schema.FILE));
        Assert.assertEquals(1, storedEvetns.size());
        Assert.assertEquals(null, storedEvetns.get(0).getIsDstDriveShared());
        Assert.assertEquals(null, storedEvetns.get(0).getIsSrcDriveShared());
    }

    @Test
    public void storeInvalidEvent_eventShouldBeFiltered() {
        FileRawEvent event = createEvent(Instant.now());
        event.setUserId(null);
        event.setEventId(null);
        ValidationResults validationResults = presidioInputPersistencyService.store(Schema.FILE, Arrays.asList(event));
        Assert.assertEquals(0, validationResults.validDocuments.size());
        Assert.assertEquals(1, validationResults.invalidDocuments.size());

        List<InvalidInputDocument> filteredEvents = mongoTemplate.findAll(InvalidInputDocument.class, DataServiceImpl.INVALID_DOCUMENTS_COLLECTION_NAME);
        Assert.assertEquals(1, filteredEvents.size());
        Assert.assertEquals(2, filteredEvents.get(0).getViolations().size());
    }

    public FileRawEvent createEvent(Instant time) {
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("key", "value");
        FileRawEvent fileRawEvent = new FileRawEvent(time, "eventId", "dataSource",
                "userId", "operationType", null, EventResult.SUCCESS,
                "userName", "userDisplayName", additionalInfo, "srcFilePath",
                true, "dstFilePath", true, 0L, "resultCode");
        return fileRawEvent;
    }
}
