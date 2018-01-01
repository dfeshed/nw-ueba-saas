package presidio.input.core.services.impl;

import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.domain.core.EventResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.authentication.EnrichedAuthenticationRecord;
import presidio.ade.domain.store.enriched.EnrichedDataAdeToCollectionNameTranslator;
import presidio.input.core.FortscaleInputCoreApplicationTest;
import presidio.input.core.services.data.AdeDataService;
import presidio.input.core.spring.InputCoreConfigurationTest;
import presidio.monitoring.elastic.repositories.MetricRepository;
import presidio.output.domain.records.events.AuthenticationEnrichedEvent;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;
import presidio.output.sdk.api.OutputDataServiceSDK;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;
import presidio.sdk.api.domain.rawevents.FileRawEvent;
import presidio.sdk.api.services.PresidioInputPersistencyService;
import presidio.sdk.api.utils.InputToCollectionNameTranslator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {FortscaleInputCoreApplicationTest.springConfig.class, InputCoreConfigurationTest.class})
public class InputExecutionServiceTest {

    @Autowired
    PresidioExecutionService executionService;

    @Autowired
    AdeDataService adeDataService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    EnrichedDataAdeToCollectionNameTranslator adeToCollectionNameTranslator;

    @Autowired
    OutputDataServiceSDK outputDataServiceSDK;

    @Autowired
    OutputToCollectionNameTranslator outputToCollectionNameTranslator;

    @MockBean
    MetricRepository metricRepository;

    @Autowired
    PresidioInputPersistencyService inputPersistencyService;

    @Autowired
    InputToCollectionNameTranslator inputToCollectionNameTranslator;


    @Before
    public void before() {
        mongoTemplate.dropCollection(outputToCollectionNameTranslator.toCollectionName(Schema.AUTHENTICATION));
        mongoTemplate.dropCollection(adeToCollectionNameTranslator.toCollectionName(Schema.AUTHENTICATION.toString().toLowerCase()));
        mongoTemplate.dropCollection(inputToCollectionNameTranslator.toCollectionName(Schema.AUTHENTICATION));

        mongoTemplate.dropCollection(outputToCollectionNameTranslator.toCollectionName(Schema.FILE));
        mongoTemplate.dropCollection(adeToCollectionNameTranslator.toCollectionName(Schema.FILE.toString().toLowerCase()));
        mongoTemplate.dropCollection(inputToCollectionNameTranslator.toCollectionName(Schema.FILE));
    }

    @Test
    public void testCleanup() throws Exception {
        Instant startTime = Instant.parse("2017-12-12T14:00:00.000Z");
        Instant endTime = Instant.parse("2017-12-12T15:00:00.000Z");

        // ade records
        List<EnrichedRecord> records = new ArrayList<>();
        records.add(new EnrichedAuthenticationRecord(Instant.parse("2017-12-12T14:15:29.975Z")));
        records.add(new EnrichedAuthenticationRecord(Instant.parse("2017-12-12T10:15:29.975Z")));
        adeDataService.store(Schema.AUTHENTICATION, startTime, endTime, records);

        // output records
        List<EnrichedEvent> events = new ArrayList<>();
        events.add(createOutputAuthenticationEvent(Instant.parse("2017-12-12T14:15:29.975Z")));
        events.add(createOutputAuthenticationEvent(Instant.parse("2017-12-12T10:15:29.975Z")));
        outputDataServiceSDK.store(Schema.AUTHENTICATION, events);

        executionService.cleanup(Schema.AUTHENTICATION, startTime, endTime, 1d);
        List<EnrichedRecord> allAdeDocuments = mongoTemplate.findAll(EnrichedRecord.class, adeToCollectionNameTranslator.toCollectionName(Schema.AUTHENTICATION.toString().toLowerCase()));
        List<AuthenticationEnrichedEvent> allOutputDocuments = mongoTemplate.findAll(AuthenticationEnrichedEvent.class, outputToCollectionNameTranslator.toCollectionName(Schema.AUTHENTICATION));
        Assert.assertEquals(1, allAdeDocuments.size());
        Assert.assertEquals(1, allOutputDocuments.size());
    }


    @Test
    public void testRetentionClean() throws Exception {
        Instant startTime = Instant.parse("2017-12-12T14:00:00.000Z");
        Instant endTime = Instant.parse("2017-12-12T15:00:00.000Z");

        List<AuthenticationRawEvent> rawEvents = new ArrayList<>();
        rawEvents.add(createAuthenticationRawEvent(Instant.parse("2017-12-10T14:00:00.000Z")));
        rawEvents.add(createAuthenticationRawEvent(Instant.parse("2017-12-09T14:00:00.000Z")));
        rawEvents.add(createAuthenticationRawEvent(Instant.parse("2017-12-11T14:00:00.000Z")));
        inputPersistencyService.store(Schema.AUTHENTICATION, rawEvents);

        executionService.retentionClean(Schema.AUTHENTICATION, startTime, endTime);

        List<AuthenticationRawEvent> remainingRawEvents = mongoTemplate.findAll(AuthenticationRawEvent.class, inputToCollectionNameTranslator.toCollectionName(Schema.AUTHENTICATION));
        Assert.assertEquals(1, remainingRawEvents.size());
    }

    @Test
    public void testRun_isSrcDriveSharedFiledIsNull_shouldKeepNullValue(){
        FileRawEvent fileRawEvent = createFileRawEvent(Instant.parse("2017-12-10T14:00:00.000Z"));
        fileRawEvent.setIsSrcDriveShared(null);
        fileRawEvent.setIsDstDriveShared(null);

        List<FileRawEvent> rawEvents = new ArrayList<>();
        rawEvents.add(fileRawEvent);
        inputPersistencyService.store(Schema.FILE, rawEvents);

        Instant startTime = Instant.parse("2017-12-12T14:00:00.000Z");
        Instant endTime = Instant.parse("2017-12-12T15:00:00.000Z");
        try {
            executionService.run(Schema.FILE, startTime, endTime, 10D);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        List<FileRawEvent> enrichedEvents = mongoTemplate.findAll(FileRawEvent.class, inputToCollectionNameTranslator.toCollectionName(Schema.FILE));
        Assert.assertEquals(1, enrichedEvents.size());
        Assert.assertEquals(null, enrichedEvents.get(0).getIsDstDriveShared());
        Assert.assertEquals(null, enrichedEvents.get(0).getIsSrcDriveShared());
    }

    @Test
    public void testRun_isSrcDriveSharedFiledIsTrue_shouldKeepOriginalValue(){
        FileRawEvent fileRawEvent = createFileRawEvent(Instant.parse("2017-12-10T14:00:00.000Z"));
        fileRawEvent.setIsSrcDriveShared(true);
        fileRawEvent.setIsDstDriveShared(true);

        List<FileRawEvent> rawEvents = new ArrayList<>();
        rawEvents.add(fileRawEvent);
        inputPersistencyService.store(Schema.FILE, rawEvents);

        Instant startTime = Instant.parse("2017-12-12T14:00:00.000Z");
        Instant endTime = Instant.parse("2017-12-12T15:00:00.000Z");
        try {
            executionService.run(Schema.FILE, startTime, endTime, 10D);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        List<FileRawEvent> enrichedEvents = mongoTemplate.findAll(FileRawEvent.class, inputToCollectionNameTranslator.toCollectionName(Schema.FILE));
        Assert.assertEquals(1, enrichedEvents.size());
        Assert.assertEquals(true, enrichedEvents.get(0).getIsDstDriveShared());
        Assert.assertEquals(true, enrichedEvents.get(0).getIsSrcDriveShared());
    }

    private EnrichedEvent createOutputAuthenticationEvent(Instant time) {
        return new AuthenticationEnrichedEvent(time, time, "eventId1", "schema", "userId", "username", "userDisplayName", "dataSource", "User authenticated through Kerberos", new ArrayList<String>(), EventResult.SUCCESS, "SUCCESS", new HashMap<>());
    }

    private AuthenticationRawEvent createAuthenticationRawEvent(Instant eventTime) {
        AuthenticationRawEvent authenticationRawEvent = new AuthenticationRawEvent(eventTime, "eventId",
                "dataSource", "userId", "operationType", null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                "srcMachineId", "srcMachineName", "dstMachineId",
                "dstMachineName", "dstMachineDomain", "resultCode", "site");

        return authenticationRawEvent;
    }

    private FileRawEvent createFileRawEvent(Instant eventTime) {
        FileRawEvent authenticationRawEvent = new FileRawEvent(eventTime, "eventId", "datasource", "userId",
                "operationType", null, EventResult.FAILURE, "userName", "displayName",
                null, "srcFilePath", true, "dstFilePath", true, 10L, "resultCode");
        return authenticationRawEvent;
    }

}
