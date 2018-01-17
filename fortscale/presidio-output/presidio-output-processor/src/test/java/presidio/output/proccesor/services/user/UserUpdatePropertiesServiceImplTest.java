package presidio.output.proccesor.services.user;


import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.events.ActiveDirectoryEnrichedEvent;
import presidio.output.domain.records.events.AuthenticationEnrichedEvent;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.FileEnrichedEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.proccesor.spring.UserUpdatePropertiesTestConfiguration;
import presidio.output.processor.services.user.UserPropertiesUpdateService;

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
        FileEnrichedEvent event = new FileEnrichedEvent(eventDate, eventDate, "eventId", Schema.FILE.toString(),
                "userId", "username", "userDisplayName", "dataSource", "oppType", new ArrayList<String>(),
                EventResult.FAILURE, "resultCode", new HashMap<String, String>(), "absoluteSrcFilePath", "absoluteDstFilePath",
                "absoluteSrcFolderFilePath", "absoluteDstFolderFilePath", 20L, true, true);
        List<FileEnrichedEvent> events = new ArrayList<>();
        events.add(event);

        //store the events into mongp
        try {
            eventPersistencyService.store(Schema.FILE, events);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    private void generateUserAndSave(String userId, String userName, String displayName, boolean tagAdmin) {
        List<String> tags = null;
        if (tagAdmin) {
            tags = new ArrayList<>();
            tags.add(TAG_ADMIN);
        }
        User user1 = new User(userId, userName, displayName, 0d, null, null, tags, UserSeverity.LOW, 0);
        List<User> userList = new ArrayList<>();
        userList.add(user1);

        userPersistencyService.save(userList);
    }

    private EnrichedEvent generateFileEnrichedEvent(Instant eventDate, String userName, String userId, String userDisplayName, Map<String, String> additionalInfo) {
        return new FileEnrichedEvent(eventDate, eventDate, "eventId", Schema.FILE.toString(),
                userId, userName, userDisplayName, "dataSource", "oppType", new ArrayList<String>(),
                EventResult.FAILURE, "resultCode", additionalInfo, "absoluteSrcFilePath", "absoluteDstFilePath",
                "absoluteSrcFolderFilePath", "absoluteDstFolderFilePath", 20L, true, true);

    }

    private EnrichedEvent generateActiveDirectoryEnrichedEvent(Instant eventDate, String userName, String userId, String userDisplayName, Map<String, String> additionalInfo) {
        return new ActiveDirectoryEnrichedEvent(eventDate, eventDate, "eventId", Schema.ACTIVE_DIRECTORY.toString(),
                userId, userName, userDisplayName, "dataSource", "USER_ACCOUNT_TYPE_CHANGED",
                new ArrayList<String>(), EventResult.SUCCESS, "resultCode", additionalInfo, Boolean.FALSE, "objectId");
    }

    private EnrichedEvent generateAuthenticationEnrichedEvent(Instant eventDate, String userName, String userId, String userDisplayName, Map<String, String> additionalInfo) {
        return new AuthenticationEnrichedEvent(eventDate, eventDate, "eventId1", Schema.AUTHENTICATION.toString(), userId, userName, userDisplayName,
                "dataSource", "User authenticated through Kerberos", new ArrayList<String>(), EventResult.SUCCESS,
                "SUCCESS", additionalInfo);
    }
}
