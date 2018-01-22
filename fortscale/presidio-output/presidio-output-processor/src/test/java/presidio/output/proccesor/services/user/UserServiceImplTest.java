package presidio.output.proccesor.services.user;

import fortscale.domain.core.EventResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import presidio.output.commons.services.user.UserSeverityService;
import presidio.output.commons.services.user.UserSeverityServiceImpl;
import presidio.output.domain.records.PresidioRange;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.alerts.AlertPersistencyServiceImpl;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.services.users.UserPersistencyServiceImpl;
import presidio.output.processor.services.user.UserScoreService;
import presidio.output.processor.services.user.UserServiceImpl;
import presidio.output.processor.services.user.UsersAlertData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private AlertPersistencyService mockAlertPersistency;
    private UserSeverityService mockUserSeverityService;

    private Page<Alert> emptyAlertPage;

    private final List<String> collectionNames = new ArrayList<>(Arrays.asList("output_authentication_enriched_events", "output_file_enriched_events",
            "output_active_directory_enriched_events"));


    @Before
    public void setup() {
        mockUserPresistency = Mockito.mock(UserPersistencyServiceImpl.class);
        mockEventPersistency = Mockito.mock(EventPersistencyService.class);
        mockUserScoreService = Mockito.mock(UserScoreService.class);
        mockAlertPersistency = Mockito.mock(AlertPersistencyServiceImpl.class);
        mockUserSeverityService = Mockito.mock(UserSeverityService.class);
        Map<UserSeverity, PresidioRange<Double>> severityRangeMap = new LinkedHashMap<>();
        severityRangeMap.put(UserSeverity.LOW, new PresidioRange<>(0d, 30d));
        severityRangeMap.put(UserSeverity.MEDIUM, new PresidioRange<>(30d, 60d));
        severityRangeMap.put(UserSeverity.HIGH, new PresidioRange<>(60d, 90d));
        severityRangeMap.put(UserSeverity.CRITICAL, new PresidioRange<>(90d, 100d));
        Mockito.when(mockUserSeverityService.getSeveritiesMap(false)).thenReturn(new UserSeverityServiceImpl.UserScoreToSeverity(severityRangeMap));

        userService = new UserServiceImpl(mockEventPersistency,
                mockUserPresistency,
                mockAlertPersistency,
                mockUserScoreService,
                mockUserSeverityService,
                ALERT_EFFECTIVE_DURATION_IN_DAYS,
                1000);
        emptyAlertPage = new PageImpl<>(Collections.emptyList());
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
        newUsersScore.put(usersWithOldScore.get(0).getId(), new UsersAlertData(80D, 1, null, new ArrayList<String>()));
        newUsersScore.put(usersWithOldScore.get(1).getId(), new UsersAlertData(50D, 1, null, new ArrayList<String>()));
        newUsersScore.put(usersWithOldScore.get(2).getId(), new UsersAlertData(30D, 1, null, new ArrayList<String>()));

        Mockito.when(this.mockUserPresistency.findByIds(Mockito.any(Set.class), Mockito.any(PageRequest.class))).thenAnswer(invocation -> {
            Set<String> userIds = (Set<String>) invocation.getArguments()[0];
            PageRequest pageContext = (PageRequest) invocation.getArguments()[1];

            if (pageContext.getPageNumber() == 0) {
                return usersPage;
            } else {
                return null;
            }
        });

        List<User> changedUsers = Whitebox.invokeMethod(userService, "updateUserAlertDataForBatch", newUsersScore, usersIDForBatch);
        assertEquals(2, changedUsers.size());
        assertEquals(80D, changedUsers.get(0).getScore(), 0.00001);
        assertEquals(30D, changedUsers.get(1).getScore(), 0.00001);


    }

    @Test
    public void testAddUserAlertData() {
        User user1 = new User("user1", null, null, 50, null, null, null, UserSeverity.CRITICAL, 0);
        List<String> classification1 = null, classification2, classification3, classification4;
        List<String> indicators1 = null, indicators2, indicators3;
        classification2 = new ArrayList<>(Arrays.asList("a", "b"));
        indicators2 = new ArrayList<>(Arrays.asList("c", "d"));
        classification3 = new ArrayList<>(Arrays.asList("a", "c"));
        classification4 = new ArrayList<>(Arrays.asList("c"));
        indicators3 = new ArrayList<>(Arrays.asList("c", "e"));
        assertEquals(null, user1.getIndicators());
        assertEquals(null, user1.getAlertClassifications());
        // adding empty classification list and empty indicator list
        UsersAlertData usersAlertData1 = new UsersAlertData(0, 0, null, indicators1);
        userService.addUserAlertData(user1, usersAlertData1);
        assertEquals(null, user1.getIndicators());
        assertEquals(null, user1.getAlertClassifications());
        // Adding classification list with 2 classifications but saving only the first one on the user and adding 2 indicators
        UsersAlertData usersAlertData2 = new UsersAlertData(0, 0, classification2.get(0), indicators2);
        userService.addUserAlertData(user1, usersAlertData2);
        assertEquals(2, user1.getIndicators().size());
        assertEquals(1, user1.getAlertClassifications().size());
        // adding classification list of 2 classifications that the first one already exists on the user and adding 2 indicators one of which already exists
        UsersAlertData usersAlertData3 = new UsersAlertData(0, 0, classification3.get(0), indicators3);
        userService.addUserAlertData(user1, usersAlertData3);
        assertEquals(3, user1.getIndicators().size());
        assertEquals(1, user1.getAlertClassifications().size());
        // adding existing classifications and indicators
        UsersAlertData usersAlertData4 = new UsersAlertData(0, 0, null, indicators1);
        userService.addUserAlertData(user1, usersAlertData4);
        assertEquals(3, user1.getIndicators().size());
        assertEquals(1, user1.getAlertClassifications().size());
        // adding new classification but existing indicator
        UsersAlertData usersAlertData5 = new UsersAlertData(0, 0, classification4.get(0), indicators1);
        userService.addUserAlertData(user1, usersAlertData5);
        assertEquals(3, user1.getIndicators().size());
        assertEquals(2, user1.getAlertClassifications().size());

    }

    @Test
    public void createUserFromEnrichedEventWithIsAdminFalseTest() {
        EventResult result = EventResult.SUCCESS;
        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("isUserAdmin", "false");
        EnrichedEvent enrichedEvent = new EnrichedEvent(Instant.now(), Instant.now(), "event1", "Active Directory", "userId1", "userName1",
                "userDisplayName1", "Active Directory", "User Logged On", new ArrayList<>(), result, "success", additionalInfo);
        Mockito.when(this.mockEventPersistency.findLatestEventForUser(Mockito.any(String.class), Mockito.any(List.class))).thenReturn(enrichedEvent);

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
        Mockito.when(this.mockEventPersistency.findLatestEventForUser(Mockito.any(String.class), Mockito.any(List.class))).thenReturn(enrichedEvent);

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
        Mockito.when(this.mockEventPersistency.findLatestEventForUser(Mockito.any(String.class), Mockito.any(List.class))).thenReturn(enrichedEvent);

        User user = userService.createUserEntity(userId);
        assertEquals(0, user.getTags().size());
        assertEquals(userId, user.getUserId());
        assertEquals(userName, user.getUserName());
        assertEquals(userName, user.getIndexedUserName());
        assertEquals(userDisplayName, user.getUserDisplayName());
    }
}
