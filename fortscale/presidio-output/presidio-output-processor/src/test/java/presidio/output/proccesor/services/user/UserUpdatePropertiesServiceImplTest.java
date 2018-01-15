package presidio.output.proccesor.services.user;


import fortscale.common.general.Schema;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.repositories.EventRepository;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.proccesor.spring.UserUpdatePropertiesTestConfiguration;
import presidio.output.processor.services.user.UserPropertiesUpdateService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


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
        Schema[] schemas= Schema.values();
        for (Schema schema : schemas){
            eventPersistencyService.remove(schema, Instant.EPOCH,Instant.now());
        }
    }


    @Test
    public void updateUserPropertiesWithAutonticationEvent() {
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
}
