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
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.users.UserPersistencyService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;


@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig.class)
public class UserPersistencyServiceTest {

    @Autowired
    private UserPersistencyService userPersistencyService;

    @Autowired
    private PresidioElasticsearchTemplate esTemplate;

    List<String> classifications1;
    List<String> classifications2;
    List<String> classifications3;
    List<String> classifications4;
    List<String> classifications5;
    User user1;
    User user2;
    User user3;
    User user4;
    User user5;


    @Before
    public void before() {
        esTemplate.deleteIndex(User.class);
        esTemplate.createIndex(User.class);
        esTemplate.putMapping(User.class);
        esTemplate.refresh(User.class);
        classifications1 = new ArrayList<>(Arrays.asList("a", "b", "c"));
        classifications2 = new ArrayList<>(Arrays.asList("b"));
        classifications3 = new ArrayList<>(Arrays.asList("a"));
        classifications4 = new ArrayList<>(Arrays.asList("d"));
        classifications5 = null;
        user1 = generateUser(classifications1, "user1", "userId1", "user1", 50d);
        user2 = generateUser(classifications2, "user2", "userId2", "user2", 60d);
        user3 = generateUser(classifications3, "user3", "userId3", "user3", 70d);
        user4 = generateUser(classifications4, "user4", "userId4", "user4", 80d);
        user5 = generateUser(classifications3, "user5", "userId5", "user4", 70d);
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

    private User generateUser(List<String> classifications, String userName, String userId, String displayName, double score) {
        ArrayList<String> indicators = new ArrayList<String>();
        indicators.add("indicator");
        return new User(userId, userName, displayName, score, classifications, indicators, false, UserSeverity.CRITICAL, 0);
    }


    @Test
    public void testFindOne() {
        User user = user1;
        userPersistencyService.save(user);

        User foundUser = userPersistencyService.findUserById(user.getId());

        assertNotNull(foundUser.getId());
        assertEquals(foundUser.getId(), user.getId());
        assertEquals(foundUser.getUserName(), user.getUserName());
        assertEquals(foundUser.getUserDisplayName(), user.getUserDisplayName());
        assertTrue(foundUser.getScore() == user.getScore());
        assertEquals(foundUser.getAlertClassifications().size(), user.getAlertClassifications().size());
        assertEquals(foundUser.getIndicators().size(), user.getIndicators().size());

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
        List<String> indicators = new ArrayList<String>();

        user3.setIndicators(indicators);
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userList.add(user5);
        userPersistencyService.save(userList);

        List<String> classificationFilter = new ArrayList<String>();
        classificationFilter.add("a");
        List<String> indicatorFilter = new ArrayList<String>();
        indicatorFilter.add("indicator");

        List<String> sortFields = new ArrayList<>();
        sortFields.add(User.SCORE_FIELD_NAME);
        sortFields.add(User.USER_ID_FIELD_NAME);
        UserQuery userQuery =
                new UserQuery.UserQueryBuilder()
                        .filterByAlertClassifications(classificationFilter)
                        .sortField(new Sort(new Sort.Order(User.SCORE_FIELD_NAME)))
                        .build();

        Page<User> foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(3L));
        assertTrue(foundUsers.iterator().next().getScore() == 50d);
    }

    @Test
    public void testFindByListOfIds() {

        User user1 = new User("userId1", "userName", "displayName", 0d, null, null, false, UserSeverity.CRITICAL, 0);
        User user2 = new User("userId2", "userName", "displayName", 0d, null, null, false, UserSeverity.CRITICAL, 0);
        User user3 = new User("userId3", "userName", "displayName", 0d, null, null, false, UserSeverity.CRITICAL, 0);
        User user4 = new User("userId4", "userName", "displayName", 0d, null, null, false, UserSeverity.CRITICAL, 0);

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
        User user1 = new User("userId1", "userName", "displayName", 5d, null, null, false, UserSeverity.CRITICAL, 0);
        User user2 = new User("userId2", "userName", "displayName", 10d, null, null, false, UserSeverity.CRITICAL, 0);
        User user3 = new User("userId3", "userName", "displayName", 20d, null, null, false, UserSeverity.CRITICAL, 0);
        User user4 = new User("userId4", "userName", "displayName", 21d, null, null, false, UserSeverity.CRITICAL, 0);

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
    public void testFindByIsUserAdmin_True() {
        user1.setAdmin(true);
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userPersistencyService.save(userList);

        List<String> sortFields = new ArrayList<>();
        sortFields.add(User.SCORE_FIELD_NAME);
        sortFields.add(User.USER_ID_FIELD_NAME);
        UserQuery userQuery =
                new UserQuery.UserQueryBuilder()
                        .filterByUserAdmin(true)
                        .build();

        Page<User> foundUsers = userPersistencyService.find(userQuery);
        assertThat(foundUsers.getTotalElements(), is(1L));
        assertTrue(foundUsers.iterator().next().getAdmin());
    }

}