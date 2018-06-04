package presidio.output.forwarder;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.forwarder.spring.OutputForwarderTestConfigBeans;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
public class UsersForwarderTest {

    @Configuration
    @Import(OutputForwarderTestConfigBeans.class)
    static class ContextConfiguration {
        
        @Autowired
        ForwarderConfiguration forwarderConfiguration;

        @Autowired
        ForwarderStrategyFactory forwarderStrategyFactory;

        @Bean
        public UserPersistencyService userPersistencyService() {
            User user = new User("test", "test1", "test3", 90.0d, new ArrayList<>(), new ArrayList<>(), null, UserSeverity.CRITICAL, 0);
            user.setId("c678bb28-f795-402c-8d64-09f26e82807d");
            UserPersistencyService usersPersistencyService = Mockito.mock(UserPersistencyService.class);
            Mockito.when(usersPersistencyService.findUsersByUpdatedDate(Mockito.any(Instant.class),Mockito.any(Instant.class))).thenReturn(Collections.singletonList(user).stream());
            return usersPersistencyService;
        }

        @Bean
        public UsersForwarder usersForwarder() {
            return new UsersForwarder(userPersistencyService(), forwarderConfiguration, forwarderStrategyFactory);
        }
    }

    @Autowired
    UsersForwarder usersForwarder;

    @Autowired
    MemoryStrategy memoryForwarder;


    @Test
    public void testUsersForwarding() {
        usersForwarder.forward(Instant.now(), Instant.now());
        Assert.assertEquals(1,memoryForwarder.allMessages.size());
        Assert.assertEquals("{\"id\":\"c678bb28-f795-402c-8d64-09f26e82807d\",\"entitiyId\":\"test\",\"severity\":\"CRITICAL\",\"alertsCount\":0}",memoryForwarder.allMessages.get(0));
    }


}
