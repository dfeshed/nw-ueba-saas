package spring;

import fortscale.presidio.output.client.api.AlertsPresidioOutputClient;
import fortscale.presidio.output.client.api.UsersPresidioOutputClient;
import fortscale.spring.PresidioUiServiceConfiguration;
import fortscale.utils.mongodb.config.SpringMongoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.client.client.ApiException;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("mock")
@ContextConfiguration(classes = {PresidioUiServiceConfiguration.class,  SpringMongoConfiguration.class})
@TestPropertySource("classpath:test.properties")
@Import(SpringContextTestsConfiguration.class)
public class SpringContextTest {

        @Autowired
        private AlertsPresidioOutputClient fakeRemoteAlertClientService;

        @Autowired
        private UsersPresidioOutputClient fakeRemoteUserClientService;


        @Test
        public void contextLoads() {
        }


        @Test
        public void testFake() throws ApiException {

                Assert.assertTrue(fakeRemoteAlertClientService.getConterollerApi().getAlerts(null).getAlerts().size()>0);
                Assert.assertTrue(fakeRemoteUserClientService.getConterollerApi().getUsers(null).getUsers().size()>0);
        }



}
