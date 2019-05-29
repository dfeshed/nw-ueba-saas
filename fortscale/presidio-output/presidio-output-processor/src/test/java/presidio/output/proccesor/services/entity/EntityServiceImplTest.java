package presidio.output.proccesor.services.entity;

import fortscale.domain.core.EventResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import presidio.output.commons.services.entity.EntitySeverityService;
import presidio.output.commons.services.entity.EntitySeverityServiceImpl;
import presidio.output.domain.records.PresidioRange;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntitySeverity;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.EnrichedUserEvent;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.alerts.AlertPersistencyServiceImpl;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.output.domain.services.entities.EntityPersistencyServiceImpl;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.processor.services.entity.EntityScoreService;
import presidio.output.processor.services.entity.EntityServiceImpl;
import presidio.output.processor.services.entity.EntitiesAlertData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by shays on 27/08/2017.
 */

public class EntityServiceImplTest {

    public static final int ALERT_EFFECTIVE_DURATION_IN_DAYS = 90;

    private EntityServiceImpl entityService;

    private EntityPersistencyService mockEntityPresistency;
    private EventPersistencyService mockEventPersistency;
    private EntityScoreService mockEntityScoreService;
    private AlertPersistencyService mockAlertPersistency;
    private EntitySeverityService mockEntitySeverityService;

    private Page<Alert> emptyAlertPage;


    @Before
    public void setup() {
        mockEntityPresistency = Mockito.mock(EntityPersistencyServiceImpl.class);
        mockEventPersistency = Mockito.mock(EventPersistencyService.class);
        mockEntityScoreService = Mockito.mock(EntityScoreService.class);
        mockAlertPersistency = Mockito.mock(AlertPersistencyServiceImpl.class);
        mockEntitySeverityService = Mockito.mock(EntitySeverityService.class);
        Map<EntitySeverity, PresidioRange<Double>> severityRangeMap = new LinkedHashMap<>();
        severityRangeMap.put(EntitySeverity.LOW, new PresidioRange<>(0d, 30d));
        severityRangeMap.put(EntitySeverity.MEDIUM, new PresidioRange<>(30d, 60d));
        severityRangeMap.put(EntitySeverity.HIGH, new PresidioRange<>(60d, 90d));
        severityRangeMap.put(EntitySeverity.CRITICAL, new PresidioRange<>(90d, 100d));
        Mockito.when(mockEntitySeverityService.getSeveritiesMap(false)).thenReturn(new EntitySeverityServiceImpl.EntityScoreToSeverity(severityRangeMap));

        entityService = new EntityServiceImpl(mockEventPersistency,
                mockEntityPresistency,
                mockAlertPersistency,
                mockEntityScoreService,
                mockEntitySeverityService,
                ALERT_EFFECTIVE_DURATION_IN_DAYS,
                1000);
        emptyAlertPage = new PageImpl<>(Collections.emptyList());
    }


    @Test
    public void testUpdateEntityScoreBatch() throws Exception {
        List<Entity> entitiesWithOldScore = Arrays.asList(
                new Entity("entity1", null, 50, null, null, null, EntitySeverity.CRITICAL, 0, "entity"),
                new Entity("entity2", null, 50, null, null, null, EntitySeverity.CRITICAL, 0, "entity"),
                new Entity("entity3", null, 50, null, null, null, EntitySeverity.CRITICAL, 0, "entity")
        );

        Pageable pageable1 = new PageRequest(0, 3);
        Page<Entity> entitiesPage = new PageImpl<>(entitiesWithOldScore, pageable1, 3);

        Set<String> entitiesIDForBatch = new HashSet<>();
        entitiesIDForBatch.add(entitiesWithOldScore.get(0).getId());
        entitiesIDForBatch.add(entitiesWithOldScore.get(1).getId());
        entitiesIDForBatch.add(entitiesWithOldScore.get(2).getId());

        Map<String, EntitiesAlertData> newEntitiesScore = new HashMap<>();
        newEntitiesScore.put(entitiesWithOldScore.get(0).getId(), new EntitiesAlertData(80D, 1, null, new ArrayList<String>()));
        newEntitiesScore.put(entitiesWithOldScore.get(1).getId(), new EntitiesAlertData(50D, 1, null, new ArrayList<String>()));
        newEntitiesScore.put(entitiesWithOldScore.get(2).getId(), new EntitiesAlertData(30D, 1, null, new ArrayList<String>()));

        Mockito.when(this.mockEntityPresistency.findByIds(Mockito.any(Set.class), Mockito.any(PageRequest.class))).thenAnswer(invocation -> {
            Set<String> entityIds = (Set<String>) invocation.getArguments()[0];
            PageRequest pageContext = (PageRequest) invocation.getArguments()[1];

            if (pageContext.getPageNumber() == 0) {
                return entitiesPage;
            } else {
                return null;
            }
        });

        List<Entity> changedEntities = Whitebox.invokeMethod(entityService, "updateEntityAlertDataForBatch", newEntitiesScore, entitiesIDForBatch);
        assertEquals(2, changedEntities.size());
        assertEquals(80D, changedEntities.get(0).getScore(), 0.00001);
        assertEquals(30D, changedEntities.get(1).getScore(), 0.00001);


    }

    @Test
    public void testAddEntityAlertData() {
        Entity entity1 = new Entity("entity1", null, 50, null, null, null, EntitySeverity.CRITICAL, 0, "entity");
        List<String> classification1 = null, classification2, classification3, classification4;
        List<String> indicators1 = null, indicators2, indicators3;
        classification2 = new ArrayList<>(Arrays.asList("a", "b"));
        indicators2 = new ArrayList<>(Arrays.asList("c", "d"));
        classification3 = new ArrayList<>(Arrays.asList("a", "c"));
        classification4 = new ArrayList<>(Arrays.asList("c"));
        indicators3 = new ArrayList<>(Arrays.asList("c", "e"));
        assertEquals(null, entity1.getIndicators());
        assertEquals(null, entity1.getAlertClassifications());
        // adding empty classification list and empty indicator list
        EntitiesAlertData entitiesAlertData1 = new EntitiesAlertData(0, 0, null, indicators1);
        entityService.addEntityAlertData(entity1, entitiesAlertData1);
        assertEquals(null, entity1.getIndicators());
        assertEquals(null, entity1.getAlertClassifications());
        // Adding classification list with 2 classifications but saving only the first one on the entity and adding 2 indicators
        EntitiesAlertData entitiesAlertData2 = new EntitiesAlertData(0, 0, classification2.get(0), indicators2);
        entityService.addEntityAlertData(entity1, entitiesAlertData2);
        assertEquals(2, entity1.getIndicators().size());
        assertEquals(1, entity1.getAlertClassifications().size());
        // adding classification list of 2 classifications that the first one already exists on the entity and adding 2 indicators one of which already exists
        EntitiesAlertData entitiesAlertData3 = new EntitiesAlertData(0, 0, classification3.get(0), indicators3);
        entityService.addEntityAlertData(entity1, entitiesAlertData3);
        assertEquals(3, entity1.getIndicators().size());
        assertEquals(1, entity1.getAlertClassifications().size());
        // adding existing classifications and indicators
        EntitiesAlertData entitiesAlertData4 = new EntitiesAlertData(0, 0, null, indicators1);
        entityService.addEntityAlertData(entity1, entitiesAlertData4);
        assertEquals(3, entity1.getIndicators().size());
        assertEquals(1, entity1.getAlertClassifications().size());
        // adding new classification but existing indicator
        EntitiesAlertData entitiesAlertData5 = new EntitiesAlertData(0, 0, classification4.get(0), indicators1);
        entityService.addEntityAlertData(entity1, entitiesAlertData5);
        assertEquals(3, entity1.getIndicators().size());
        assertEquals(2, entity1.getAlertClassifications().size());

    }

    @Test
    public void createEntityFromEnrichedEvent() {
        EventResult result = EventResult.SUCCESS;
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "false");
        String entityId = "entityId1";
        String userDisplayName = "userDisplayName1";
        EnrichedUserEvent enrichedEvent = new EnrichedUserEvent(Instant.now(), Instant.now(), "event1", "Active Directory", entityId, entityId,
                userDisplayName, "Active Directory", additionalInfo);
        Mockito.when(this.mockEventPersistency.findLatestEventForEntity(Mockito.any(String.class), Mockito.any(List.class), Mockito.any(String.class))).thenReturn(enrichedEvent);

        Entity entity = entityService.createEntity(entityId, "entity");
        assertEquals(0, entity.getTags().size());
        assertEquals(entityId, entity.getEntityId());
        assertEquals(entityId, entity.getEntityName());
    }
}
