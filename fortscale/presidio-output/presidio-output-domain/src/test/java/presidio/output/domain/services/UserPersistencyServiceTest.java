package presidio.output.domain.services;

import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.services.users.UserPersistencyService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;


@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes=presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig.class)
public class UserPersistencyServiceTest {

    @Autowired
    private UserPersistencyService userPersistencyService;

    @Autowired
    private PresidioElasticsearchTemplate esTemplate;

    @Before
    public void before() {
        esTemplate.deleteIndex(User.class);
        esTemplate.createIndex(User.class);
        esTemplate.putMapping(User.class);
        esTemplate.refresh(User.class);
    }

    @Test
    public void testSave() {
        User user = generateUser();
        User createdUser = userPersistencyService.save(user);

        assertNotNull(createdUser.getId());
        assertEquals(createdUser.getId(), user.getId());
        assertEquals(createdUser.getUserName(), user.getUserName());
        assertEquals(createdUser.getUserDisplayName(), user.getUserDisplayName());
        assertTrue(createdUser.getUserScore() == user.getUserScore());
        assertEquals(createdUser.getAlertClassifications().size(), user.getAlertClassifications().size());
        assertEquals(createdUser.getIndicators().size(), user.getIndicators().size());
    }

    @Test
    public void testSaveBulk() {
        User user1 = generateUser();
        User user2 = generateUser();
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        Iterable<User> createdUsers = userPersistencyService.save(userList);

        assertThat(Lists.newArrayList(createdUsers).size(), is(2));

    }

    private User generateUser() {
        ArrayList<String> classifications = new ArrayList<String>();
        classifications.add("classification");
        ArrayList<String> indicators = new ArrayList<String>();
        indicators.add("indicator");
        return new User("userId", "userName", "displayName", 0d, classifications, indicators);
    }

    @Test
    public void clean(){

    }

    @Test
    public void testFindOne() {
        User user = generateUser();
        userPersistencyService.save(user);

        User foundUser = userPersistencyService.findUserById(user.getId());

        assertNotNull(foundUser.getId());
        assertEquals(foundUser.getId(), user.getId());
        assertEquals(foundUser.getUserName(), user.getUserName());
        assertEquals(foundUser.getUserDisplayName(), user.getUserDisplayName());
        assertTrue(foundUser.getUserScore() == user.getUserScore());
        assertEquals(foundUser.getAlertClassifications().size(), user.getAlertClassifications().size());
        assertEquals(foundUser.getIndicators().size(), user.getIndicators().size());

    }

    @Test
    public void testFindAll() {
        User user1 = generateUser();
        User user2 = generateUser();
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        userPersistencyService.save(userList);

        Iterable<User> foundUsers = userPersistencyService.findAll();
        assertThat(Lists.newArrayList(foundUsers).size(), is(2));
    }

    @Test
    public void testFindByQueryFilterByIndicatorsAndClassifications() {

        User user1 = generateUser();
        User user2 = generateUser();
        User user3 = generateUser();
        User user4 = generateUser();
        user2.setUserScore(100d);
        user4.setUserScore(50d);
        List<String> classification = new ArrayList<String>();
        user3.setAlertClassifications(classification);
        List<String> indicators = new ArrayList<String>();;
        user3.setIndicators(indicators);
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userPersistencyService.save(userList);

        List<String> classificationFilter = new ArrayList<String>();
        classificationFilter.add("classification");
        List<String> indicatorFilter = new ArrayList<String>();
        classificationFilter.add("indicator");

        UserQuery userQuery =
                new UserQuery.UserQueryBuilder()
                        .filterByAlertClassifications(classificationFilter)
                        .filterByIndicators(indicatorFilter)
                        .sortField(User.SCORE_FIELD_NAME, false)
                        .build();

        Page<User> foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(3L));
        assertTrue(foundUsers.iterator().next().getUserScore() == 100d); //verify the sorting- descending order, score 100 should be the first
    }

    @Test
    public void testFindByListOfIds(){
        User user1 = new User("userId1", "userName", "displayName", 0d, null, null);
        User user2 = new User("userId2", "userName", "displayName", 0d, null, null);
        User user3 = new User("userId3", "userName", "displayName", 0d, null, null);
        User user4 = new User("userId4", "userName", "displayName", 0d, null, null);

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
        Assert.assertEquals(2,usersPageResult.getContent().size());
        Assert.assertEquals(user1.getUserId(),usersPageResult.getContent().get(0).getUserId());
        Assert.assertEquals(user2.getUserId(),usersPageResult.getContent().get(1).getUserId());
    }

    @Test
    public void testFindByListOfExculdedIds(){
        User user1 = new User("userId1", "userName", "displayName", 0d, null, null);
        User user2 = new User("userId2", "userName", "displayName", 0d, null, null);
        User user3 = new User("userId3", "userName", "displayName", 0d, null, null);
        User user4 = new User("userId4", "userName", "displayName", 0d, null, null);

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        Iterable<User> createdUsers = userPersistencyService.save(userList);
        List<String> excludedUserIds = new ArrayList<>();
        excludedUserIds.add(user1.getUserId());
        excludedUserIds.add(user2.getUserId());
        excludedUserIds.add("userId5");

        UserQuery.UserQueryBuilder queryBuilder = new UserQuery.UserQueryBuilder().pageNumber(0).pageSize(10).filterByNotHaveAnyOfUserIds(excludedUserIds);
        Page<User> usersPageResult = userPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(2,usersPageResult.getContent().size());
        Assert.assertEquals(user3.getUserId(),usersPageResult.getContent().get(0).getUserId());
        Assert.assertEquals(user4.getUserId(),usersPageResult.getContent().get(1).getUserId());
    }

    @Test
    public void testFindByUserScore(){
        User user1 = new User("userId1", "userName", "displayName", 5d, null, null);
        User user2 = new User("userId2", "userName", "displayName", 10d, null, null);
        User user3 = new User("userId3", "userName", "displayName", 20d, null, null);
        User user4 = new User("userId4", "userName", "displayName", 21d, null, null);

        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        Iterable<User> createdUsers = userPersistencyService.save(userList);


        UserQuery.UserQueryBuilder queryBuilder = new UserQuery.UserQueryBuilder().pageNumber(0).pageSize(10).minScore(10).maxScore(20);
        Page<User> usersPageResult = userPersistencyService.find(queryBuilder.build());
        Assert.assertEquals(2,usersPageResult.getContent().size());
        Assert.assertEquals(user2.getUserId(),usersPageResult.getContent().get(0).getUserId());
        Assert.assertEquals(user3.getUserId(),usersPageResult.getContent().get(1).getUserId());
    }

}