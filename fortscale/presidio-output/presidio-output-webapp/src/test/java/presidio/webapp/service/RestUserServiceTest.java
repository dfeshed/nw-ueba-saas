package presidio.webapp.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.webapp.model.UserQuery;
import presidio.webapp.model.UsersWrapper;
import presidio.webapp.spring.OutputWebappConfigurationTest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.notNull;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = OutputWebappConfigurationTest.class)
public class RestUserServiceTest {

    @Autowired
    AlertPersistencyService alertService;

    @Autowired
    UserPersistencyService userPersistencyService;

    @Autowired
    RestUserService restUserService;

    @Test
    public void testReturnUserWithoutExpand() {
        User user = createUser(1);
        when(userPersistencyService.findUserById(eq(user.getId()))).thenReturn(user);
        presidio.webapp.model.User resultUser = restUserService.getUserById("useruser1", false);
        Assert.assertNotNull(resultUser);
    }


    @Test
    public void testReturnUserWithExpand() {
        Alert alert = createAlert(1);
        Page<Alert> page = new PageImpl<Alert>(new ArrayList<>(Arrays.asList(alert)));
        when(alertService.findByUserId(eq(alert.getUserId()), notNull(PageRequest.class))).thenReturn(page);

        User user = createUser(1);
        when(userPersistencyService.findUserById(eq(user.getId()))).thenReturn(user);
        presidio.webapp.model.User resultUser = restUserService.getUserById("useruser1", true);

        Assert.assertEquals(1, resultUser.getAlerts().size());


    }

    @Test
    public void testReturnUsersWithoutExpand() {
        User user3 = createUser(3);
        user3.setScore(90);
        User user4 = createUser(4);
        user4.setScore(90);
        User user5 = createUser(5);
        user5.setScore(90);
        Page<User> page = new PageImpl<User>(new ArrayList<>(Arrays.asList(user3, user4, user5)), null, 5);
        when(userPersistencyService.find(notNull(presidio.output.domain.records.users.UserQuery.class))).thenReturn(page);
        UserQuery userQuery = new UserQuery();
        userQuery.setExpand(false);
        userQuery.setMinScore(70);
        userQuery.setMaxScore(100);
        UsersWrapper usersWrapper = restUserService.getUsers(userQuery);
        List<presidio.webapp.model.User> resultUser = usersWrapper.getUsers();

        Assert.assertEquals(3, resultUser.size());
        Assert.assertEquals(5, usersWrapper.getTotal().intValue());
    }


    @Test
    public void testReturnUsersWithExpand() {
        User user1 = createUser(1);
        User user2 = createUser(2);
        User user3 = createUser(3);
        Alert alert1 = createAlert(1);
        Alert alert2 = createAlert(2);
        Alert alert3 = createAlert(2);
        user3.setAlertsCount(0);
        user2.setAlertsCount(2);

        Page<User> userPage = new PageImpl<User>(new ArrayList<>(Arrays.asList(user1, user2, user3)));
        when(userPersistencyService.find(notNull(presidio.output.domain.records.users.UserQuery.class))).thenReturn(userPage);
        UserQuery userQuery = new UserQuery();
        userQuery.setExpand(true);
        Page<Alert> page = new PageImpl<Alert>(new ArrayList<>(Arrays.asList(alert1, alert2, alert3)));
        when(alertService.findByUserIdIn(notNull(Collection.class), notNull(PageRequest.class))).thenReturn(page);
        List<presidio.webapp.model.User> resultUser = restUserService.getUsers(userQuery).getUsers();
        resultUser.forEach(user -> {
            if (user.getId().equals("useruser1"))
                Assert.assertEquals(1, user.getAlerts().size());
            else {
                if (user.getId().equals("useruser2"))
                    Assert.assertEquals(2, user.getAlerts().size());
                else
                    Assert.assertEquals(0, user.getAlerts().size());
            }
        });
    }


    private User createUser(int number) {
        User user = new User();
        user.setUserName("user" + number);
        user.setId("useruser" + number);
        user.setAlertsCount(1);
        user.setUserDisplayName("superuser" + number);
        user.setScore(60);
        user.setSeverity(UserSeverity.MEDIUM);
        List classifications = new ArrayList(Arrays.asList("Mass Changes to Critical Enterprise Groups"));
        user.setAlertClassifications(classifications);
        return user;
    }


    private Alert createAlert(int number) {
        List<String> classifications = new ArrayList<>(Arrays.asList("Mass Changes to Critical Enterprise Groups"));
        return new Alert("useruser" + number, classifications, "user" + number,
                Instant.parse("2017-01-01T00:00:00Z").toEpochMilli(), Instant.parse("2017-01-01T11:00:00Z").toEpochMilli(),
                10, 10, AlertEnums.AlertTimeframe.DAILY, AlertEnums.AlertSeverity.CRITICAL, null);
    }
}
