package presidio.output.commons.services.user;

import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.commons.services.spring.UserUpdatePropertiesTestConfiguration;
import presidio.output.domain.records.events.*;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
        generateAuthenticationEnrichedEvent(eventDate.minus(1, ChronoUnit.MINUTES), "userName1", "userId", "userDisplayName1", additionalInfo);
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
        Assert.assertTrue(CollectionUtils.isEmpty(userUpdated.getTags()));
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
        Assert.assertTrue(CollectionUtils.isEmpty(userUpdated.getTags()));
    }

    @Test
    public void updateUserPropertiesWithPrintEvent() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generatePrintEnrichedEvent(eventDate, "userName1", "userId", "userDisplayName1", null);
        User user = generateUserAndSave("userId", "userName", "userDisplayName", false);
        User userUpdated = userPropertiesUpdateService.userPropertiesUpdate(user);
        Assert.assertEquals("userName1", userUpdated.getUserName());
        Assert.assertEquals("userDisplayName1", userUpdated.getUserDisplayName());
        Assert.assertEquals("userName1", userUpdated.getIndexedUserName());
        Assert.assertEquals("userName1", userUpdated.getUserDisplayNameSortLowercase());
        Assert.assertTrue(CollectionUtils.isEmpty(userUpdated.getTags()));
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

    @Test
    public void updateUserPropertiesMissingDisplayName_shouldSetNullAsDisplayName() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generateAuthenticationEnrichedEvent(eventDate, "userName1", "userId", null, additionalInfo);
        User user = generateUserAndSave("userId", "userName", "userDisplayName", true);
        User userUpdated = userPropertiesUpdateService.userPropertiesUpdate(user);
        Assert.assertEquals("userName1", userUpdated.getUserName());
        Assert.assertNull(userUpdated.getUserDisplayName());
    }

    @Test
    public void updateUserPropertiesUserWithoutDisplayName_shouldSetUpdatedDisplayName() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generateAuthenticationEnrichedEvent(eventDate, "userName1", "userId", "displayName1", additionalInfo);
        User user = generateUserAndSave("userId", "userName", null, true);
        User userUpdated = userPropertiesUpdateService.userPropertiesUpdate(user);
        Assert.assertEquals("userName1", userUpdated.getUserName());
        Assert.assertEquals("displayName1", userUpdated.getUserDisplayName());
    }

    @Test
    public void updateUserAdminTag_AddAdmin() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generateAuthenticationEnrichedEvent(eventDate, "userName1", "userId", "userDisplayName1", additionalInfo);
        String someTagName = "someTag";
        List<String> tags = new ArrayList<>();
        tags.add(someTagName);
        User user = generateUserAndSave("userId", "userName", "userDisplayName", tags);
        User userUpdated = userPropertiesUpdateService.userPropertiesUpdate(user);
        Assert.assertEquals("userName1", userUpdated.getUserName());
        Assert.assertEquals("userDisplayName1", userUpdated.getUserDisplayName());
        Assert.assertEquals("userName1", userUpdated.getIndexedUserName());
        Assert.assertEquals("userName1", userUpdated.getUserDisplayNameSortLowercase());
        Assert.assertEquals(2, userUpdated.getTags().size());
        Assert.assertTrue(userUpdated.getTags().contains(someTagName));
        Assert.assertTrue(userUpdated.getTags().contains(TAG_ADMIN));
    }

    @Test
    public void updateUserAdminTag_AddAdminTagsNull() {
        Instant eventDate = Instant.now();
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        generateAuthenticationEnrichedEvent(eventDate, "userName1", "userId", "userDisplayName1", additionalInfo);
        String someTagName = "someTag";
        List<String> tags = new ArrayList<>();
        tags.add(someTagName);
        User user = generateUserAndSave("userId", "userName", "userDisplayName", null);
        User userUpdated = userPropertiesUpdateService.userPropertiesUpdate(user);
        Assert.assertEquals("userName1", userUpdated.getUserName());
        Assert.assertEquals("userDisplayName1", userUpdated.getUserDisplayName());
        Assert.assertEquals("userName1", userUpdated.getIndexedUserName());
        Assert.assertEquals("userName1", userUpdated.getUserDisplayNameSortLowercase());
        Assert.assertEquals(1, userUpdated.getTags().size());
        Assert.assertTrue(userUpdated.getTags().contains(TAG_ADMIN));
    }


    @Test
    public void updateUserAdminTag_RemoveAdmin() {
        Instant eventDate = Instant.now();
        generateAuthenticationEnrichedEvent(eventDate, "userName1", "userId", "userDisplayName1", null);
        String someTagName = "someTag";
        List<String> tags = new ArrayList<>();
        tags.add(someTagName);
        tags.add(TAG_ADMIN);
        User user = generateUserAndSave("userId", "userName", "userDisplayName", tags);
        User userUpdated = userPropertiesUpdateService.userPropertiesUpdate(user);
        Assert.assertEquals("userName1", userUpdated.getUserName());
        Assert.assertEquals("userDisplayName1", userUpdated.getUserDisplayName());
        Assert.assertEquals("userName1", userUpdated.getIndexedUserName());
        Assert.assertEquals("userName1", userUpdated.getUserDisplayNameSortLowercase());
        Assert.assertNotNull(userUpdated.getTags());
        Assert.assertEquals(1, userUpdated.getTags().size());
        Assert.assertTrue(userUpdated.getTags().contains(someTagName));
    }

    private User generateUserAndSave(String userId, String userName, String displayName, boolean tagAdmin) {
        List<String> tags = new ArrayList<>();
        if (tagAdmin) {
            tags.add(TAG_ADMIN);
        }
        return generateUserAndSave(userId, userName, displayName, tags);
    }

    private User generateUserAndSave(String userId, String userName, String displayName, List<String> tags) {
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

    private void generatePrintEnrichedEvent(Instant eventDate, String userName, String userId, String userDisplayName, Map<String, String> additionalInfo) {
        PrintEnrichedEvent printEnrichedEvent = new PrintEnrichedEvent(Instant.now(), eventDate, "eventId",
                "schema", userId, userName, userDisplayName,
                "dataSource", "operationType", null, EventResult.SUCCESS,
                "resultCode", additionalInfo, "srcMachineId", "srcMachineCluster",
                "printerId", "printareName", "srcFilePath", "srcFolderPath",
                "srcFileExtension", false, 10l, 10l);
        saveEvent(printEnrichedEvent, Schema.PRINT);
    }

    private void generateAuthenticationEnrichedEvent(Instant eventDate, String userName, String userId, String userDisplayName, Map<String, String> additionalInfo) {
        EnrichedEvent event = new AuthenticationEnrichedEvent(eventDate, eventDate, "eventId1", Schema.AUTHENTICATION.toString(), userId, userName, userDisplayName,
                "dataSource", "User authenticated through Kerberos", new ArrayList<String>(), EventResult.SUCCESS,
                "SUCCESS", additionalInfo);
        saveEvent(event, Schema.AUTHENTICATION);
    }
}
