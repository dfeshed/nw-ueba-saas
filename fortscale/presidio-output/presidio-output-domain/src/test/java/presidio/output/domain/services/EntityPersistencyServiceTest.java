package presidio.output.domain.services;

import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import org.assertj.core.util.Lists;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.AbstractElasticDocument;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntityQuery;
import presidio.output.domain.records.entity.EntitySeverity;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.output.domain.spring.TestConfig;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig.class, TestConfig.class, ElasticsearchTestConfig.class})
public class EntityPersistencyServiceTest{

    @Autowired
    private EntityPersistencyService entityPersistencyService;

    private List<String> classifications1 = new ArrayList<>(Arrays.asList("a", "b", "c"));
    private List<String> classifications2 = new ArrayList<>(Arrays.asList("b"));
    private List<String> classifications3 = new ArrayList<>(Arrays.asList("a"));
    private List<String> classifications4 = new ArrayList<>(Arrays.asList("d"));
    private Entity entity1 = generateEntity(classifications1, "entity1", "entityId1", 50d);
    private  Entity entity2 = generateEntity(classifications2, "entity2", "entityId2", 60d);
    private Entity entity3 = generateEntity(classifications3, "entity3", "entityId3", 70d);
    private Entity entity4 = generateEntity(classifications4, "entity4", "entityId4", 80d);
    private  Entity entity5 = generateEntity(classifications3, "entity5", "entityId5", 70d);
    private Entity entity6 = generateEntity(classifications3, "fretext", "entityId6", 70d);
    private  Entity entity7 = generateEntity(classifications3, "free", "entityId7", 70d);
    private Entity entity8 = generateEntity(classifications3, "text", "entityId8", 70d);
    private Entity entity9 = generateEntity(classifications3, "freetex", "entityId8", 70d);


    @Autowired
    public Client client;

    @After
    public void cleanTestData() {
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .source(AbstractElasticDocument.INDEX_NAME + "-" + Entity.DOC_TYPE)
                .get();
    }

    private Entity generateEntity(List<String> classifications, String entityName, String entityId, double score) {
        List<String> indicators = Arrays.asList(new String("indicator"));
        return new Entity(entityId, entityName, score, classifications, indicators, null, EntitySeverity.CRITICAL, 0, "entityType");
    }

    @Test
    public void testSave() {
        Entity entity = entity1;
        Entity createdEntity = entityPersistencyService.save(entity1);

        assertNotNull(createdEntity.getEntityId());
        assertEquals(createdEntity.getEntityId(), entity.getEntityId());
        assertEquals(createdEntity.getEntityName(), entity.getEntityName());
        assertTrue(createdEntity.getScore() == entity.getScore());
        assertEquals(createdEntity.getAlertClassifications().size(), entity.getAlertClassifications().size());
        assertEquals(createdEntity.getIndicators().size(), entity.getIndicators().size());
    }

    @Test
    public void testSaveBulk() {
        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);

        Iterable<Entity> createdEntities = entityPersistencyService.save(entityList);
        assertThat(Lists.newArrayList(createdEntities).size(), is(2));
    }


    @Test
    public void testFindOne() {
        Entity entity = generateEntity(classifications1, "entity1", "entityId1", 50d);
        entityPersistencyService.save(entity);

        Date createdByBeforeFind = entity.getCreatedDate();
        Entity foundEntity = entityPersistencyService.findEntityByDocumentId(entity.getId());
        Date createdByAfterFind = foundEntity.getCreatedDate();

        assertNotNull(foundEntity.getEntityId());
        assertEquals(createdByBeforeFind, createdByAfterFind);
        assertEquals(foundEntity.getEntityId(), entity.getEntityId());
        assertEquals(foundEntity.getEntityName(), entity.getEntityName());
        assertTrue(foundEntity.getScore() == entity.getScore());
        assertEquals(foundEntity.getAlertClassifications().size(), entity.getAlertClassifications().size());
        assertEquals(foundEntity.getIndicators().size(), entity.getIndicators().size());

    }

    @Test
    public void testFindEntitiesByUpdatedDateAndEntityType() {
        Entity entity = generateEntity(classifications1, "entity1", "entityId1", 50d);
        Instant end = Instant.now();
        Instant start = end.minus(1, ChronoUnit.HOURS);
        entity.setLastUpdateLogicalStartDate(Date.from(start));
        entity.setLastUpdateLogicalEndDate(Date.from(end));
        entityPersistencyService.save(entity);

        Stream<Entity> entities = entityPersistencyService.findEntitiesByUpdatedDateAndEntityType(start, end, "entityType");

        assertNotNull(entities);
        assertEquals(1, entities.count());
    }

    @Test
    public void testUpdatedBY() throws InterruptedException {
        Thread.currentThread().setName("TEST");
        Entity entity = generateEntity(classifications1, "entity1", "entityId1", 50d);
        String created = entity.getUpdatedBy();
        entityPersistencyService.save(entity);
        Entity foundEntity = entityPersistencyService.findEntityByDocumentId(entity.getId());
        String createdBy = foundEntity.getUpdatedBy();
        Thread.sleep(1000);
        entityPersistencyService.save(foundEntity);
        foundEntity = entityPersistencyService.findEntityByDocumentId(entity.getId());
        String updatedByAgain = foundEntity.getUpdatedBy();
        Thread.currentThread().setName("TEST2");
        Thread.sleep(1000);
        entityPersistencyService.save(foundEntity);
        foundEntity = entityPersistencyService.findEntityByDocumentId(entity.getId());
        String updatedByAgain2 = foundEntity.getUpdatedBy();

        assertNotNull(foundEntity.getEntityId());
        assertEquals(foundEntity.getEntityId(), entity.getEntityId());
        assertNotEquals(null, createdBy);
        assertEquals(createdBy, updatedByAgain);
        assertNotEquals(createdBy, updatedByAgain2);
    }

    @Test
    public void testFreeTextWithoutIsPrefixEnabled() {
        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity6);
        entityList.add(entity7);
        entityList.add(entity8);
        entityPersistencyService.save(entityList);

        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("free")
                        .build();
        Page<Entity> foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(1L));

        entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("fre")
                        .build();
        foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(0L));

        entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("text")
                        .build();
        foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(1L));
    }

    @Test
    public void testFreeTextWithIsPrefixEnabled() {
        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity6);
        entityList.add(entity7);
        entityList.add(entity8);
        entityList.add(entity9);
        entityPersistencyService.save(entityList);

        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("free")
                        .filterByEntityNameWithPrefix(true)
                        .build();
        Page<Entity> foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(2L));

        entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("fre")
                        .filterByEntityNameWithPrefix(true)
                        .build();
        foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(3L));

        entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("text")
                        .filterByEntityNameWithPrefix(true)
                        .build();
        foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(1L));
    }

    @Test
    public void testFreeTextWhitentityName() {
        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity6);
        entityList.add(entity7);
        entityList.add(entity8);
        entityPersistencyService.save(entityList);
        Page<Entity> foundEntities = null;

        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("free")
                        .filterByEntityNameWithPrefix(true)
                        .filterByEntityName("free")
                        .build();
        foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(1L));

        entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("free")
                        .filterByEntityName("free")
                        .build();
        foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(1L));

        entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("fre")
                        .filterByEntityNameWithPrefix(true)
                        .filterByEntityName("fre")
                        .build();
        foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(2L));

        entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("fre")
                        .filterByEntityName("fre")
                        .build();
        foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(0L));
    }

    @Test
    public void testFindAll() {
        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);

        entityPersistencyService.save(entityList);

        Iterable<Entity> foundEntities = entityPersistencyService.findAll();
        assertThat(Lists.newArrayList(foundEntities).size(), is(2));
    }

    @Test
    public void testFindByQueryFilterByClassificationsAndSortByScoreAscending() {

        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);
        entityList.add(entity3);
        entityList.add(entity4);
        entityList.add(entity5);
        entityPersistencyService.save(entityList);

        List<String> classificationFilter = new ArrayList<String>();
        classificationFilter.add("a");

        List<String> sort = new ArrayList<>();
        sort.add(Entity.SCORE_FIELD_NAME);
        sort.add(Entity.ENTITY_ID_FIELD_NAME);
        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder()
                        .filterByAlertClassifications(classificationFilter)
                        .sort(new Sort(new Sort.Order(Entity.SCORE_FIELD_NAME)))
                        .build();

        Page<Entity> foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(3L));
        assertTrue(foundEntities.iterator().next().getScore() == 50d);
    }

    @Test
    public void testFindByQueryFilterByIndicators() {
        List<String> indicators1 = Arrays.asList("indicatorName1");
        List<String> indicators2 = Arrays.asList("indicatorName1", "indicatorName2");

        entity1.setIndicators(indicators1);
        entity2.setIndicators(indicators2);
        List<Entity> entityList = Arrays.asList(entity1, entity2);
        entityPersistencyService.save(entityList);

        List<String> indicatorFilter = new ArrayList<String>();
        indicatorFilter.add("indicator");

        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder()
                        .filterByIndicators(indicators1)
                        .build();

        Page<Entity> foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(2L));

        EntityQuery entityQuery2 =
                new EntityQuery.EntityQueryBuilder()
                        .filterByIndicators(Arrays.asList("indicatorName2"))
                        .build();

        Page<Entity> foundEntities2 = entityPersistencyService.find(entityQuery2);
        assertThat(foundEntities2.getTotalElements(), is(1L));
    }

    @Test
    public void testFindByListOfIds() {

        Entity entity1 = new Entity("entityId1", "entityName", 0d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity2 = new Entity("entityId2", "entityName", 0d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity3 = new Entity("entityId3", "entityName", 0d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity4 = new Entity("entityId4", "entityName", 0d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType");

        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);
        entityList.add(entity3);
        entityList.add(entity4);
        Iterable<Entity> createdEntities = entityPersistencyService.save(entityList);
        List<String> EntitiesIds = new ArrayList<>();
        EntitiesIds.add(entity1.getEntityId());
        EntitiesIds.add(entity2.getEntityId());
        EntitiesIds.add("entityId5");

        EntityQuery.EntityQueryBuilder queryBuilder = new EntityQuery.EntityQueryBuilder().pageNumber(0).pageSize(10).filterByEntitiesIds(EntitiesIds);
        Page<Entity> entitysPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(2, entitysPageResult.getContent().size());
    }

    @Test
    public void testFindByListOfTypes() {

        Entity entity1 = new Entity("entityId1", "entityName", 0d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity2 = new Entity("entityId2", "entityName", 0d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity3 = new Entity("entityId3", "entityName", 0d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity4 = new Entity("entityId4", "entityName", 0d, null, null, null, EntitySeverity.CRITICAL, 0, "ja3");

        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);
        entityList.add(entity3);
        entityList.add(entity4);
        Iterable<Entity> createdEntities = entityPersistencyService.save(entityList);
        List<String> entitiesTypes = new ArrayList<>();
        entitiesTypes.add(entity1.getEntityType());

        EntityQuery.EntityQueryBuilder queryBuilder = new EntityQuery.EntityQueryBuilder().pageNumber(0).pageSize(10).filterByEntitiesTypes(entitiesTypes);
        Page<Entity> entitysPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(3, entitysPageResult.getContent().size());
    }

    @Test
    public void testFindByEntityScore() {

        List<String> tags = new ArrayList<>();
        tags.add("ADMIN");

        List<String> classification = new ArrayList<>();
        classification.add("a");
        Entity entity1 = new Entity("entityId1", "entityName", 5d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity2 = new Entity("entityId2", "entityName", 10d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity3 = new Entity("entityId3", "entityName", 20d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity4 = new Entity("entityId4", "entityName", 21d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType");


        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);
        entityList.add(entity3);
        entityList.add(entity4);
        Iterable<Entity> createdEntities = entityPersistencyService.save(entityList);


        EntityQuery.EntityQueryBuilder queryBuilder = new EntityQuery.EntityQueryBuilder().pageNumber(0).pageSize(10).minScore(10).maxScore(20);
        Page<Entity> entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(2, entitiesPageResult.getContent().size());
    }

    @Test
    public void testFindByEntityId() {
        Entity entity1 = new Entity("entityId1-1234-5678", "entityName", 5d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity2 = new Entity("entityId1@somecompany.com", "entityName", 20d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity3 = new Entity("entityId1", "entityName", 21d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType");


        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);
        entityList.add(entity3);
        Iterable<Entity> createdEntities = entityPersistencyService.save(entityList);


        EntityQuery.EntityQueryBuilder queryBuilder = new EntityQuery.EntityQueryBuilder().filterByEntitiesIds(Arrays.asList(new String[]{"entityId1"}));
        Page<Entity> entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());


        queryBuilder = new EntityQuery.EntityQueryBuilder().filterByEntitiesIds(Arrays.asList(new String[]{"entityId1-1234-5678"}));
        entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());

        queryBuilder = new EntityQuery.EntityQueryBuilder().filterByEntitiesIds(Arrays.asList(new String[]{"1234-5678-entityId1"}));
        entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(0, entitiesPageResult.getContent().size());

        queryBuilder = new EntityQuery.EntityQueryBuilder().filterByEntitiesIds(Arrays.asList(new String[]{"1234"}));
        entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(0, entitiesPageResult.getContent().size());

        queryBuilder = new EntityQuery.EntityQueryBuilder().filterByEntitiesIds(Arrays.asList(new String[]{"entityId1@somecompany.com"}));
        entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());

        queryBuilder = new EntityQuery.EntityQueryBuilder().filterByEntitiesIds(Arrays.asList(new String[]{"somecompany.com@entityId1"}));
        entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(0, entitiesPageResult.getContent().size());
    }

    @Test
    public void testFindByIsEntityAdmin_True() {
        List<String> tags = new ArrayList<>();
        tags.add("ADMIN");
        entity1.setTags(tags);
        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);
        entityPersistencyService.save(entityList);

        List<String> sort = new ArrayList<>();
        sort.add(Entity.SCORE_FIELD_NAME);
        sort.add(Entity.ENTITY_ID_FIELD_NAME);
        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder()
                        .filterByEntityTags(tags)
                        .build();

        Page<Entity> foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(1L));
        Entity foundEntity = foundEntities.iterator().next();
        assertNotNull(foundEntity.getTags());
        assertEquals(1, foundEntity.getTags().size());
        assertEquals(tags, foundEntity.getTags());
    }

    @Test
    public void testFindByQueryWithSeverityAggregation() {

        List<String> classification = new ArrayList<>();
        classification.add("a");
        Entity entity1 = new Entity("entityId1", "entityName", 5d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity2 = new Entity("entityId2", "entityName", 10d, null, null, null, EntitySeverity.MEDIUM, 0, "entityType");
        Entity entity3 = new Entity("entityId3", "entityName", 20d, null, null, null, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity4 = new Entity("entityId4", "entityName", 21d, null, null, null, EntitySeverity.MEDIUM, 0, "entityType");


        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);
        entityList.add(entity3);
        entityList.add(entity4);
        entityPersistencyService.save(entityList);

        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(Entity.SEVERITY_FIELD_NAME);

        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder()
                        .aggregateByFields(aggregationFields)
                        .build();

        Page<Entity> result = entityPersistencyService.find(entityQuery);
        Map<String, Aggregation> stringAggregationMap = ((AggregatedPageImpl<Entity>) result).getAggregations().asMap();
        StringTerms severityAgg = (StringTerms) stringAggregationMap.get(Entity.SEVERITY_FIELD_NAME);
        List<Terms.Bucket> buckets = severityAgg.getBuckets();

        assertEquals(buckets.size(), 2L); //two buckets- HIGH and MEDIUM
        assertEquals(severityAgg.getBucketByKey("CRITICAL").getDocCount(), 2L);
        assertEquals(severityAgg.getBucketByKey("MEDIUM").getDocCount(), 2L);
    }

    @Test
    public void testFindByQueryWithTagsAggregation() {

        List<String> tags1 = new ArrayList<>(Arrays.asList("admin", "watch"));
        List<String> tags2 = new ArrayList<>(Arrays.asList("admin"));

        Entity entity1 = new Entity("entityId1", "entityName", 5d, null, null, tags1, EntitySeverity.CRITICAL, 0 , "entityType");
        Entity entity2 = new Entity("entityId2", "entityName", 10d, null, null, tags2, EntitySeverity.MEDIUM, 0, "entityType");
        Entity entity3 = new Entity("entityId3", "entityName", 20d, null, null, tags1, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity4 = new Entity("entityId4", "entityName", 21d, null, null, null, EntitySeverity.MEDIUM, 0, "entityType");


        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);
        entityList.add(entity3);
        entityList.add(entity4);
        entityPersistencyService.save(entityList);

        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(Entity.TAGS_FIELD_NAME);

        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder()
                        .aggregateByFields(aggregationFields)
                        .build();

        Page<Entity> result = entityPersistencyService.find(entityQuery);
        Map<String, Aggregation> stringAggregationMap = ((AggregatedPageImpl<Entity>) result).getAggregations().asMap();
        StringTerms severityAgg = (StringTerms) stringAggregationMap.get(Entity.TAGS_FIELD_NAME);
        List<Terms.Bucket> buckets = severityAgg.getBuckets();

        assertEquals(buckets.size(), 2L); //two buckets- admin and watch
        assertEquals(severityAgg.getBucketByKey("admin").getDocCount(), 3L);
        assertEquals(severityAgg.getBucketByKey("watch").getDocCount(), 2L);
    }

    @Test
    public void testFindByQueryWithClassificationsAggregation() {

        List<String> tags1 = Arrays.asList("admin", "watch");
        List<String> tags2 = Arrays.asList("admin");


        List<String> classificationA = Arrays.asList("a");
        List<String> classificationB = Arrays.asList("a", "b");
        List<String> classificationC = Arrays.asList("a", "b", "c");
        Entity entity1 = new Entity("entityId1", "entityName", 5d, classificationA, null, tags1, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity2 = new Entity("entityId2", "entityName", 10d, classificationB, null, tags2, EntitySeverity.MEDIUM, 0, "entityType");
        Entity entity3 = new Entity("entityId3", "entityName", 20d, classificationC, null, tags1, EntitySeverity.CRITICAL, 0, "entityType");


        List<Entity> entityList = Arrays.asList(entity1, entity2, entity3);
        entityPersistencyService.save(entityList);

        List<String> aggregationFields = Arrays.asList(Entity.ALERT_CLASSIFICATIONS_FIELD_NAME);

        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder()
                        .aggregateByFields(aggregationFields)
                        .build();

        Page<Entity> result = entityPersistencyService.find(entityQuery);
        Map<String, Aggregation> stringAggregationMap = ((AggregatedPageImpl<Entity>) result).getAggregations().asMap();
        StringTerms severityAgg = (StringTerms) stringAggregationMap.get(Entity.ALERT_CLASSIFICATIONS_FIELD_NAME);
        List<Terms.Bucket> buckets = severityAgg.getBuckets();

        assertEquals(buckets.size(), 3L); //two buckets- admin and watch
        assertEquals(severityAgg.getBucketByKey("a").getDocCount(), 3L);
        assertEquals(severityAgg.getBucketByKey("b").getDocCount(), 2L);
        assertEquals(severityAgg.getBucketByKey("c").getDocCount(), 1L);
    }

    @Test
    public void testFindByQueryFilterByClassificationsAndAggregateBySeverity() {

        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);
        entityList.add(entity3);
        entityList.add(entity4);
        entityList.add(entity5);
        entityPersistencyService.save(entityList);

        List<String> classificationFilter = new ArrayList<String>();
        classificationFilter.add("a");

        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder()
                        .filterByAlertClassifications(classificationFilter)
                        .aggregateByFields(Arrays.asList(Entity.SEVERITY_FIELD_NAME))
                        .build();

        Page<Entity> foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(3L));

        Map<String, Aggregation> stringAggregationMap = ((AggregatedPageImpl<Entity>) foundEntities).getAggregations().asMap();
        StringTerms severityAgg = (StringTerms) stringAggregationMap.get(Entity.SEVERITY_FIELD_NAME);
        List<Terms.Bucket> buckets = severityAgg.getBuckets();

        assertEquals(buckets.size(), 1L); //one bucket- CRITICAL
        assertEquals(3L, severityAgg.getBucketByKey("CRITICAL").getDocCount());
    }

    @Test
    public void testFindByQueryWithIndicatorsAggregation() {

        List<String> tags1 = Arrays.asList("admin", "watch");
        List<String> tags2 = Arrays.asList("admin");


        List<String> indicatorsA = Arrays.asList("a");
        List<String> indicatorsB = Arrays.asList("a", "b");
        List<String> indicatorsC = Arrays.asList("a", "b", "c");
        Entity entity1 = new Entity("entityId1", "entityName", 5d, null, indicatorsA, tags1, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity2 = new Entity("entityId2", "entityName", 10d, null, indicatorsB, tags2, EntitySeverity.MEDIUM, 0, "entityType");
        Entity entity3 = new Entity("entityId3", "entityName", 20d, null, indicatorsC, tags1, EntitySeverity.CRITICAL, 0, "entityType");


        List<Entity> entityList = Arrays.asList(entity1, entity2, entity3);
        entityPersistencyService.save(entityList);

        List<String> aggregationFields = Arrays.asList(Entity.INDICATORS_FIELD_NAME);

        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder()
                        .aggregateByFields(aggregationFields)
                        .build();

        Page<Entity> result = entityPersistencyService.find(entityQuery);
        Map<String, Aggregation> stringAggregationMap = ((AggregatedPageImpl<Entity>) result).getAggregations().asMap();
        StringTerms severityAgg = (StringTerms) stringAggregationMap.get(Entity.INDICATORS_FIELD_NAME);
        List<Terms.Bucket> buckets = severityAgg.getBuckets();

        assertEquals(buckets.size(), 3L); //two buckets- admin and watch
        assertEquals(severityAgg.getBucketByKey("a").getDocCount(), 3L);
        assertEquals(severityAgg.getBucketByKey("b").getDocCount(), 2L);
        assertEquals(severityAgg.getBucketByKey("c").getDocCount(), 1L);
    }

    @Test
    public void testSortByEntityName() {

        List<String> tags = Arrays.asList("admin");
        List<String> indicators = Arrays.asList("a");
        Entity entity1 = new Entity("entityId1", "w_entityName", 5d, null, indicators, tags, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity2 = new Entity("entityId2", "C_entityName", 10d, null, indicators, tags, EntitySeverity.MEDIUM, 0, "entityType");
        Entity entity3 = new Entity("entityId3", "b_entityName", 20d, null, indicators, tags, EntitySeverity.CRITICAL, 0, "entityType");


        List<Entity> entityList = Arrays.asList(entity1, entity2, entity3);
        entityPersistencyService.save(entityList);


        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder()
                        .sort(new Sort(Sort.Direction.ASC, Entity.ENTITY_NAME_FIELD_NAME + ".keyword"))
                        .build();

        Page<Entity> result = entityPersistencyService.find(entityQuery);
        assertEquals(3L, result.getContent().size());
        Iterator<Entity> iterator = result.iterator();
        Assert.assertEquals("b_entityName", iterator.next().getEntityName());
        Assert.assertEquals("C_entityName", iterator.next().getEntityName());
        Assert.assertEquals("w_entityName", iterator.next().getEntityName());
    }


    @Test
    public void testSortByAlertsNumber() {

        List<String> tags = Arrays.asList("admin");
        List<String> indicators = Arrays.asList("a");
        Entity entity1 = new Entity("entityId1", "W_entityName", 5d, null, indicators, tags, EntitySeverity.CRITICAL, 1, "entityType");
        Entity entity2 = new Entity("entityId2", "C_entityName", 10d, null, indicators, tags, EntitySeverity.MEDIUM, 2, "entityType");
        Entity entity3 = new Entity("entityId3", "B_entityName", 20d, null, indicators, tags, EntitySeverity.CRITICAL, 3, "entityType");


        List<Entity> entityList = Arrays.asList(entity1, entity2, entity3);
        entityPersistencyService.save(entityList);


        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder()
                        .sort(new Sort(Sort.Direction.DESC, Entity.ALERTS_COUNT_FIELD_NAME))
                        .build();

        Page<Entity> result = entityPersistencyService.find(entityQuery);
        assertEquals(3L, result.getContent().size());
        Iterator<Entity> iterator = result.iterator();
        Assert.assertEquals("B_entityName", iterator.next().getEntityName());
        Assert.assertEquals("C_entityName", iterator.next().getEntityName());
        Assert.assertEquals("W_entityName", iterator.next().getEntityName());

        entityQuery =
                new EntityQuery.EntityQueryBuilder()
                        .sort(new Sort(Sort.Direction.ASC, Entity.ALERTS_COUNT_FIELD_NAME))
                        .build();

        result = entityPersistencyService.find(entityQuery);
        assertEquals(3L, result.getContent().size());
        iterator = result.iterator();
        Assert.assertEquals("W_entityName", iterator.next().getEntityName());
        Assert.assertEquals("C_entityName", iterator.next().getEntityName());
        Assert.assertEquals("B_entityName", iterator.next().getEntityName());
    }

    @Test
    public void testFindByEntityNameCaseInsensitive() {

        List<String> tags = Arrays.asList("admin");
        List<String> indicators = Arrays.asList("a");
        Entity entity1 = new Entity("entityId1", "W_entityName", 5d, null, indicators, tags, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity2 = new Entity("entityId2", "C_entityName", 10d, null, indicators, tags, EntitySeverity.MEDIUM, 0, "entityType");
        Entity entity3 = new Entity("entityId3", "B_entityName", 20d, null, indicators, tags, EntitySeverity.CRITICAL, 0, "entityType");


        List<Entity> entityList = Arrays.asList(entity1, entity2, entity3);
        entityPersistencyService.save(entityList);


        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByEntityName("w_entityName")
                        .build();

        Page<Entity> result = entityPersistencyService.find(entityQuery);
        assertEquals(1L, result.getContent().size());
        Iterator<Entity> iterator = result.iterator();
        Assert.assertEquals("W_entityName", iterator.next().getEntityName());
    }


    @Test
    public void testFindByEntityNameContains() {

        List<String> tags = Arrays.asList("admin");
        List<String> indicators = Arrays.asList("a");
        Entity entity1 = new Entity("entityId1", "Donald Duck", 5d, null, indicators, tags, EntitySeverity.CRITICAL, 0, "entityType");
        Entity entity2 = new Entity("entityId2", "Mini Mous", 10d, null, indicators, tags, EntitySeverity.MEDIUM, 0, "entityType");
        Entity entity3 = new Entity("entityId3", "Donkey", 20d, null, indicators, tags, EntitySeverity.CRITICAL, 0, "entityType");


        List<Entity> entityList = Arrays.asList(entity1, entity2, entity3);
        entityPersistencyService.save(entityList);


        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByEntityName("duck")
                        .build();

        Page<Entity> result = entityPersistencyService.find(entityQuery);
        assertEquals(1L, result.getContent().size());
        Iterator<Entity> iterator = result.iterator();
        Assert.assertEquals("Donald Duck", iterator.next().getEntityName());
    }
}
