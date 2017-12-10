package presidio.output.proccesor.services.user;

import fortscale.domain.core.EventResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.services.users.UserPersistencyServiceImpl;
import presidio.output.processor.services.user.UserScoreService;
import presidio.output.processor.services.user.UserServiceImpl;
import presidio.output.processor.services.user.UsersAlertData;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by shays on 27/08/2017.
 */

public class UserServiceImplTest {

    public static final int ALERT_EFFECTIVE_DURATION_IN_DAYS = 90;

    private UserServiceImpl userService;

    private UserPersistencyService mockUserPresistency;
    private EventPersistencyService mockEventPersistency;
    private UserScoreService mockUserScoreService;

    private Page<Alert> emptyAlertPage;


    @Before
    public void setup() {
        mockUserPresistency = Mockito.mock(UserPersistencyServiceImpl.class);
        mockEventPersistency = Mockito.mock(EventPersistencyService.class);
        mockUserScoreService = Mockito.mock(UserScoreService.class);

        userService = new UserServiceImpl(mockEventPersistency,
                mockUserPresistency,
                mockUserScoreService,
                ALERT_EFFECTIVE_DURATION_IN_DAYS,
                1000);
        emptyAlertPage = new PageImpl<Alert>(Collections.emptyList());
    }


    @Test
    public void testUpdateUserScoreBatch() throws Exception {
        List<User> usersWithOldScore = Arrays.asList(
                new User("user1", null, null, 50, null, null, null, UserSeverity.CRITICAL, 0),
                new User("user2", null, null, 50, null, null, null, UserSeverity.CRITICAL, 0),
                new User("user3", null, null, 50, null, null, null, UserSeverity.CRITICAL, 0)
        );

        Pageable pageable1 = new PageRequest(0, 3);
        Page<User> usersPage = new PageImpl<>(usersWithOldScore, pageable1, 3);

        Set<String> usersIDForBatch = new HashSet<>();
        usersIDForBatch.add(usersWithOldScore.get(0).getId());
        usersIDForBatch.add(usersWithOldScore.get(1).getId());
        usersIDForBatch.add(usersWithOldScore.get(2).getId());

        Map<String, UsersAlertData> newUsersScore = new HashMap<>();
        newUsersScore.put(usersWithOldScore.get(0).getId(), new UsersAlertData(80D, 1));
        newUsersScore.put(usersWithOldScore.get(1).getId(), new UsersAlertData(50D, 1));
        newUsersScore.put(usersWithOldScore.get(2).getId(), new UsersAlertData(30D, 1));

        Mockito.when(this.mockUserPresistency.findByIds(Mockito.any(Set.class),Mockito.any(PageRequest.class))).thenAnswer(new Answer<Page>() {
            @Override
            public Page answer(InvocationOnMock invocation) throws Throwable {
                Set<String> userIds = (Set<String>) invocation.getArguments()[0];
                PageRequest pageContext = (PageRequest) invocation.getArguments()[1];

                if (pageContext.getPageNumber() == 0) {
                    return usersPage;
                } else {
                    return null;
                }
            }
        });

        List<User> changedUsers = Whitebox.invokeMethod(userService, "updateUserAlertDataForBatch", newUsersScore, usersIDForBatch);
        assertEquals(2, changedUsers.size());
        assertEquals(80D, changedUsers.get(0).getScore(), 0.00001);
        assertEquals(30D, changedUsers.get(1).getScore(), 0.00001);


    }

    @Test
    public void testSetUserAlertData() {
        String date = new Date().toString();
        User user1 = new User("user1", null, null, 50, null, null, null, UserSeverity.CRITICAL, 0);
        List<String> classification1 = null, classification2, classification3;
        List<String> indicators1 = null, indicators2, indicators3;
        classification2 = new ArrayList<>(Arrays.asList("a", "b"));
        indicators2 = new ArrayList<>(Arrays.asList("c", "d"));
        classification3 = new ArrayList<>(Arrays.asList("a", "c"));
        indicators3 = new ArrayList<>(Arrays.asList("c", "e"));
        assertEquals(null, user1.getIndicators());
        assertEquals(null, user1.getAlertClassifications());
        userService.setUserAlertData(user1, classification1, indicators1, AlertEnums.AlertSeverity.CRITICAL);
        assertEquals(null, user1.getIndicators());
        assertEquals(null, user1.getAlertClassifications());
        userService.setUserAlertData(user1, classification2, indicators2, AlertEnums.AlertSeverity.CRITICAL);
        assertEquals(2, user1.getIndicators().size());
        assertEquals(2, user1.getAlertClassifications().size());
        userService.setUserAlertData(user1, classification3, indicators3, AlertEnums.AlertSeverity.CRITICAL);
        assertEquals(3, user1.getIndicators().size());
        assertEquals(3, user1.getAlertClassifications().size());
        userService.setUserAlertData(user1, classification1, indicators1, AlertEnums.AlertSeverity.CRITICAL);
        assertEquals(3, user1.getIndicators().size());
        assertEquals(3, user1.getAlertClassifications().size());

    }

    @Test
    public void createUserFromEnrichedEventWithIsAdminFalseTest() {
        EventResult result = EventResult.SUCCESS;
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "false");
        EnrichedEvent enrichedEvent = new EnrichedEvent(Instant.now(), Instant.now(), "event1", "Active Directory", "userId1", "userName1",
                "userDisplayName1", "Active Directory", "User Logged On", new ArrayList<>(), result, "success", additionalInfo);
        Mockito.when(this.mockEventPersistency.findLatestEventForUser(Mockito.any(String.class))).thenReturn(enrichedEvent);

        User user = userService.createUserEntity("userId1");
        assertEquals(0, user.getTags().size());
    }

    @Test
    public void createUserFromEnrichedEventWithIsAdminTrueTest() {
        EventResult result = EventResult.SUCCESS;
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "true");
        EnrichedEvent enrichedEvent = new EnrichedEvent(Instant.now(), Instant.now(), "event1", "Active Directory", "userId1", "userName1",
                "userDisplayName1", "Active Directory", "User Logged On", new ArrayList<>(), result, "success", additionalInfo);
        Mockito.when(this.mockEventPersistency.findLatestEventForUser(Mockito.any(String.class))).thenReturn(enrichedEvent);

        User user = userService.createUserEntity("userId1");
        assertEquals(1, user.getTags().size());
        assertEquals("admin", user.getTags().get(0));
    }

    @Test
    public void createUserFromEnrichedEvent() {
        EventResult result = EventResult.SUCCESS;
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "false");
        String userId = "userId1";
        String userName = "userName1";
        String userDisplayName = "userDisplayName1";
        EnrichedEvent enrichedEvent = new EnrichedEvent(Instant.now(), Instant.now(), "event1", "Active Directory", userId, userName,
                userDisplayName, "Active Directory", "User Logged On", new ArrayList<>(), result, "success", additionalInfo);
        Mockito.when(this.mockEventPersistency.findLatestEventForUser(Mockito.any(String.class))).thenReturn(enrichedEvent);

        User user = userService.createUserEntity(userId);
        assertEquals(0, user.getTags().size());
        assertEquals(userId, user.getUserId());
        assertEquals(userName, user.getUserName());
        assertEquals(userName, user.getIndexedUserName());
        assertEquals(userDisplayName, user.getUserDisplayName());
    }
}
