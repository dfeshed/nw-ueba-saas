package presidio.webapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.rest.jsonpatch.JsonPatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.users.User;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.webapp.model.UserQuery;
import presidio.webapp.model.UsersWrapper;
import presidio.webapp.spring.RestServiceTestConfig;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.notNull;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RestServiceTestConfig.class)
public class RestUserServiceTest {

    @Autowired
    AlertPersistencyService alertPersistencyService;

    @Autowired
    UserPersistencyService userPersistencyService;

    @Autowired
    RestUserService restUserService;

    @Test
    public void testReturnUserWithoutExpand() {
        User user = createUser(1);
        when(userPersistencyService.findUserById(eq(user.getId()))).thenReturn(user);
        presidio.webapp.model.User resultUser = restUserService.getUserById(user.getId(), false);
        assertNotNull(resultUser);
        assertEquals(user.getUserId(), resultUser.getUserId());
        assertEquals(user.getAlertClassifications(), resultUser.getAlertClassifications());
        assertEquals(user.getAlertsCount(), resultUser.getAlertsCount().intValue());
        assertEquals(user.getId(), resultUser.getId());
        assertEquals(0, Double.compare(user.getScore(), resultUser.getScore().doubleValue()));
        assertEquals(user.getSeverity().toString(), resultUser.getSeverity().toString());
        assertEquals(user.getUserDisplayName(), resultUser.getUserDisplayName());
        assertEquals(user.getUserName(), resultUser.getUsername());
    }

    @Test
    public void testReturnUserWithExpand() {
        Alert alert = createAlert(1);
        Page<Alert> page = new PageImpl<Alert>(new ArrayList<>(Arrays.asList(alert)));
        when(alertPersistencyService.findByEntityId(eq(alert.getEntityId()), notNull(PageRequest.class))).thenReturn(page);

        User user = createUser(1);
        when(userPersistencyService.findUserById(eq(user.getId()))).thenReturn(user);
        presidio.webapp.model.User resultUser = restUserService.getUserById("useruser1", true);

        assertEquals(1, resultUser.getAlerts().size());
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

        assertEquals(3, resultUser.size());
        assertEquals(5, usersWrapper.getTotal().intValue());
    }

    @Test
    public void testReturnUsersWithIndicatorsFilter() {
        User user1 = createUser(1);
        user1.setIndicators(Arrays.asList("indicator1"));
        User user2 = createUser(2);
        user2.setIndicators(Arrays.asList("indicator1", "indicator2"));

        Page<User> page = new PageImpl<User>(new ArrayList<>(Arrays.asList(user2)), null, 2);
        presidio.output.domain.records.users.UserQuery domainQuery = new presidio.output.domain.records.users.UserQuery.UserQueryBuilder()
                .filterByIndicators(Arrays.asList("indicator2")).build();
        when(userPersistencyService.find(domainQuery)).thenReturn(page);
        UserQuery userQuery = new UserQuery();
        userQuery.setExpand(false);
        userQuery.setIndicatorsName(Arrays.asList("indicator2"));
        UsersWrapper usersWrapper = restUserService.getUsers(userQuery);
        List<presidio.webapp.model.User> resultUser = usersWrapper.getUsers();

        assertEquals(1, resultUser.size());
        assertEquals(2, usersWrapper.getTotal().intValue());
    }


    @Test
    public void testReturnUsersWithExpand() {
        User user1 = createUser(1);
        User user2 = createUser(2);
        User user3 = createUser(3);
        Alert alert1 = createAlert(1);
        Alert alert2 = createAlert(2);
        Alert alert3 = createAlert(3);
        Alert alert4 = createAlert(4);
        alert4.setEntityId(user2.getId());
        alert4.setEntityName(user2.getUserName());
        user2.setAlertsCount(2);

        Page<User> userPage = new PageImpl<>(new ArrayList<>(Arrays.asList(user1, user2, user3)));
        when(userPersistencyService.find(notNull(presidio.output.domain.records.users.UserQuery.class))).thenReturn(userPage);
        UserQuery userQuery = new UserQuery();
        userQuery.setExpand(true);
        Page<Alert> firstPage = new PageImpl<>(new ArrayList<>(Arrays.asList(alert1)));
        Page<Alert> secondPage = new PageImpl<>(new ArrayList<>(Arrays.asList(alert4, alert2)));
        Page<Alert> thirdPage = new PageImpl<>(new ArrayList<>(Arrays.asList(alert3)));
        when(alertPersistencyService.findByEntityId(notNull(String.class), notNull(PageRequest.class))).thenReturn(firstPage, secondPage, thirdPage);
        List<presidio.webapp.model.User> resultUser = restUserService.getUsers(userQuery).getUsers();
        resultUser.forEach(user -> {
            if (user.getId().equals(user1.getId()) || user.getId().equals(user3.getId()))
                Assert.assertEquals(1, user.getAlerts().size());
            if (user.getId().equals("useruser1"))
                assertEquals(1, user.getAlerts().size());
            else {
                if (user.getId().equals(user2.getId())) {
                    Assert.assertEquals(2, user.getAlerts().size());
                }
            }
        });
    }

    @Test
    public void testUpdateUser_addFirstTag() throws IOException {
        User user = createUser(1);

        String patchOperationString = "{\"operations\":[{ \"op\": \"add\", \"path\": \"/tags/-\", \"value\":\"1\"}]}";
        JsonNode jsonNode = ObjectMapperProvider.defaultJsonObjectMapper().readTree(patchOperationString);

        when(userPersistencyService.findUserById(anyString())).thenReturn(user);
        when(userPersistencyService.save(Matchers.any(User.class))).thenReturn(user);

        presidio.webapp.model.User updatedUser = restUserService.updateUser(user.getId(), JsonPatch.fromJson(jsonNode));
        assertNotNull(updatedUser.getTags());
        assertEquals(1, updatedUser.getTags().size());
    }

    @Test
    public void testUpdateUser_addTag() throws IOException {
        User user = createUser(1);
        user.setTags(Arrays.asList("Tag"));

        String patchOperationString = "{\"operations\":[{ \"op\": \"add\", \"path\": \"/tags/-\", \"value\":\"1\"}]}";
        JsonNode jsonNode = ObjectMapperProvider.defaultJsonObjectMapper().readTree(patchOperationString);

        when(userPersistencyService.findUserById(anyString())).thenReturn(user);
        when(userPersistencyService.save(Matchers.any(User.class))).thenReturn(user);

        presidio.webapp.model.User updatedUser = restUserService.updateUser(user.getId(), JsonPatch.fromJson(jsonNode));
        assertNotNull(updatedUser.getTags());
        assertEquals(2, updatedUser.getTags().size());
    }

    @Test
    public void testUpdateUser_addExistingTag() throws IOException {
        User user = createUser(1);
        user.setTags(Arrays.asList("Tag"));

        String patchOperationString = "{\"operations\":[{ \"op\": \"add\", \"path\": \"/tags/-\", \"value\":\"Tag\"}]}";
        JsonNode jsonNode = ObjectMapperProvider.defaultJsonObjectMapper().readTree(patchOperationString);

        when(userPersistencyService.findUserById(anyString())).thenReturn(user);
        when(userPersistencyService.save(Matchers.any(User.class))).thenReturn(user);

        presidio.webapp.model.User updatedUser = restUserService.updateUser(user.getId(), JsonPatch.fromJson(jsonNode));
        assertNotNull(updatedUser.getTags());
        assertEquals(1, updatedUser.getTags().size());
    }

    @Test
    public void testUpdateUser_removeTag() throws IOException {
        User user = createUser(1);
        user.setTags(Arrays.asList("Tag", "anotherTag"));

        String patchOperationString = "{\"operations\":[{ \"op\": \"remove\", \"path\": \"/tags/-\", \"value\":\"Tag\"}]}";
        JsonNode jsonNode = ObjectMapperProvider.defaultJsonObjectMapper().readTree(patchOperationString);

        when(userPersistencyService.findUserById(anyString())).thenReturn(user);
        when(userPersistencyService.save(Matchers.any(User.class))).thenReturn(user);

        presidio.webapp.model.User updatedUser = restUserService.updateUser(user.getId(), JsonPatch.fromJson(jsonNode));
        assertNotNull(updatedUser.getTags());
        assertEquals(1, updatedUser.getTags().size());
    }

    @Test
    public void testUpdateUser_removeNotExistingTag() throws IOException {
        User user = createUser(1);
        user.setTags(Arrays.asList("Tag", "anotherTag"));

        String patchOperationString = "{\"operations\":[{ \"op\": \"remove\", \"path\": \"/tags/-\", \"value\":\"notExistingTag\"}]}";
        JsonNode jsonNode = ObjectMapperProvider.defaultJsonObjectMapper().readTree(patchOperationString);

        when(userPersistencyService.findUserById(anyString())).thenReturn(user);
        when(userPersistencyService.save(Matchers.any(User.class))).thenReturn(user);

        presidio.webapp.model.User updatedUser = restUserService.updateUser(user.getId(), JsonPatch.fromJson(jsonNode));
        assertNotNull(updatedUser.getTags());
        assertEquals(2, updatedUser.getTags().size());
    }

    private User createUser(int number) {
        User user = new User();
        user.setUserName("user" + number);
        user.setId("useruser" + number);
        user.setUserId("vendorUserId" + number);
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
        return new Alert("useruser" + number, "smartId", classifications, "user" + number, "user" + number,
                Date.from(Instant.parse("2017-01-01T00:00:00Z")), Date.from(Instant.parse("2017-01-01T11:00:00Z")),
                10, 10, AlertEnums.AlertTimeframe.DAILY, AlertEnums.AlertSeverity.CRITICAL, null, 0D, "entityType");
    }
}
