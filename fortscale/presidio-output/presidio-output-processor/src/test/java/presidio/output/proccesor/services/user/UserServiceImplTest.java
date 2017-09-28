package presidio.output.proccesor.services.user;

import org.junit.Assert;
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
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserQuery;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.services.users.UserPersistencyServiceImpl;
import presidio.output.processor.services.user.UserScoreService;
import presidio.output.processor.services.user.UserServiceImpl;
import presidio.output.processor.services.user.UsersAlertData;

import java.util.*;

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
        usersIDForBatch.add("user1");
        usersIDForBatch.add("user2");
        usersIDForBatch.add("user3");

        Map<String, UsersAlertData> newUsersScore = new HashMap<>();
        newUsersScore.put("user1", new UsersAlertData(80D, 1));
        newUsersScore.put("user2", new UsersAlertData(50D, 1));
        newUsersScore.put("user3", new UsersAlertData(30D, 1));

        Mockito.when(this.mockUserPresistency.find(Mockito.any(UserQuery.class))).thenAnswer(new Answer<Page>() {
            @Override
            public Page answer(InvocationOnMock invocation) throws Throwable {
                UserQuery query = (UserQuery) invocation.getArguments()[0];
                if (query.getPageNumber() == 0) {
                    return usersPage;
                } else {
                    return null;
                }
            }
        });

        List<User> changedUsers = Whitebox.invokeMethod(userService, "updateUserAlertDataForBatch", newUsersScore, usersIDForBatch);
        Assert.assertEquals(2, changedUsers.size());
        Assert.assertEquals(80D, changedUsers.get(0).getScore(), 0.00001);
        Assert.assertEquals(30D, changedUsers.get(1).getScore(), 0.00001);


    }

}
