package presidio.output.domain.services;

import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import org.assertj.core.util.Lists;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.AbstractElasticDocument;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.spring.TestConfig;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig.class, TestConfig.class, ElasticsearchTestConfig.class})
@ActiveProfiles("useEmbeddedElastic")
public class UserPersistencyServiceTest{

    @Autowired
    private UserPersistencyService userPersistencyService;

    List<String> classifications1 = new ArrayList<>(Arrays.asList("a", "b", "c"));
    List<String> classifications2 = new ArrayList<>(Arrays.asList("b"));
    List<String> classifications3 = new ArrayList<>(Arrays.asList("a"));
    List<String> classifications4 = new ArrayList<>(Arrays.asList("d"));
    User user1 = generateUser(classifications1, "user1", "userId1", "user1", 50d);
    User user2 = generateUser(classifications2, "user2", "userId2", "user2", 60d);
    User user3 = generateUser(classifications3, "user3", "userId3", "user3", 70d);
    User user4 = generateUser(classifications4, "user4", "userId4", "user4", 80d);
    User user5 = generateUser(classifications3, "user5", "userId5", "user4", 70d);
    User user6 = generateUser(classifications3, "fretext", "userId6", "free", 70d);
    User user7 = generateUser(classifications3, "free", "userId7", "text", 70d);
    User user8 = generateUser(classifications3, "text", "userId8", "freetex", 70d);

    @Autowired
    public Client client;

    @After
    public void cleanTestData() {
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .source(AbstractElasticDocument.INDEX_NAME + "-" + User.USER_DOC_TYPE)
                .get();
    }

    private User generateUser(List<String> classifications, String userName, String userId, String displayName, double score) {
        List<String> indicators = Arrays.asList(new String("indicator"));
        return new User(userId, userName, displayName, score, classifications, indicators, null, UserSeverity.CRITICAL, 0);
    }

    @Test
    public void testSave() {
        User user = user1;
        User createdUser = userPersistencyService.save(user1);

        assertNotNull(createdUser.getId());
        assertEquals(createdUser.getId(), user.getId());
        assertEquals(createdUser.getUserName(), user.getUserName());
        assertEquals(createdUser.getUserDisplayName(), user.getUserDisplayName());
        assertTrue(createdUser.getScore() == user.getScore());
        assertEquals(createdUser.getAlertClassifications().size(), user.getAlertClassifications().size());
        assertEquals(createdUser.getIndicators().size(), user.getIndicators().size());
    }

    @Test
    public void testSaveBulk() {
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        Iterable<User> createdUsers = userPersistencyService.save(userList);

        assertThat(Lists.newArrayList(createdUsers).size(), is(2));

    }


    @Test
    public void testFindOne() {
        User user = generateUser(classifications1, "user1", "userId1", "user1", 50d);
        userPersistencyService.save(user);

        Date createdByBeforeFind = user.getCreatedDate();
        User foundUser = userPersistencyService.findUserById(user.getId());
        Date createdByAfterFind = foundUser.getCreatedDate();

        assertNotNull(foundUser.getId());
        assertEquals(createdByBeforeFind, createdByAfterFind);
        assertEquals(foundUser.getId(), user.getId());
        assertEquals(foundUser.getUserName(), user.getUserName());
        assertEquals(foundUser.getUserDisplayName(), user.getUserDisplayName());
        assertTrue(foundUser.getScore() == user.getScore());
        assertEquals(foundUser.getAlertClassifications().size(), user.getAlertClassifications().size());
        assertEquals(foundUser.getIndicators().size(), user.getIndicators().size());

    }

    @Test
    public void testUpdatedBY() throws InterruptedException {
        Thread.currentThread().setName("TEST");
        User user = generateUser(classifications1, "user1", "userId1", "user1", 50d);
        String created = user.getUpdatedBy();
        userPersistencyService.save(user);
        User foundUser = userPersistencyService.findUserById(user.getId());
        String createdBy = foundUser.getUpdatedBy();
        Thread.sleep(1000);
        userPersistencyService.save(foundUser);
        foundUser = userPersistencyService.findUserById(user.getId());
        String updatedByAgain = foundUser.getUpdatedBy();
        Thread.currentThread().setName("TEST2");
        Thread.sleep(1000);
        userPersistencyService.save(foundUser);
        foundUser = userPersistencyService.findUserById(user.getId());
        String updatedByAgain2 = foundUser.getUpdatedBy();

        assertNotNull(foundUser.getId());
        assertEquals(foundUser.getId(), user.getId());
        assertNotEquals(null, createdBy);
        assertEquals(createdBy, updatedByAgain);
        assertNotEquals(createdBy, updatedByAgain2);
    }

    @Test
    public void testFreeTextWithoutIsPrefixEnabled() {
        List<User> userList = new ArrayList<>();
        userList.add(user6);
        userList.add(user7);
        userList.add(user8);
        userPersistencyService.save(userList);

        UserQuery userQuery =
                new UserQuery.UserQueryBuilder().filterByFreeText("free")
                        .build();
        Page<User> foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(2L));

        userQuery =
                new UserQuery.UserQueryBuilder().filterByFreeText("fre")
                        .build();
        foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(0L));

        userQuery =
                new UserQuery.UserQueryBuilder().filterByFreeText("text")
                        .build();
        foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(2L));
    }

    @Test
    public void testFreeTextWithIsPrefixEnabled() {
        List<User> userList = new ArrayList<>();
        userList.add(user6);
        userList.add(user7);
        userList.add(user8);
        userPersistencyService.save(userList);

        UserQuery userQuery =
                new UserQuery.UserQueryBuilder().filterByFreeText("free")
                        .filterByUserNameWithPrefix(true)
                        .build();
        Page<User> foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(3L));

        userQuery =
                new UserQuery.UserQueryBuilder().filterByFreeText("fre")
                        .filterByUserNameWithPrefix(true)
                        .build();
        foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(3L));

        userQuery =
                new UserQuery.UserQueryBuilder().filterByFreeText("text")
                        .filterByUserNameWithPrefix(true)
                        .build();
        foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(2L));
    }

    @Test
    public void testFreeTextWhitUserName() {
        List<User> userList = new ArrayList<>();
        userList.add(user6);
        userList.add(user7);
        userList.add(user8);
        userPersistencyService.save(userList);
        Page<User> foundUsers = null;

        UserQuery userQuery =
                new UserQuery.UserQueryBuilder().filterByFreeText("free")
                        .filterByUserNameWithPrefix(true)
                        .filterByUserName("free")
                        .build();
        foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(1L));

        userQuery =
                new UserQuery.UserQueryBuilder().filterByFreeText("free")
                        .filterByUserName("free")
                        .build();
        foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(1L));

        userQuery =
                new UserQuery.UserQueryBuilder().filterByFreeText("fre")
                        .filterByUserNameWithPrefix(true)
                        .filterByUserName("fre")
                        .build();
        foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(2L));

        userQuery =
                new UserQuery.UserQueryBuilder().filterByFreeText("fre")
                        .filterByUserName("fre")
                        .build();
        foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(0L));
    }

    @Test
    public void testFindAll() {
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        userPersistencyService.save(userList);

        Iterable<User> foundUsers = userPersistencyService.findAll();
        assertThat(Lists.newArrayList(foundUsers).size(), is(2));
    }

    @Test
    public void testFindByQueryFilterByClassificationsAndSortByScoreAscending() {

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userList.add(user5);
        userPersistencyService.save(userList);

        List<String> classificationFilter = new ArrayList<String>();
        classificationFilter.add("a");

        List<String> sort = new ArrayList<>();
        sort.add(User.SCORE_FIELD_NAME);
        sort.add(User.USER_ID_FIELD_NAME);
        UserQuery userQuery =
                new UserQuery.UserQueryBuilder()
                        .filterByAlertClassifications(classificationFilter)
                        .sort(new Sort(new Sort.Order(User.SCORE_FIELD_NAME)))
                        .build();

        Page<User> foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(3L));
        assertTrue(foundUsers.iterator().next().getScore() == 50d);
    }

    @Test
    public void testFindByQueryFilterByIndicators() {
        List<String> indicators1 = Arrays.asList("indicatorName1");
        List<String> indicators2 = Arrays.asList("indicatorName1", "indicatorName2");

        user1.setIndicators(indicators1);
        user2.setIndicators(indicators2);
        List<User> userList = Arrays.asList(user1, user2);
        userPersistencyService.save(userList);

        List<String> indicatorFilter = new ArrayList<String>();
        indicatorFilter.add("indicator");

        UserQuery userQuery =
                new UserQuery.UserQueryBuilder()
                        .filterByIndicators(indicators1)
                        .build();

        Page<User> foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(2L));

        UserQuery userQuery2 =
                new UserQuery.UserQueryBuilder()
                        .filterByIndicators(Arrays.asList("indicatorName2"))
                        .build();

        Page<User> foundUsers2 = userPersistencyService.find(userQuery2);
        assertThat(foundUsers2.getTotalElements(), is(1L));
    }

    @Test
    public void testFindByListOfIds() {

        User user1 = new User("userId1", "userName", "displayName", 0d, null, null, null, UserSeverity.CRITICAL, 0);
        User user2 = new User("userId2", "userName", "displayName", 0d, null, null, null, UserSeverity.CRITICAL, 0);
        User user3 = new User("userId3", "userName", "displayName", 0d, null, null, null, UserSeverity.CRITICAL, 0);
        User user4 = new User("userId4", "userName", "displayName", 0d, null, null, null, UserSeverity.CRITICAL, 0);

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        Iterable<User> createdUsers = userPersistencyService.save(userList);
        List<String> userIds = new ArrayList<>();
        userIds.add(user1.getUserId());
        userIds.add(user2.getUserId());
        userIds.add("userId5");

        UserQuery.UserQueryBuilder queryBuilder = new UserQuery.UserQueryBuilder().pageNumber(0).pageSize(10).filterByUsersIds(userIds);
        Page<User> usersPageResult = userPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(2, usersPageResult.getContent().size());
    }

    @Test
    public void testFindByUserScore() {

        List<String> tags = new ArrayList<>();
        tags.add("ADMIN");

        List<String> classification = new ArrayList<>();
        classification.add("a");
        User user1 = new User("userId1", "userName", "displayName", 5d, null, null, null, UserSeverity.CRITICAL, 0);
        User user2 = new User("userId2", "userName", "displayName", 10d, null, null, null, UserSeverity.CRITICAL, 0);
        User user3 = new User("userId3", "userName", "displayName", 20d, null, null, null, UserSeverity.CRITICAL, 0);
        User user4 = new User("userId4", "userName", "displayName", 21d, null, null, null, UserSeverity.CRITICAL, 0);


        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        Iterable<User> createdUsers = userPersistencyService.save(userList);


        UserQuery.UserQueryBuilder queryBuilder = new UserQuery.UserQueryBuilder().pageNumber(0).pageSize(10).minScore(10).maxScore(20);
        Page<User> usersPageResult = userPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(2, usersPageResult.getContent().size());
    }

    @Test
    public void testFindByUserId() {
        List<String> tags = new ArrayList<>();


        User user1 = new User("userId1-1234-5678", "userName", "displayName", 5d, null, null, null, UserSeverity.CRITICAL, 0);
        User user2 = new User("userId1@somecompany.com", "userName", "displayName", 20d, null, null, null, UserSeverity.CRITICAL, 0);
        User user3 = new User("userId1", "userName", "displayName", 21d, null, null, null, UserSeverity.CRITICAL, 0);


        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        Iterable<User> createdUsers = userPersistencyService.save(userList);


        UserQuery.UserQueryBuilder queryBuilder = new UserQuery.UserQueryBuilder().filterByUsersIds(Arrays.asList(new String[]{"userId1"}));
        Page<User> usersPageResult = userPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, usersPageResult.getContent().size());


        queryBuilder = new UserQuery.UserQueryBuilder().filterByUsersIds(Arrays.asList(new String[]{"userId1-1234-5678"}));
        usersPageResult = userPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, usersPageResult.getContent().size());

        queryBuilder = new UserQuery.UserQueryBuilder().filterByUsersIds(Arrays.asList(new String[]{"1234-5678-userId1"}));
        usersPageResult = userPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(0, usersPageResult.getContent().size());

        queryBuilder = new UserQuery.UserQueryBuilder().filterByUsersIds(Arrays.asList(new String[]{"1234"}));
        usersPageResult = userPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(0, usersPageResult.getContent().size());

        queryBuilder = new UserQuery.UserQueryBuilder().filterByUsersIds(Arrays.asList(new String[]{"userId1@somecompany.com"}));
        usersPageResult = userPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(1, usersPageResult.getContent().size());

        queryBuilder = new UserQuery.UserQueryBuilder().filterByUsersIds(Arrays.asList(new String[]{"somecompany.com@userId1"}));
        usersPageResult = userPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(0, usersPageResult.getContent().size());
    }

    @Test
    public void testFindByIsUserAdmin_True() {
        List<String> tags = new ArrayList<>();
        tags.add("ADMIN");
        user1.setTags(tags);
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userPersistencyService.save(userList);

        List<String> sort = new ArrayList<>();
        sort.add(User.SCORE_FIELD_NAME);
        sort.add(User.USER_ID_FIELD_NAME);
        UserQuery userQuery =
                new UserQuery.UserQueryBuilder()
                        .filterByUserTags(tags)
                        .build();

        Page<User> foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(1L));
        User foundUser = foundUsers.iterator().next();
        assertNotNull(foundUser.getTags());
        assertEquals(1, foundUser.getTags().size());
        assertEquals(tags, foundUser.getTags());
    }

    @Test
    public void testFindByQueryWithSeverityAggregation() {

        List<String> classification = new ArrayList<>();
        classification.add("a");
        User user1 = new User("userId1", "userName", "displayName", 5d, null, null, null, UserSeverity.CRITICAL, 0);
        User user2 = new User("userId2", "userName", "displayName", 10d, null, null, null, UserSeverity.MEDIUM, 0);
        User user3 = new User("userId3", "userName", "displayName", 20d, null, null, null, UserSeverity.CRITICAL, 0);
        User user4 = new User("userId4", "userName", "displayName", 21d, null, null, null, UserSeverity.MEDIUM, 0);


        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userPersistencyService.save(userList);

        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(User.SEVERITY_FIELD_NAME);

        UserQuery userQuery =
                new UserQuery.UserQueryBuilder()
                        .aggregateByFields(aggregationFields)
                        .build();

        Page<User> result = userPersistencyService.find(userQuery);
        Map<String, Aggregation> stringAggregationMap = ((AggregatedPageImpl<User>) result).getAggregations().asMap();
        StringTerms severityAgg = (StringTerms) stringAggregationMap.get(User.SEVERITY_FIELD_NAME);
        List<Terms.Bucket> buckets = severityAgg.getBuckets();

        assertEquals(buckets.size(), 2L); //two buckets- HIGH and MEDIUM
        assertEquals(severityAgg.getBucketByKey("CRITICAL").getDocCount(), 2L);
        assertEquals(severityAgg.getBucketByKey("MEDIUM").getDocCount(), 2L);
    }

    @Test
    public void testFindByQueryWithTagsAggregation() {

        List<String> tags1 = new ArrayList<>(Arrays.asList("admin", "watch"));
        List<String> tags2 = new ArrayList<>(Arrays.asList("admin"));

        User user1 = new User("userId1", "userName", "displayName", 5d, null, null, tags1, UserSeverity.CRITICAL, 0);
        User user2 = new User("userId2", "userName", "displayName", 10d, null, null, tags2, UserSeverity.MEDIUM, 0);
        User user3 = new User("userId3", "userName", "displayName", 20d, null, null, tags1, UserSeverity.CRITICAL, 0);
        User user4 = new User("userId4", "userName", "displayName", 21d, null, null, null, UserSeverity.MEDIUM, 0);


        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userPersistencyService.save(userList);

        List<String> aggregationFields = new ArrayList<>();
        aggregationFields.add(User.TAGS_FIELD_NAME);

        UserQuery userQuery =
                new UserQuery.UserQueryBuilder()
                        .aggregateByFields(aggregationFields)
                        .build();

        Page<User> result = userPersistencyService.find(userQuery);
        Map<String, Aggregation> stringAggregationMap = ((AggregatedPageImpl<User>) result).getAggregations().asMap();
        StringTerms severityAgg = (StringTerms) stringAggregationMap.get(User.TAGS_FIELD_NAME);
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
        User user1 = new User("userId1", "userName", "displayName", 5d, classificationA, null, tags1, UserSeverity.CRITICAL, 0);
        User user2 = new User("userId2", "userName", "displayName", 10d, classificationB, null, tags2, UserSeverity.MEDIUM, 0);
        User user3 = new User("userId3", "userName", "displayName", 20d, classificationC, null, tags1, UserSeverity.CRITICAL, 0);


        List<User> userList = Arrays.asList(user1, user2, user3);
        userPersistencyService.save(userList);

        List<String> aggregationFields = Arrays.asList(User.ALERT_CLASSIFICATIONS_FIELD_NAME);

        UserQuery userQuery =
                new UserQuery.UserQueryBuilder()
                        .aggregateByFields(aggregationFields)
                        .build();

        Page<User> result = userPersistencyService.find(userQuery);
        Map<String, Aggregation> stringAggregationMap = ((AggregatedPageImpl<User>) result).getAggregations().asMap();
        StringTerms severityAgg = (StringTerms) stringAggregationMap.get(User.ALERT_CLASSIFICATIONS_FIELD_NAME);
        List<Terms.Bucket> buckets = severityAgg.getBuckets();

        assertEquals(buckets.size(), 3L); //two buckets- admin and watch
        assertEquals(severityAgg.getBucketByKey("a").getDocCount(), 3L);
        assertEquals(severityAgg.getBucketByKey("b").getDocCount(), 2L);
        assertEquals(severityAgg.getBucketByKey("c").getDocCount(), 1L);
    }

    @Test
    public void testFindByQueryFilterByClassificationsAndAggregateBySeverity() {

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userList.add(user5);
        userPersistencyService.save(userList);

        List<String> classificationFilter = new ArrayList<String>();
        classificationFilter.add("a");

        UserQuery userQuery =
                new UserQuery.UserQueryBuilder()
                        .filterByAlertClassifications(classificationFilter)
                        .aggregateByFields(Arrays.asList(User.SEVERITY_FIELD_NAME))
                        .build();

        Page<User> foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(3L));

        Map<String, Aggregation> stringAggregationMap = ((AggregatedPageImpl<User>) foundUsers).getAggregations().asMap();
        StringTerms severityAgg = (StringTerms) stringAggregationMap.get(User.SEVERITY_FIELD_NAME);
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
        User user1 = new User("userId1", "userName", "displayName", 5d, null, indicatorsA, tags1, UserSeverity.CRITICAL, 0);
        User user2 = new User("userId2", "userName", "displayName", 10d, null, indicatorsB, tags2, UserSeverity.MEDIUM, 0);
        User user3 = new User("userId3", "userName", "displayName", 20d, null, indicatorsC, tags1, UserSeverity.CRITICAL, 0);


        List<User> userList = Arrays.asList(user1, user2, user3);
        userPersistencyService.save(userList);

        List<String> aggregationFields = Arrays.asList(User.INDICATORS_FIELD_NAME);

        UserQuery userQuery =
                new UserQuery.UserQueryBuilder()
                        .aggregateByFields(aggregationFields)
                        .build();

        Page<User> result = userPersistencyService.find(userQuery);
        Map<String, Aggregation> stringAggregationMap = ((AggregatedPageImpl<User>) result).getAggregations().asMap();
        StringTerms severityAgg = (StringTerms) stringAggregationMap.get(User.INDICATORS_FIELD_NAME);
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
        User user1 = new User("userId1", "W_userName", "displayName", 5d, null, indicators, tags, UserSeverity.CRITICAL, 0);
        User user2 = new User("userId2", "C_userName", "displayName", 10d, null, indicators, tags, UserSeverity.MEDIUM, 0);
        User user3 = new User("userId3", "B_userName", "displayName", 20d, null, indicators, tags, UserSeverity.CRITICAL, 0);


        List<User> userList = Arrays.asList(user1, user2, user3);
        userPersistencyService.save(userList);


        UserQuery userQuery =
                new UserQuery.UserQueryBuilder()
                        .sort(new Sort(Sort.Direction.ASC, User.INDEXED_USER_NAME_FIELD_NAME))
                        .build();

        Page<User> result = userPersistencyService.find(userQuery);
        assertEquals(3L, result.getContent().size());
        Iterator<User> iterator = result.iterator();
        Assert.assertEquals("B_userName", iterator.next().getUserName());
        Assert.assertEquals("C_userName", iterator.next().getUserName());
        Assert.assertEquals("W_userName", iterator.next().getUserName());
    }

    @Test
    public void testSortByAlertsNumber() {

        List<String> tags = Arrays.asList("admin");
        List<String> indicators = Arrays.asList("a");
        User user1 = new User("userId1", "W_userName", "displayName", 5d, null, indicators, tags, UserSeverity.CRITICAL, 1);
        User user2 = new User("userId2", "C_userName", "displayName", 10d, null, indicators, tags, UserSeverity.MEDIUM, 2);
        User user3 = new User("userId3", "B_userName", "displayName", 20d, null, indicators, tags, UserSeverity.CRITICAL, 3);


        List<User> userList = Arrays.asList(user1, user2, user3);
        userPersistencyService.save(userList);


        UserQuery userQuery =
                new UserQuery.UserQueryBuilder()
                        .sort(new Sort(Sort.Direction.DESC, User.ALERTS_COUNT_FIELD_NAME))
                        .build();

        Page<User> result = userPersistencyService.find(userQuery);
        assertEquals(3L, result.getContent().size());
        Iterator<User> iterator = result.iterator();
        Assert.assertEquals("B_userName", iterator.next().getUserName());
        Assert.assertEquals("C_userName", iterator.next().getUserName());
        Assert.assertEquals("W_userName", iterator.next().getUserName());

        userQuery =
                new UserQuery.UserQueryBuilder()
                        .sort(new Sort(Sort.Direction.ASC, User.ALERTS_COUNT_FIELD_NAME))
                        .build();

        result = userPersistencyService.find(userQuery);
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
        User user1 = new User("userId1", "W_userName", "displayName", 5d, null, indicators, tags, UserSeverity.CRITICAL, 0);
        User user2 = new User("userId2", "C_userName", "displayName", 10d, null, indicators, tags, UserSeverity.MEDIUM, 0);
        User user3 = new User("userId3", "B_userName", "displayName", 20d, null, indicators, tags, UserSeverity.CRITICAL, 0);


        List<User> userList = Arrays.asList(user1, user2, user3);
        userPersistencyService.save(userList);


        UserQuery userQuery =
                new UserQuery.UserQueryBuilder().filterByUserName("w_userName")
                        .build();

        Page<User> result = userPersistencyService.find(userQuery);
        assertEquals(1L, result.getContent().size());
        Iterator<User> iterator = result.iterator();
        Assert.assertEquals("W_userName", iterator.next().getUserName());
    }


    @Test
    public void testFindByUserNameContains() {

        List<String> tags = Arrays.asList("admin");
        List<String> indicators = Arrays.asList("a");
        User user1 = new User("userId1", "Donald Duck", "displayName", 5d, null, indicators, tags, UserSeverity.CRITICAL, 0);
        User user2 = new User("userId2", "Mini Mous", "displayName", 10d, null, indicators, tags, UserSeverity.MEDIUM, 0);
        User user3 = new User("userId3", "Donkey", "displayName", 20d, null, indicators, tags, UserSeverity.CRITICAL, 0);


        List<User> userList = Arrays.asList(user1, user2, user3);
        userPersistencyService.save(userList);


        UserQuery userQuery =
                new UserQuery.UserQueryBuilder().filterByUserName("duck")
                        .build();

        Page<User> result = userPersistencyService.find(userQuery);
        assertEquals(1L, result.getContent().size());
        Iterator<User> iterator = result.iterator();
        Assert.assertEquals("Donald Duck", iterator.next().getUserName());
    }
}