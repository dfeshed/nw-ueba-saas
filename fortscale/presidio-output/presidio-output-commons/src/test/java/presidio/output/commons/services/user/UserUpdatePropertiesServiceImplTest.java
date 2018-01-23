package presidio.output.commons.services.user;


import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.commons.services.spring.UserUpdatePropertiesTestConfiguration;
import presidio.output.domain.records.events.ActiveDirectoryEnrichedEvent;
import presidio.output.domain.records.events.AuthenticationEnrichedEvent;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.FileEnrichedEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UserUpdatePropertiesTestConfiguration.class)
public class UserUpdatePropertiesServiceImplTest {

    @Autowired
    private UserPersistencyService userPersistencyService;
    @Autowired
    private UserPropertiesUpdateService userPropertiesUpdateService;
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
    public void updateUserPropertiesWithAuthenticationEvent() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generateAuthenticationEnrichedEvent(eventDate, "userName1", "userId", "userDisplayName1", additionalInfo);
        User user = generateUserAndSave("userId", "userName", "userDisplayName", false);
        User userUpdated = userPropertiesUpdateService.userPropertiesUpdate(user);
        Assert.assertEquals("userName1", userUpdated.getUserName());
        Assert.assertEquals("userDisplayName1", userUpdated.getUserDisplayName());
        Assert.assertEquals("userName1", userUpdated.getIndexedUserName());
        Assert.assertEquals("userName1", userUpdated.getUserDisplayNameSortLowercase());
        Assert.assertEquals(user.getTags().get(0), userUpdated.getTags().get(0));
    }

    @Test
    public void updateUserPropertiesWithAuthenticationEventDeleteTags() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generateAuthenticationEnrichedEvent(eventDate, "userName1", "userId", "userDisplayName1", null);
        User user = generateUserAndSave("userId", "userName", "userDisplayName", true);
        User userUpdated = userPropertiesUpdateService.userPropertiesUpdate(user);
        Assert.assertEquals("userName1", userUpdated.getUserName());
        Assert.assertEquals("userDisplayName1", userUpdated.getUserDisplayName());
        Assert.assertEquals("userName1", userUpdated.getIndexedUserName());
        Assert.assertEquals("userName1", userUpdated.getUserDisplayNameSortLowercase());
        Assert.assertEquals(null, userUpdated.getTags());
    }

    @Test
    public void updateUserPropertiesWithFileEvent() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generateFileEnrichedEvent(eventDate, "userName1", "userId", "userDisplayName1", additionalInfo);
        User user = generateUserAndSave("userId", "userName", "userDisplayName", true);
        User userUpdated = userPropertiesUpdateService.userPropertiesUpdate(user);
        Assert.assertEquals("userName1", userUpdated.getUserName());
        Assert.assertEquals("userDisplayName1", userUpdated.getUserDisplayName());
        Assert.assertEquals("userName1", userUpdated.getIndexedUserName());
        Assert.assertEquals("userName1", userUpdated.getUserDisplayNameSortLowercase());
        Assert.assertEquals(user.getTags().get(0), userUpdated.getTags().get(0));
    }

    @Test
    public void updateUserPropertiesWithActiveDirectoryEvent() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generateActiveDirectoryEnrichedEvent(eventDate, "userName1", "userId", "userDisplayName1", null);
        User user = generateUserAndSave("userId", "userName", "userDisplayName", false);
        User userUpdated = userPropertiesUpdateService.userPropertiesUpdate(user);
        Assert.assertEquals("userName1", userUpdated.getUserName());
        Assert.assertEquals("userDisplayName1", userUpdated.getUserDisplayName());
        Assert.assertEquals("userName1", userUpdated.getIndexedUserName());
        Assert.assertEquals("userName1", userUpdated.getUserDisplayNameSortLowercase());
        Assert.assertEquals(user.getTags().get(0), userUpdated.getTags().get(0));
    }

    @Test
    public void updateUserPropertiesWithAuthenticationAndActiveDirectoryEvent() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generateActiveDirectoryEnrichedEvent(eventDate, "userName1", "userId", "userDisplayName1", additionalInfo);
        generateAuthenticationEnrichedEvent(eventDate, "userName2", "userId", "userDisplayName2", additionalInfo);
        User user = generateUserAndSave("userId", "userName", "userDisplayName", false);
        User userUpdated = userPropertiesUpdateService.userPropertiesUpdate(user);
        Assert.assertEquals("userName2", userUpdated.getUserName());
        Assert.assertEquals("userDisplayName2", userUpdated.getUserDisplayName());
        Assert.assertEquals("userName2", userUpdated.getIndexedUserName());
        Assert.assertEquals("userName2", userUpdated.getUserDisplayNameSortLowercase());
        Assert.assertEquals(user.getTags().get(0), userUpdated.getTags().get(0));
    }

    @Test
    public void updateUserPropertiesNoEvents() {
        User user = generateUserAndSave("userId", "userName", "userDisplayName", false);
        User userUpdated = userPropertiesUpdateService.userPropertiesUpdate(user);
        Assert.assertNull(userUpdated);
    }

    @Test
    public void updateUserPropertiesNoChanges() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generateAuthenticationEnrichedEvent(eventDate, "userName", "userId", "userDisplayName", additionalInfo);
        User user = generateUserAndSave("userId", "userName", "userDisplayName", true);
        User userUpdated = userPropertiesUpdateService.userPropertiesUpdate(user);
        Assert.assertNull(userUpdated);
    }

    private User generateUserAndSave(String userId, String userName, String displayName, boolean tagAdmin) {
        List<String> tags = null;
        if (tagAdmin) {
            tags = new ArrayList<>();
            tags.add(TAG_ADMIN);
        }
        User user1 = new User(userId, userName, displayName, 0d, null, null, tags, UserSeverity.LOW, 0);
        userPersistencyService.save(user1);
        return user1;
    }

    private void saveEvent(EnrichedEvent event, Schema schema) {
        List<EnrichedEvent> events = new ArrayList<>();
        events.add(event);
        try {
            eventPersistencyService.store(schema, events);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    private void generateFileEnrichedEvent(Instant eventDate, String userName, String userId, String userDisplayName, Map<String, String> additionalInfo) {
        EnrichedEvent event = new FileEnrichedEvent(eventDate, eventDate, "eventId", Schema.FILE.toString(),
                userId, userName, userDisplayName, "dataSource", "oppType", new ArrayList<String>(),
                EventResult.FAILURE, "resultCode", additionalInfo, "absoluteSrcFilePath", "absoluteDstFilePath",
                "absoluteSrcFolderFilePath", "absoluteDstFolderFilePath", 20L, true, true);
        saveEvent(event, Schema.FILE);

    }

    private void generateActiveDirectoryEnrichedEvent(Instant eventDate, String userName, String userId, String userDisplayName, Map<String, String> additionalInfo) {
        EnrichedEvent event = new ActiveDirectoryEnrichedEvent(eventDate, eventDate, "eventId", Schema.ACTIVE_DIRECTORY.toString(),
                userId, userName, userDisplayName, "dataSource", "USER_ACCOUNT_TYPE_CHANGED",
                new ArrayList<String>(), EventResult.SUCCESS, "resultCode", additionalInfo, Boolean.FALSE, "objectId");
        saveEvent(event, Schema.ACTIVE_DIRECTORY);
    }

    private void generateAuthenticationEnrichedEvent(Instant eventDate, String userName, String userId, String userDisplayName, Map<String, String> additionalInfo) {
        EnrichedEvent event = new AuthenticationEnrichedEvent(eventDate, eventDate, "eventId1", Schema.AUTHENTICATION.toString(), userId, userName, userDisplayName,
                "dataSource", "User authenticated through Kerberos", new ArrayList<String>(), EventResult.SUCCESS,
                "SUCCESS", additionalInfo);
        saveEvent(event, Schema.AUTHENTICATION);
    }
}
