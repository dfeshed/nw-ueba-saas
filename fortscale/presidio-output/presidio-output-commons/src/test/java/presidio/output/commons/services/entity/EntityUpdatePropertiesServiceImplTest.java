package presidio.output.commons.services.entity;

import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.commons.services.spring.EntityUpdatePropertiesTestConfiguration;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntitySeverity;
import presidio.output.domain.records.events.*;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.output.domain.services.event.EventPersistencyService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EntityUpdatePropertiesTestConfiguration.class)
public class EntityUpdatePropertiesServiceImplTest {

    @Autowired
    private EntityPersistencyService entityPersistencyService;
    @Autowired
    private EntityPropertiesUpdateService entityPropertiesUpdateService;
    @Autowired
    private EventPersistencyService eventPersistencyService;

    private final String TAG_ADMIN = "admin";

    @Before
    public void cleanCollections() {
        Schema[] schemas = Schema.values();
        for (Schema schema : schemas) {
            eventPersistencyService.remove(schema, Instant.EPOCH, Instant.now());
        }
    }

    @Test
    public void updateEntityPropertiesWithAuthenticationEvent() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        generateAuthenticationEnrichedEvent(eventDate.minus(1, ChronoUnit.MINUTES), "userName1", "userId", "userDisplayName1", additionalInfo);
        additionalInfo.put("isUserAdmin", "true");
        generateAuthenticationEnrichedEvent(eventDate, "userName1", "userId", "userDisplayName1", additionalInfo);
        Entity entity = generateEntityAndSave("userId", "userName", false);
        Entity entityUpdated = entityPropertiesUpdateService.updateEntityProperties(entity);
        Assert.assertEquals("userName1", entityUpdated.getEntityName());
    }

    @Test
    public void updateEntityPropertiesWithFileEvent() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generateFileEnrichedEvent(eventDate, "userName1", "userId", "userDisplayName1", additionalInfo);
        Entity entity = generateEntityAndSave("userId", "userName", true);
        Entity entityUpdated = entityPropertiesUpdateService.updateEntityProperties(entity);
        Assert.assertEquals("userName1", entityUpdated.getEntityName());
        Assert.assertEquals(entity.getTags().get(0), entityUpdated.getTags().get(0));
    }

    @Test
    public void updateEntityPropertiesWithActiveDirectoryEvent() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generateActiveDirectoryEnrichedEvent(eventDate, "userName1", "userId", "userDisplayName1", null);
        Entity entity = generateEntityAndSave("userId", "userName", false);
        Entity entityUpdated = entityPropertiesUpdateService.updateEntityProperties(entity);
        Assert.assertEquals("userName1", entityUpdated.getEntityName());
        Assert.assertTrue(CollectionUtils.isEmpty(entityUpdated.getTags()));
    }

    @Test
    public void updateEntityPropertiesWithPrintEvent() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generatePrintEnrichedEvent(eventDate, "userName1", "userId", "userDisplayName1", null);
        Entity entity = generateEntityAndSave("userId", "userName", false);
        Entity entityUpdated = entityPropertiesUpdateService.updateEntityProperties(entity);
        Assert.assertEquals("userName1", entityUpdated.getEntityName());
        Assert.assertTrue(CollectionUtils.isEmpty(entityUpdated.getTags()));
    }

    @Test
    public void updateEntityPropertiesWithAuthenticationAndActiveDirectoryEvent() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generateActiveDirectoryEnrichedEvent(eventDate, "userName1", "userId", "userDisplayName1", additionalInfo);
        generateAuthenticationEnrichedEvent(eventDate, "userName2", "userId", "userDisplayName2", additionalInfo);
        Entity entity = generateEntityAndSave("userId", "userName", false);
        Entity entityUpdated = entityPropertiesUpdateService.updateEntityProperties(entity);
        Assert.assertEquals("userName2", entityUpdated.getEntityName());
    }

    @Test
    public void updateEntityPropertiesNoEvents() {
        Entity entity = generateEntityAndSave("entityId", "entityName", false);
        Entity entityUpdated = entityPropertiesUpdateService.updateEntityProperties(entity);
        Assert.assertNull(entityUpdated);
    }

    @Test
    public void updateEntityPropertiesNoChanges() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generateAuthenticationEnrichedEvent(eventDate, "userName", "userId", "userDisplayName", additionalInfo);
        Entity entity = generateEntityAndSave("userId", "userName", true);
        Entity entityUpdated = entityPropertiesUpdateService.updateEntityProperties(entity);
        Assert.assertNull(entityUpdated);
    }

    @Test
    public void updateEntityPropertiesMissingDisplayName_shouldSetNullAsDisplayName() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generateAuthenticationEnrichedEvent(eventDate, "userName1", "userId", null, additionalInfo);
        Entity entity = generateEntityAndSave("userId", "userName", true);
        Entity entityUpdated = entityPropertiesUpdateService.updateEntityProperties(entity);
        Assert.assertEquals("userName1", entityUpdated.getEntityName());
    }

    @Test
    public void updateEntityPropertiesEntityWithoutDisplayName_shouldSetUpdatedDisplayName() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generateAuthenticationEnrichedEvent(eventDate, "userName1", "userId", "displayName1", additionalInfo);
        Entity entity = generateEntityAndSave("userId", "userName", true);
        Entity entityUpdated = entityPropertiesUpdateService.updateEntityProperties(entity);
        Assert.assertEquals("userName1", entityUpdated.getEntityName());
    }

    private Entity generateEntityAndSave(String entityId, String entityName, boolean tagAdmin) {
        List<String> tags = new ArrayList<>();
        if (tagAdmin) {
            tags.add(TAG_ADMIN);
        }
        return generateEntityAndSave(entityId, entityName, tags);
    }

    private Entity generateEntityAndSave(String entityId, String entityName, List<String> tags) {
        Entity entity1 = new Entity(entityId, entityName, 0d, null, null, tags, EntitySeverity.LOW, 0, "userId");
        entityPersistencyService.save(entity1);
        return entity1;
    }

    private void saveEvent(EnrichedUserEvent event, Schema schema) {
        List<EnrichedUserEvent> events = new ArrayList<>();
        events.add(event);
        try {
            eventPersistencyService.store(schema, events);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    private void generateFileEnrichedEvent(Instant eventDate, String userName, String userId, String userDisplayName, Map<String, String> additionalInfo) {
        EnrichedUserEvent  event = new FileEnrichedEvent(eventDate, eventDate, "eventId", Schema.FILE.toString(),
                userId, userName, userDisplayName, "dataSource", "oppType", new ArrayList<String>(),
                EventResult.FAILURE, "resultCode", additionalInfo, "absoluteSrcFilePath", "absoluteDstFilePath",
                "absoluteSrcFolderFilePath", "absoluteDstFolderFilePath", 20L, true, true);
        saveEvent(event, Schema.FILE);

    }

    private void generateActiveDirectoryEnrichedEvent(Instant eventDate, String userName, String userId, String userDisplayName, Map<String, String> additionalInfo) {
        EnrichedUserEvent event = new ActiveDirectoryEnrichedEvent(eventDate, eventDate, "eventId", Schema.ACTIVE_DIRECTORY.toString(),
                userId, userName, userDisplayName, "dataSource", "USER_ACCOUNT_TYPE_CHANGED",
                new ArrayList<String>(), EventResult.SUCCESS, "resultCode", additionalInfo, "objectId");
        saveEvent(event, Schema.ACTIVE_DIRECTORY);
    }

    private void generatePrintEnrichedEvent(Instant eventDate, String userName, String userId, String userDisplayName, Map<String, String> additionalInfo) {
        PrintEnrichedEvent printEnrichedEvent = new PrintEnrichedEvent(Instant.now(), eventDate, "eventId",
                "schema", userId, userName, userDisplayName,
                "dataSource", "operationType", null, EventResult.SUCCESS,
                "resultCode", additionalInfo, "srcMachineId", "srcMachineCluster",
                "printerId", "printareName", "srcFilePath", "srcFolderPath",
                "srcFileExtension", false, 10l, 10l);
        saveEvent(printEnrichedEvent, Schema.PRINT);
    }

    private void generateAuthenticationEnrichedEvent(Instant eventDate, String userName, String userId, String userDisplayName, Map<String, String> additionalInfo) {
        EnrichedUserEvent event = new AuthenticationEnrichedEvent(eventDate, eventDate, "eventId1", Schema.AUTHENTICATION.toString(), userId, userName, userDisplayName,
                "dataSource", "User authenticated through Kerberos", new ArrayList<String>(), EventResult.SUCCESS,
                "SUCCESS", additionalInfo);
        saveEvent(event, Schema.AUTHENTICATION);
    }
}
