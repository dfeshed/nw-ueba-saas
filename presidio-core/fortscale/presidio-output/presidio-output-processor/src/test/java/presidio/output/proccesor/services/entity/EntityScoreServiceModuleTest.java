package presidio.output.proccesor.services.entity;

import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ScrolledPage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.commons.services.alert.AlertSeverityService;
import presidio.output.commons.services.entity.EntitySeverityService;
import presidio.output.commons.services.entity.EntitySeverityServiceImpl;
import presidio.output.domain.records.AbstractElasticDocument;
import presidio.output.domain.records.PresidioRange;
import presidio.output.domain.records.EntitySeveritiesRangeDocument;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntityQuery;
import presidio.output.domain.records.entity.EntitySeverity;
import presidio.output.domain.repositories.EntitySeveritiesRangeRepository;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.output.proccesor.spring.OutputProcessorTestConfiguration;
import presidio.output.proccesor.spring.TestConfig;
import presidio.output.processor.services.entity.EntityService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {OutputProcessorTestConfiguration.class, TestConfig.class, ElasticsearchTestConfig.class, MongodbTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class EntityScoreServiceModuleTest {

    @Autowired
    private EntitySeveritiesRangeRepository entitySeveritiesRangeRepository;

    @Autowired
    private EntityPersistencyService entityPersistencyService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private PresidioElasticsearchTemplate esTemplate;

    @Autowired
    private AlertPersistencyService alertPersistencyService;

    @Autowired
    public Client client;

    @Autowired
    private AlertSeverityService alertSeverityService;

    @Autowired
    private EntitySeverityService entitySeverityService;

    @After
    public void cleanTestData() {
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .source(AbstractElasticDocument.INDEX_NAME + "-" + Entity.DOC_TYPE)
                .get();
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .source(AbstractElasticDocument.INDEX_NAME + "-" + Alert.ALERT_TYPE)
                .get();
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .source(AbstractElasticDocument.INDEX_NAME + "-" + EntitySeveritiesRangeDocument.ENTITY_SEVERITY_RANGE_DOC_TYPE)
                .get();
    }

    @Test
    public void testSingleEntityScoreCalculation() {
        //Generate one entity with 2 critical alerts
        String entityDocumentId1 = "entityDocumentId1";
        String entityType = "entityType";
        generateEntityAndAlerts("entityId1", entityDocumentId1, "entityName1", entityType, AlertEnums.AlertSeverity.CRITICAL, AlertEnums.AlertSeverity.CRITICAL);

        EntityQuery.EntityQueryBuilder queryBuilder = new EntityQuery.EntityQueryBuilder().pageNumber(0).pageSize(10).filterByEntitiesIds(Collections.singletonList("entityId1"));
        Page<Entity> entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());
        Assert.assertEquals("entityId1", entitiesPageResult.getContent().get(0).getEntityId());
        Assert.assertEquals(entityDocumentId1, entitiesPageResult.getContent().get(0).getId());
        Assert.assertEquals("entityName1", entitiesPageResult.getContent().get(0).getEntityName());
        Assert.assertEquals(0, entitiesPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertNull(entitiesPageResult.getContent().get(0).getSeverity());

        entityService.updateAllEntitiesAlertData(Instant.now(), entityType);
        entitySeverityService.updateSeverities(entityType);

        entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());
        Assert.assertEquals("entityId1", entitiesPageResult.getContent().get(0).getEntityId());
        Assert.assertEquals("entityName1", entitiesPageResult.getContent().get(0).getEntityName());
        Assert.assertEquals(40, entitiesPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertEquals(EntitySeverity.LOW, entitiesPageResult.getContent().get(0).getSeverity());

    }

    @Test
    public void testTwoEntityTypesScoreCalculation() {
        //Generate 2 entities with 2 critical alerts
        generateEntityAndAlerts("entityId", "entityDocumentId", "entityName", "entityType", AlertEnums.AlertSeverity.CRITICAL, AlertEnums.AlertSeverity.CRITICAL);
        generateEntityAndAlerts("entityId1", "entityDocumentId1", "entityName1", "entityType1", AlertEnums.AlertSeverity.CRITICAL, AlertEnums.AlertSeverity.CRITICAL);


        EntityQuery.EntityQueryBuilder queryBuilder = new EntityQuery.EntityQueryBuilder().pageNumber(0).pageSize(10).filterByEntitiesIds(Collections.singletonList("entityId")).filterByEntitiesTypes(Collections.singletonList("entityType"));
        Page<Entity> entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());
        Assert.assertEquals("entityId", entitiesPageResult.getContent().get(0).getEntityId());
        Assert.assertEquals("entityDocumentId", entitiesPageResult.getContent().get(0).getId());
        Assert.assertEquals("entityName", entitiesPageResult.getContent().get(0).getEntityName());
        Assert.assertEquals(0, entitiesPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertNull(entitiesPageResult.getContent().get(0).getSeverity());

        EntityQuery.EntityQueryBuilder queryBuilder1 = new EntityQuery.EntityQueryBuilder().pageNumber(0).pageSize(10).filterByEntitiesIds(Collections.singletonList("entityId1")).filterByEntitiesTypes(Collections.singletonList("entityType1"));
        entitiesPageResult = entityPersistencyService.find(queryBuilder1.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());
        Assert.assertEquals("entityId1", entitiesPageResult.getContent().get(0).getEntityId());
        Assert.assertEquals("entityDocumentId1", entitiesPageResult.getContent().get(0).getId());
        Assert.assertEquals("entityName1", entitiesPageResult.getContent().get(0).getEntityName());
        Assert.assertEquals(0, entitiesPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertNull(entitiesPageResult.getContent().get(0).getSeverity());

        entityService.updateAllEntitiesAlertData(Instant.now(), "entityType");
        entitySeverityService.updateSeverities("entityType");

        entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());
        Assert.assertEquals("entityId", entitiesPageResult.getContent().get(0).getEntityId());
        Assert.assertEquals("entityName", entitiesPageResult.getContent().get(0).getEntityName());
        Assert.assertEquals(40, entitiesPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertEquals(EntitySeverity.LOW, entitiesPageResult.getContent().get(0).getSeverity());

        entitiesPageResult = entityPersistencyService.find(queryBuilder1.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());
        Assert.assertEquals("entityId1", entitiesPageResult.getContent().get(0).getEntityId());
        Assert.assertEquals("entityDocumentId1", entitiesPageResult.getContent().get(0).getId());
        Assert.assertEquals("entityName1", entitiesPageResult.getContent().get(0).getEntityName());
        Assert.assertEquals(0, entitiesPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertNull(entitiesPageResult.getContent().get(0).getSeverity());

        entityService.updateAllEntitiesAlertData(Instant.now(), "entityType1");
        entitySeverityService.updateSeverities("entityType1");

        entitiesPageResult = entityPersistencyService.find(queryBuilder1.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());
        Assert.assertEquals("entityId1", entitiesPageResult.getContent().get(0).getEntityId());
        Assert.assertEquals("entityName1", entitiesPageResult.getContent().get(0).getEntityName());
        Assert.assertEquals(40, entitiesPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertEquals(EntitySeverity.LOW, entitiesPageResult.getContent().get(0).getSeverity());
    }

    @Test
    public void testSingleEntityScoreCalculationSomeMoreThen90Days() throws InterruptedException {
        //Generate one entity with 3 alerts
        String entityType = "entityType";
        Entity entity1 = new Entity("entityId1", "entityName1", 0d, null, null, null, EntitySeverity.CRITICAL, 0, entityType);
        entity1.setSeverity(null);
        Iterable<Entity> entityItr = entityPersistencyService.save(Collections.singletonList(entity1));
        Entity savedEntity = entityItr.iterator().next();
        String entityDocumentId = savedEntity.getId();


        List<Alert> alerts = new ArrayList<>();
        alerts.add(new Alert(entityDocumentId, "smartId", null, "entityName1", "entityName1",getMinusDay(10), getMinusDay(9), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 15D, entityType));
        alerts.add(new Alert(entityDocumentId, "smartId", null, "entityName1", "entityName1",getMinusDay(10), getMinusDay(9), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.LOW, null, 5D, entityType));
        alerts.add(new Alert(entityDocumentId, "smartId", null, "entityName1", "entityName1",getMinusDay(100), getMinusDay(99), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.LOW, null, 5D, entityType));
        alertPersistencyService.save(alerts);


        Map<EntitySeverity, PresidioRange<Double>> severityToScoreRangeMap = new LinkedHashMap<>();
        severityToScoreRangeMap.put(EntitySeverity.LOW, new PresidioRange<>(0d, 50d));
        severityToScoreRangeMap.put(EntitySeverity.MEDIUM, new PresidioRange<>(500d, 100d));
        severityToScoreRangeMap.put(EntitySeverity.HIGH, new PresidioRange<>(100d, 150d));
        severityToScoreRangeMap.put(EntitySeverity.CRITICAL, new PresidioRange<>(150d, 200d));
        entitySeveritiesRangeRepository.save(new EntitySeveritiesRangeDocument(severityToScoreRangeMap, entityType));
        entityService.updateAllEntitiesAlertData(Instant.now(), entityType);
        entitySeverityService.updateSeverities(entityType);
        Thread.sleep(1000);

        Page<Alert> alertsPageResult = alertPersistencyService.findPage(new AlertQuery.AlertQueryBuilder().filterByEndDate(getMinusDay(100).getTime()).setPageSize(10).setPageNumber(0).build());

        Entity updatedEntity = entityPersistencyService.findEntityByDocumentId(entityDocumentId);
        Assert.assertEquals("entityId1", updatedEntity.getEntityId());
        Assert.assertEquals("entityName1", updatedEntity.getEntityName());
        Assert.assertEquals(20, updatedEntity.getScore(), 0.00001);
        Assert.assertEquals(EntitySeverity.LOW, updatedEntity.getSeverity());
        Assert.assertEquals(0, alertsPageResult.getContent().get(0).getContributionToEntityScore(), 0.00001);

        EntitySeveritiesRangeDocument entitySeveritiesRangeDocument = entitySeveritiesRangeRepository.findById(EntitySeveritiesRangeDocument.getEntitySeveritiesDocIdName(entityType)).get();
        Assert.assertEquals(new Double(0), entitySeveritiesRangeDocument.getSeverityToScoreRangeMap().get(EntitySeverity.LOW).getLowerBound());
        Assert.assertEquals(new Double(20), entitySeveritiesRangeDocument.getSeverityToScoreRangeMap().get(EntitySeverity.LOW).getUpperBound());
        Assert.assertEquals(new Double(22), entitySeveritiesRangeDocument.getSeverityToScoreRangeMap().get(EntitySeverity.MEDIUM).getLowerBound());
        Assert.assertEquals(new Double(22), entitySeveritiesRangeDocument.getSeverityToScoreRangeMap().get(EntitySeverity.MEDIUM).getUpperBound());
        Assert.assertEquals(new Double(28.6), entitySeveritiesRangeDocument.getSeverityToScoreRangeMap().get(EntitySeverity.HIGH).getLowerBound());
        Assert.assertEquals(new Double(28.6), entitySeveritiesRangeDocument.getSeverityToScoreRangeMap().get(EntitySeverity.HIGH).getUpperBound());
        Assert.assertEquals(new Double(42.900000000000006), entitySeveritiesRangeDocument.getSeverityToScoreRangeMap().get(EntitySeverity.CRITICAL).getLowerBound());
        Assert.assertEquals(new Double(42.900000000000006), entitySeveritiesRangeDocument.getSeverityToScoreRangeMap().get(EntitySeverity.CRITICAL).getUpperBound());
    }

    @Test
    public void testSingleEntityScoreCalculationAllAlertsMoreThen90Days() throws InterruptedException {
        //Generate one entity with 2 critical alerts
        String entityType = "entityType";
        Entity entity1 = new Entity("entityId1", "entityName1", 0d, null, null, null, EntitySeverity.CRITICAL, 0, entityType);
        entity1.setSeverity(null);
        List<Alert> alerts = new ArrayList<>();
        alerts.add(new Alert(entity1.getId(), "smartId", null, "entityName1","entityName" , getMinusDay(105), getMinusDay(104), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 25D, entityType));
        alerts.add(new Alert(entity1.getId(), "smartId", null, "entityName1", "entityName" ,getMinusDay(100), getMinusDay(99), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.LOW, null, 10D, entityType));
        alerts.add(new Alert(entity1.getId(), "smartId", null, "entityName1", "entityName" ,getMinusDay(120), getMinusDay(119), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 25D, entityType));


        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);

        entityPersistencyService.save(entityList);
        alertPersistencyService.save(alerts);

        EntityQuery.EntityQueryBuilder queryBuilder = new EntityQuery.EntityQueryBuilder().pageNumber(0).pageSize(10).filterByEntitiesIds(Collections.singletonList("entityId1"));
        Page<Entity> entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());
        Assert.assertEquals("entityId1", entitiesPageResult.getContent().get(0).getEntityId());
        Assert.assertEquals("entityName1", entitiesPageResult.getContent().get(0).getEntityName());
        Assert.assertEquals(0, entitiesPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertNull(entitiesPageResult.getContent().get(0).getSeverity());

        entityService.updateAllEntitiesAlertData(Instant.now(), entityType);
        entitySeverityService.updateSeverities(entityType);
        Thread.sleep(1000);

        Page<Alert> alertsPageResult = alertPersistencyService.findPage(new AlertQuery.AlertQueryBuilder().setPageSize(10).setPageNumber(0).build());

        entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());
        Assert.assertEquals("entityId1", entitiesPageResult.getContent().get(0).getEntityId());
        Assert.assertEquals("entityName1", entitiesPageResult.getContent().get(0).getEntityName());
        Assert.assertEquals(0, entitiesPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertEquals(0, alertsPageResult.getContent().get(0).getContributionToEntityScore(), 0.00001);

    }

    @Test
    public void testTwoEntityTypesScoreCalculationAllAlertsMoreThen90Days() {
        //Generate one entity with 2 critical alerts
        String entityType = "entityType1";
        Entity entity1 = new Entity("entityId1", "entityName1", 0d, null, null, null, EntitySeverity.CRITICAL, 0, entityType);
        entity1.setSeverity(null);
        List<Alert> alerts = new ArrayList<>();
        alerts.add(new Alert(entity1.getId(), "smartId", null, "entityName1","entityName" , getMinusDay(105), getMinusDay(104), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 25D, entityType));
        alerts.add(new Alert(entity1.getId(), "smartId", null, "entityName1", "entityName" ,getMinusDay(100), getMinusDay(99), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.LOW, null, 10D, entityType));
        alerts.add(new Alert(entity1.getId(), "smartId", null, "entityName1", "entityName" ,getMinusDay(120), getMinusDay(119), 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 25D, entityType));

        Entity entity2 = new Entity("entityId2", "entityName2", 50d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType2");

        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);

        entityPersistencyService.save(entityList);
        alertPersistencyService.save(alerts);

        EntityQuery.EntityQueryBuilder queryBuilder = new EntityQuery.EntityQueryBuilder().pageNumber(0).pageSize(10).filterByEntitiesTypes(Collections.singletonList(entityType));
        Page<Entity> entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());
        Assert.assertEquals("entityId1", entitiesPageResult.getContent().get(0).getEntityId());
        Assert.assertEquals("entityName1", entitiesPageResult.getContent().get(0).getEntityName());
        Assert.assertEquals(0, entitiesPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertNull(entitiesPageResult.getContent().get(0).getSeverity());

        EntityQuery.EntityQueryBuilder queryBuilder2 = new EntityQuery.EntityQueryBuilder().pageNumber(0).pageSize(10).filterByEntitiesTypes(Collections.singletonList("entityType2"));
        entitiesPageResult = entityPersistencyService.find(queryBuilder2.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());
        Assert.assertEquals("entityId2", entitiesPageResult.getContent().get(0).getEntityId());
        Assert.assertEquals("entityName2", entitiesPageResult.getContent().get(0).getEntityName());
        Assert.assertEquals(50, entitiesPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertEquals(EntitySeverity.CRITICAL, entitiesPageResult.getContent().get(0).getSeverity());

        entityService.updateAllEntitiesAlertData(Instant.now(), entityType);
        entitySeverityService.updateSeverities(entityType);

        entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());
        Assert.assertEquals("entityId1", entitiesPageResult.getContent().get(0).getEntityId());
        Assert.assertEquals("entityName1", entitiesPageResult.getContent().get(0).getEntityName());
        Assert.assertEquals(0, entitiesPageResult.getContent().get(0).getScore(), 0.00001);

        entitiesPageResult = entityPersistencyService.find(queryBuilder2.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());
        Assert.assertEquals("entityId2", entitiesPageResult.getContent().get(0).getEntityId());
        Assert.assertEquals("entityName2", entitiesPageResult.getContent().get(0).getEntityName());
        Assert.assertEquals(50, entitiesPageResult.getContent().get(0).getScore(), 0.00001);
        Assert.assertEquals(EntitySeverity.CRITICAL, entitiesPageResult.getContent().get(0).getSeverity());
    }

    @Test
    public void testBulkEntityScore() {
        String entityType = "entityType";
        for (int i = 0; i < 100; i++) {
            AlertEnums.AlertSeverity[] severities = new AlertEnums.AlertSeverity[i + 1];
            for (int j = 0; j <= i; j++) {
                severities[j] = AlertEnums.AlertSeverity.HIGH;
            }
            generateEntityAndAlerts("entityId" + i, "entityHash" + i, "entityname" + i, entityType, severities);

        }

        Page<Entity> entities = entityPersistencyService.find(new EntityQuery.EntityQueryBuilder().pageSize(1).pageNumber(0).build());
        Assert.assertEquals(100, entities.getTotalElements());
        Page<Alert> alerts = alertPersistencyService.findPage(new AlertQuery.AlertQueryBuilder().setPageSize(1).setPageNumber(0).build());
        Assert.assertEquals(5050, alerts.getTotalElements());

        entityService.updateAllEntitiesAlertData(Instant.now(), entityType);

        entitySeverityService.updateSeverities(entityType);

        Entity entity0 = getEntityById("entityId0");
        Assert.assertEquals(15D, entity0.getScore(), 0.00001); //one medium alert
        Assert.assertEquals(EntitySeverity.LOW, entity0.getSeverity());

        Entity entity60 = getEntityById("entityId60");
        Assert.assertEquals(915D, entity60.getScore(), 0.00001); //61 medium alert
        Assert.assertEquals(EntitySeverity.LOW, entity60.getSeverity());


        Entity entity99 = getEntityById("entityId99");
        Assert.assertEquals(1500D, entity99.getScore(), 0.00001); //100 Medium Alerts
        Assert.assertEquals(EntitySeverity.LOW, entity99.getSeverity());
    }

    @Test
    public void testBulkEntityScoreLargeScale() {
        final int DAYS_COUNT = 110;
        final int ENTITIES_COUNT = 1000;

        List<Entity> entityList = new ArrayList<>();
        List<LocalDateTime> dates = getListOfLastXdays(DAYS_COUNT);
        String entityType = "entityType";

        //For each entity generate entity and list of alerts - 2 alerts per days
        List<Alert> alertsAllEntities = new ArrayList<>();
        for (int i = 0; i < ENTITIES_COUNT; i++) {
            Entity entity1 = new Entity("entityId" + i, "entityname" + i, 0d, null, null, null, EntitySeverity.CRITICAL, 0, entityType);
            entity1.setId("entityDocumentId" + i);
            entity1.setSeverity(null);
            //For each day generate to alerts
            for (LocalDateTime day : dates) {
                Date alert1StartTime = new Date(Date.from(day.plusHours(3).atZone(ZoneOffset.UTC).toInstant()).getTime());
                Date alert1EndTime = new Date(Date.from(day.plusHours(4).atZone(ZoneOffset.UTC).toInstant()).getTime());

                Date alert2StartTime = Date.from(day.plusHours(5).atZone(ZoneOffset.UTC).toInstant());
                Date alert2EndTime = Date.from(day.plusHours(6).atZone(ZoneOffset.UTC).toInstant());
                //Alerts per entity per day
                alertsAllEntities.add(new Alert("entityDocumentId" + i, "smartId", null, "entityName" + i, "entityName" + i,alert1StartTime, alert1EndTime, 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.CRITICAL, null, 30D, entityType));
                alertsAllEntities.add(new Alert("entityDocumentId" + i, "smartId", null, "entityName" + i, "entityName" + i,alert2StartTime, alert2EndTime, 100, 0, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 25D, entityType));
            }


            entityList.add(entity1);
        }
        //Save all the entity's alerts
        alertPersistencyService.save(alertsAllEntities);

        //Save all the entities
        entityPersistencyService.save(entityList);

        System.out.println("Finish Inserting data " + Instant.now().toString());
        long timeBefore = System.currentTimeMillis();
        entityService.updateAllEntitiesAlertData(Instant.now(), entityType);

        entitySeverityService.updateSeverities(entityType);
        long timeAfter = System.currentTimeMillis();
        long seconds = (timeAfter - timeBefore) / 1000;
        System.out.println("Total time in seconds: " + seconds);
        Assert.assertTrue(seconds < 120);

    }

    @Test
    public void calculateScorePercentilesTwice_shouldCreatePercentilesDocOnce() {
        //calculate percentiles with 0 entities (all entities should get low severity)
        String entityType = "entityType";
        entitySeverityService.updateSeverities(entityType);
        Iterable<EntitySeveritiesRangeDocument> all = entitySeveritiesRangeRepository.findAll();
        Assert.assertEquals(1, ((ScrolledPage<EntitySeveritiesRangeDocument>) all).getNumberOfElements());

        //creating new entities
        for (int i = 0; i < 100; i++) {
            AlertEnums.AlertSeverity[] severities = new AlertEnums.AlertSeverity[i + 1];
            for (int j = 0; j <= i; j++) {
                severities[j] = AlertEnums.AlertSeverity.HIGH;
            }
            generateEntityAndAlerts("entityId" + i, "entityHash" + i, "entityname" + i, entityType, severities);
        }

        //re-calculate percentiles with new entities
        entitySeverityService.updateSeverities(entityType);

        EntitySeverityServiceImpl.EntityScoreToSeverity severitiesMap = entitySeverityService.getSeveritiesMap(false, entityType);
        all = entitySeveritiesRangeRepository.findAll();
        Assert.assertEquals(1, ((ScrolledPage<EntitySeveritiesRangeDocument>) all).getNumberOfElements());

    }

    private List<LocalDateTime> getListOfLastXdays(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startTime = endDate.minusDays(days);
        List<LocalDateTime> dates = new ArrayList<>();
        for (LocalDate d = startTime; !d.isAfter(endDate); d = d.plusDays(1)) {
            LocalDateTime time = d.atStartOfDay();
            dates.add(time);
        }
        return dates;
    }

    private Entity getEntityById(String entityId) {
        Page<Entity> entities = entityPersistencyService.find(new EntityQuery.EntityQueryBuilder().pageSize(1).pageNumber(0).filterByEntitiesIds(Collections.singletonList(entityId)).build());
        Assert.assertEquals(1, entities.getTotalElements());

        Entity entity = entities.getContent().get(0);

        Assert.assertEquals(entityId, entity.getEntityId());
        return entity;
    }

    private void generateEntityAndAlerts(String entityId, String entityDocumentId, String entityName, String entityType, AlertEnums.AlertSeverity... severities) {
        Entity entity1 = new Entity(entityId, entityName, 0d, null, null, null, EntitySeverity.CRITICAL, 0, entityType);
        entity1.setId(entityDocumentId);
        entity1.setSeverity(null);
        List<Alert> alerts = new ArrayList<>();

        for (AlertEnums.AlertSeverity severity : severities) {
            alerts.add(new Alert(entityDocumentId, "smartId", null, entityName, entityName, getMinusDay(10), getMinusDay(9), 100, 0, AlertEnums.AlertTimeframe.HOURLY, severity, null, alertSeverityService.getEntityScoreContributionFromSeverity(severity), entityType));
        }

        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);

        entityPersistencyService.save(entityList);
        alertPersistencyService.save(alerts);
    }

    private Date getMinusDay(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -1 * days);
        return new Date(c.getTime().getTime());

    }

}