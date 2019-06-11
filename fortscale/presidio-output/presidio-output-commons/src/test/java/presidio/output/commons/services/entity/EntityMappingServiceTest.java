package presidio.output.commons.services.entity;


import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.commons.services.spring.EntityMappingServiceTestConfig;
import presidio.output.domain.records.events.AuthenticationEnrichedEvent;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.EnrichedUserEvent;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;

import java.time.Instant;
import java.util.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EntityMappingServiceTestConfig.class)
public class EntityMappingServiceTest {

    @Autowired
    private EntityMappingService entityMappingService;
    @Autowired
    private EventPersistencyService eventPersistencyService;
    @Autowired
    private OutputToCollectionNameTranslator outputToCollectionNameTranslator;

    @Before
    public void cleanCollections() {
        Schema[] schemas = Schema.values();
        for (Schema schema : schemas) {
            eventPersistencyService.remove(schema, Instant.EPOCH, Instant.now());
        }
    }

    @Test
    public void getEntityNameForUser() {
        String entityType = "userId";
        String entityId = "userId1";
        generateEnrichedUserEvent("userName", entityId, "Active Directory");
        List<Schema> schemas = entityMappingService.getSchemas(entityType);
        List<String> collections = new ArrayList<>();
        schemas.forEach(schema -> collections.add(outputToCollectionNameTranslator.toCollectionName(schema)));
        EnrichedEvent event = eventPersistencyService.findLatestEventForEntity(entityId, collections, entityType);
        Assert.assertEquals("userName", entityMappingService.getEntityName(event, entityType));
    }

    @Test
    public void getEntityNameNotUser() {
        String entityType = "srcMachineId";
        String entityId = "machineId";
        generateAuthenticationEnrichedEvent("userName", "userId", entityId);
        List<Schema> schemas = entityMappingService.getSchemas(entityType);
        List<String> collections = new ArrayList<>();
        schemas.forEach(schema -> collections.add(outputToCollectionNameTranslator.toCollectionName(schema)));
        EnrichedEvent event = eventPersistencyService.findLatestEventForEntity(entityId, collections, entityType);
        Assert.assertEquals(entityId, entityMappingService.getEntityName(event, entityType));
    }

    private void generateEnrichedUserEvent(String userName, String userId, String schema) {
        EnrichedUserEvent enrichedEvent = new EnrichedUserEvent(Instant.now(), Instant.now(), "event", schema, userId, userName,
                "userDisplayName", "dataSource", null);
        saveEvent(enrichedEvent, Schema.ACTIVE_DIRECTORY);
    }

    private void generateAuthenticationEnrichedEvent(String userName, String userId, String srcMachineId) {
        AuthenticationEnrichedEvent event = new AuthenticationEnrichedEvent(Instant.now(), Instant.now(), "eventId1", Schema.AUTHENTICATION.toString(), userId, userName, "userDisplayName",
                "dataSource", "User authenticated through Kerberos", new ArrayList<>(), EventResult.SUCCESS,
                "SUCCESS", null);
        event.setSrcMachineId(srcMachineId);
        saveEvent(event, Schema.AUTHENTICATION);
    }


    private void saveEvent(EnrichedEvent event, Schema schema) {
        List<EnrichedEvent> events = new ArrayList<>();
        events.add(event);
        try {
            eventPersistencyService.store(schema, events);
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
