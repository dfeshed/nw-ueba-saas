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

import java.util.*;

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
    private Entity entity1 = generateEntity(classifications1, "user1", "userId1", "user1", 50d);
    private  Entity entity2 = generateEntity(classifications2, "user2", "userId2", "user2", 60d);
    private Entity entity3 = generateEntity(classifications3, "user3", "userId3", "user3", 70d);
    private Entity entity4 = generateEntity(classifications4, "user4", "userId4", "user4", 80d);
    private  Entity entity5 = generateEntity(classifications3, "user5", "userId5", "user4", 70d);
    private Entity entity6 = generateEntity(classifications3, "fretext", "userId6", "free", 70d);
    private  Entity entity7 = generateEntity(classifications3, "free", "userId7", "text", 70d);
    private Entity entity8 = generateEntity(classifications3, "text", "userId8", "freetex", 70d);

    @Autowired
    public Client client;

    @After
    public void cleanTestData() {
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .source(AbstractElasticDocument.INDEX_NAME + "-" + Entity.DOC_TYPE)
                .get();
    }

    private Entity generateEntity(List<String> classifications, String userName, String userId, String displayName, double score) {
        List<String> indicators = Arrays.asList(new String("indicator"));
        return new Entity(userId, userName, displayName, score, classifications, indicators, null, EntitySeverity.CRITICAL, 0, "user");
    }

    @Test
    public void testSave() {
        Entity entity = entity1;
        Entity createdEntity = entityPersistencyService.save(entity1);

        assertNotNull(createdEntity.getId());
        assertEquals(createdEntity.getId(), entity.getId());
        assertEquals(createdEntity.getUserName(), entity.getUserName());
        assertEquals(createdEntity.getUserDisplayName(), entity.getUserDisplayName());
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
        Entity entity = generateEntity(classifications1, "user1", "userId1", "user1", 50d);
        entityPersistencyService.save(entity);

        Date createdByBeforeFind = entity.getCreatedDate();
        Entity foundEntity = entityPersistencyService.findEntityById(entity.getId());
        Date createdByAfterFind = foundEntity.getCreatedDate();

        assertNotNull(foundEntity.getId());
        assertEquals(createdByBeforeFind, createdByAfterFind);
        assertEquals(foundEntity.getId(), entity.getId());
        assertEquals(foundEntity.getUserName(), entity.getUserName());
        assertEquals(foundEntity.getUserDisplayName(), entity.getUserDisplayName());
        assertTrue(foundEntity.getScore() == entity.getScore());
        assertEquals(foundEntity.getAlertClassifications().size(), entity.getAlertClassifications().size());
        assertEquals(foundEntity.getIndicators().size(), entity.getIndicators().size());

    }

    @Test
    public void testUpdatedBY() throws InterruptedException {
        Thread.currentThread().setName("TEST");
        Entity entity = generateEntity(classifications1, "user1", "userId1", "user1", 50d);
        String created = entity.getUpdatedBy();
        entityPersistencyService.save(entity);
        Entity foundEntity = entityPersistencyService.findEntityById(entity.getId());
        String createdBy = foundEntity.getUpdatedBy();
        Thread.sleep(1000);
        entityPersistencyService.save(foundEntity);
        foundEntity = entityPersistencyService.findEntityById(entity.getId());
        String updatedByAgain = foundEntity.getUpdatedBy();
        Thread.currentThread().setName("TEST2");
        Thread.sleep(1000);
        entityPersistencyService.save(foundEntity);
        foundEntity = entityPersistencyService.findEntityById(entity.getId());
        String updatedByAgain2 = foundEntity.getUpdatedBy();

        assertNotNull(foundEntity.getId());
        assertEquals(foundEntity.getId(), entity.getId());
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
        assertThat(foundEntities.getTotalElements(), is(2L));

        entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("fre")
                        .build();
        foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(0L));

        entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("text")
                        .build();
        foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(2L));
    }

    @Test
    public void testFreeTextWithIsPrefixEnabled() {
        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity6);
        entityList.add(entity7);
        entityList.add(entity8);
        entityPersistencyService.save(entityList);

        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("free")
                        .filterByUserNameWithPrefix(true)
                        .build();
        Page<Entity> foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(3L));

        entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("fre")
                        .filterByUserNameWithPrefix(true)
                        .build();
        foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(3L));

        entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("text")
                        .filterByUserNameWithPrefix(true)
                        .build();
        foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(2L));
    }

    @Test
    public void testFreeTextWhitUserName() {
        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity6);
        entityList.add(entity7);
        entityList.add(entity8);
        entityPersistencyService.save(entityList);
        Page<Entity> foundEntities = null;

        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("free")
                        .filterByUserNameWithPrefix(true)
                        .filterByUserName("free")
                        .build();
        foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(1L));

        entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("free")
                        .filterByUserName("free")
                        .build();
        foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(1L));

        entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("fre")
                        .filterByUserNameWithPrefix(true)
                        .filterByUserName("fre")
                        .build();
        foundEntities = entityPersistencyService.find(entityQuery);
        assertThat(foundEntities.getTotalElements(), is(2L));

        entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByFreeText("fre")
                        .filterByUserName("fre")
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
        sort.add(Entity.USER_ID_FIELD_NAME);
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

        Entity entity1 = new Entity("userId1", "userName", "displayName", 0d, null, null, null, EntitySeverity.CRITICAL, 0, "user");
        Entity entity2 = new Entity("userId2", "userName", "displayName", 0d, null, null, null, EntitySeverity.CRITICAL, 0, "user");
        Entity entity3 = new Entity("userId3", "userName", "displayName", 0d, null, null, null, EntitySeverity.CRITICAL, 0, "user");
        Entity entity4 = new Entity("userId4", "userName", "displayName", 0d, null, null, null, EntitySeverity.CRITICAL, 0, "user");

        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);
        entityList.add(entity3);
        entityList.add(entity4);
        Iterable<Entity> createdEntities = entityPersistencyService.save(entityList);
        List<String> EntitiesIds = new ArrayList<>();
        EntitiesIds.add(entity1.getUserId());
        EntitiesIds.add(entity2.getUserId());
        EntitiesIds.add("userId5");

        EntityQuery.EntityQueryBuilder queryBuilder = new EntityQuery.EntityQueryBuilder().pageNumber(0).pageSize(10).filterByUsersIds(EntitiesIds);
        Page<Entity> entitysPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(2, entitysPageResult.getContent().size());
    }

    @Test
    public void testFindByUserScore() {

        List<String> tags = new ArrayList<>();
        tags.add("ADMIN");

        List<String> classification = new ArrayList<>();
        classification.add("a");
        Entity entity1 = new Entity("userId1", "userName", "displayName", 5d, null, null, null, EntitySeverity.CRITICAL, 0, "user");
        Entity entity2 = new Entity("userId2", "userName", "displayName", 10d, null, null, null, EntitySeverity.CRITICAL, 0, "user");
        Entity entity3 = new Entity("userId3", "userName", "displayName", 20d, null, null, null, EntitySeverity.CRITICAL, 0, "user");
        Entity entity4 = new Entity("userId4", "userName", "displayName", 21d, null, null, null, EntitySeverity.CRITICAL, 0, "user");


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
    public void testFindByUserId() {
        List<String> tags = new ArrayList<>();


        Entity entity1 = new Entity("userId1-1234-5678", "userName", "displayName", 5d, null, null, null, EntitySeverity.CRITICAL, 0, "user");
        Entity entity2 = new Entity("userId1@somecompany.com", "userName", "displayName", 20d, null, null, null, EntitySeverity.CRITICAL, 0, "user");
        Entity entity3 = new Entity("userId1", "userName", "displayName", 21d, null, null, null, EntitySeverity.CRITICAL, 0, "user");


        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);
        entityList.add(entity3);
        Iterable<Entity> createdUsers = entityPersistencyService.save(entityList);


        EntityQuery.EntityQueryBuilder queryBuilder = new EntityQuery.EntityQueryBuilder().filterByUsersIds(Arrays.asList(new String[]{"userId1"}));
        Page<Entity> entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());


        queryBuilder = new EntityQuery.EntityQueryBuilder().filterByUsersIds(Arrays.asList(new String[]{"userId1-1234-5678"}));
        entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());

        queryBuilder = new EntityQuery.EntityQueryBuilder().filterByUsersIds(Arrays.asList(new String[]{"1234-5678-userId1"}));
        entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(0, entitiesPageResult.getContent().size());

        queryBuilder = new EntityQuery.EntityQueryBuilder().filterByUsersIds(Arrays.asList(new String[]{"1234"}));
        entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(0, entitiesPageResult.getContent().size());

        queryBuilder = new EntityQuery.EntityQueryBuilder().filterByUsersIds(Arrays.asList(new String[]{"userId1@somecompany.com"}));
        entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, entitiesPageResult.getContent().size());

        queryBuilder = new EntityQuery.EntityQueryBuilder().filterByUsersIds(Arrays.asList(new String[]{"somecompany.com@userId1"}));
        entitiesPageResult = entityPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(0, entitiesPageResult.getContent().size());
    }

    @Test
    public void testFindByIsUserAdmin_True() {
        List<String> tags = new ArrayList<>();
        tags.add("ADMIN");
        entity1.setTags(tags);
        List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);
        entityPersistencyService.save(entityList);

        List<String> sort = new ArrayList<>();
        sort.add(Entity.SCORE_FIELD_NAME);
        sort.add(Entity.USER_ID_FIELD_NAME);
        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder()
                        .filterByUserTags(tags)
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
        Entity entity1 = new Entity("userId1", "userName", "displayName", 5d, null, null, null, EntitySeverity.CRITICAL, 0, "user");
        Entity entity2 = new Entity("userId2", "userName", "displayName", 10d, null, null, null, EntitySeverity.MEDIUM, 0, "user");
        Entity entity3 = new Entity("userId3", "userName", "displayName", 20d, null, null, null, EntitySeverity.CRITICAL, 0, "user");
        Entity entity4 = new Entity("userId4", "userName", "displayName", 21d, null, null, null, EntitySeverity.MEDIUM, 0, "user");


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

        Entity entity1 = new Entity("userId1", "userName", "displayName", 5d, null, null, tags1, EntitySeverity.CRITICAL, 0 , "user");
        Entity entity2 = new Entity("userId2", "userName", "displayName", 10d, null, null, tags2, EntitySeverity.MEDIUM, 0, "user");
        Entity entity3 = new Entity("userId3", "userName", "displayName", 20d, null, null, tags1, EntitySeverity.CRITICAL, 0, "user");
        Entity entity4 = new Entity("userId4", "userName", "displayName", 21d, null, null, null, EntitySeverity.MEDIUM, 0, "user");


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
        Entity entity1 = new Entity("userId1", "userName", "displayName", 5d, classificationA, null, tags1, EntitySeverity.CRITICAL, 0, "user");
        Entity entity2 = new Entity("userId2", "userName", "displayName", 10d, classificationB, null, tags2, EntitySeverity.MEDIUM, 0, "user");
        Entity entity3 = new Entity("userId3", "userName", "displayName", 20d, classificationC, null, tags1, EntitySeverity.CRITICAL, 0, "user");


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
        Entity entity1 = new Entity("userId1", "userName", "displayName", 5d, null, indicatorsA, tags1, EntitySeverity.CRITICAL, 0, "user");
        Entity entity2 = new Entity("userId2", "userName", "displayName", 10d, null, indicatorsB, tags2, EntitySeverity.MEDIUM, 0, "user");
        Entity entity3 = new Entity("userId3", "userName", "displayName", 20d, null, indicatorsC, tags1, EntitySeverity.CRITICAL, 0, "user");


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
    public void testSortByUserName() {

        List<String> tags = Arrays.asList("admin");
        List<String> indicators = Arrays.asList("a");
        Entity entity1 = new Entity("userId1", "w_userName", "displayName", 5d, null, indicators, tags, EntitySeverity.CRITICAL, 0, "user");
        Entity entity2 = new Entity("userId2", "C_userName", "displayName", 10d, null, indicators, tags, EntitySeverity.MEDIUM, 0, "user");
        Entity entity3 = new Entity("userId3", "b_userName", "displayName", 20d, null, indicators, tags, EntitySeverity.CRITICAL, 0, "user");


        List<Entity> entityList = Arrays.asList(entity1, entity2, entity3);
        entityPersistencyService.save(entityList);


        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder()
                        .sort(new Sort(Sort.Direction.ASC, Entity.INDEXED_USER_NAME_FIELD_NAME))
                        .build();

        Page<Entity> result = entityPersistencyService.find(entityQuery);
        assertEquals(3L, result.getContent().size());
        Iterator<Entity> iterator = result.iterator();
        Assert.assertEquals("b_userName", iterator.next().getUserName());
        Assert.assertEquals("C_userName", iterator.next().getUserName());
        Assert.assertEquals("w_userName", iterator.next().getUserName());
    }

    @Test
    public void testSortByDisplayUserName() {

        List<String> tags = Arrays.asList("admin");
        List<String> indicators = Arrays.asList("a");
        Entity entity1 = new Entity("userId1", "W_userName", "Tttttt", 5d, null, indicators, tags, EntitySeverity.CRITICAL, 0, "user");
        Entity entity2 = new Entity("userId2", "C_userName", "eeeee", 10d, null, indicators, tags, EntitySeverity.MEDIUM, 0, "user");
        Entity entity3 = new Entity("userId3", "B_userName", "Qqqqqq", 20d, null, indicators, tags, EntitySeverity.CRITICAL, 0, "user");


        List<Entity> entityList = Arrays.asList(entity1, entity2, entity3);
        entityPersistencyService.save(entityList);

        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder()
                        .sort(new Sort(Sort.Direction.ASC, Entity.USER_DISPLAY_NAME_SORT_LOWERCASE_FIELD_NAME))
                        .build();

        Page<Entity> result = entityPersistencyService.find(entityQuery);
        assertEquals(3L, result.getContent().size());
        Iterator<Entity> iterator = result.iterator();
        Assert.assertEquals("eeeee", iterator.next().getUserDisplayName());
        Assert.assertEquals("Qqqqqq", iterator.next().getUserDisplayName());
        Assert.assertEquals("Tttttt", iterator.next().getUserDisplayName());
    }


    @Test
    public void testSortByAlertsNumber() {

        List<String> tags = Arrays.asList("admin");
        List<String> indicators = Arrays.asList("a");
        Entity entity1 = new Entity("userId1", "W_userName", "displayName", 5d, null, indicators, tags, EntitySeverity.CRITICAL, 1, "user");
        Entity entity2 = new Entity("userId2", "C_userName", "displayName", 10d, null, indicators, tags, EntitySeverity.MEDIUM, 2, "user");
        Entity entity3 = new Entity("userId3", "B_userName", "displayName", 20d, null, indicators, tags, EntitySeverity.CRITICAL, 3, "user");


        List<Entity> entityList = Arrays.asList(entity1, entity2, entity3);
        entityPersistencyService.save(entityList);


        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder()
                        .sort(new Sort(Sort.Direction.DESC, Entity.ALERTS_COUNT_FIELD_NAME))
                        .build();

        Page<Entity> result = entityPersistencyService.find(entityQuery);
        assertEquals(3L, result.getContent().size());
        Iterator<Entity> iterator = result.iterator();
        Assert.assertEquals("B_userName", iterator.next().getUserName());
        Assert.assertEquals("C_userName", iterator.next().getUserName());
        Assert.assertEquals("W_userName", iterator.next().getUserName());

        entityQuery =
                new EntityQuery.EntityQueryBuilder()
                        .sort(new Sort(Sort.Direction.ASC, Entity.ALERTS_COUNT_FIELD_NAME))
                        .build();

        result = entityPersistencyService.find(entityQuery);
        assertEquals(3L, result.getContent().size());
        iterator = result.iterator();
        Assert.assertEquals("W_userName", iterator.next().getUserName());
        Assert.assertEquals("C_userName", iterator.next().getUserName());
        Assert.assertEquals("B_userName", iterator.next().getUserName());
    }

    @Test
    public void testFindByUserNameCaseInsensitive() {

        List<String> tags = Arrays.asList("admin");
        List<String> indicators = Arrays.asList("a");
        Entity entity1 = new Entity("userId1", "W_userName", "displayName", 5d, null, indicators, tags, EntitySeverity.CRITICAL, 0, "user");
        Entity entity2 = new Entity("userId2", "C_userName", "displayName", 10d, null, indicators, tags, EntitySeverity.MEDIUM, 0, "user");
        Entity entity3 = new Entity("userId3", "B_userName", "displayName", 20d, null, indicators, tags, EntitySeverity.CRITICAL, 0, "user");


        List<Entity> entityList = Arrays.asList(entity1, entity2, entity3);
        entityPersistencyService.save(entityList);


        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByUserName("w_userName")
                        .build();

        Page<Entity> result = entityPersistencyService.find(entityQuery);
        assertEquals(1L, result.getContent().size());
        Iterator<Entity> iterator = result.iterator();
        Assert.assertEquals("W_userName", iterator.next().getUserName());
    }


    @Test
    public void testFindByUserNameContains() {

        List<String> tags = Arrays.asList("admin");
        List<String> indicators = Arrays.asList("a");
        Entity entity1 = new Entity("userId1", "Donald Duck", "displayName", 5d, null, indicators, tags, EntitySeverity.CRITICAL, 0, "user");
        Entity entity2 = new Entity("userId2", "Mini Mous", "displayName", 10d, null, indicators, tags, EntitySeverity.MEDIUM, 0, "user");
        Entity entity3 = new Entity("userId3", "Donkey", "displayName", 20d, null, indicators, tags, EntitySeverity.CRITICAL, 0, "user");


        List<Entity> entityList = Arrays.asList(entity1, entity2, entity3);
        entityPersistencyService.save(entityList);


        EntityQuery entityQuery =
                new EntityQuery.EntityQueryBuilder().filterByUserName("duck")
                        .build();

        Page<Entity> result = entityPersistencyService.find(entityQuery);
        assertEquals(1L, result.getContent().size());
        Iterator<Entity> iterator = result.iterator();
        Assert.assertEquals("Donald Duck", iterator.next().getUserName());
    }
}
