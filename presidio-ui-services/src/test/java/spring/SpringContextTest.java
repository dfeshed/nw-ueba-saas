package spring;

import fortscale.remote.fake.FakeRemoteAlertClientService;
import fortscale.remote.fake.FakeRemoteUserClientService;
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
@ActiveProfiles("fake")
@ContextConfiguration(classes = {PresidioUiServiceConfiguration.class,  SpringMongoConfiguration.class})
@TestPropertySource("classpath:test.properties")
@Import(SpringContextTestsConfiguration.class)
public class SpringContextTest {

        @Autowired
        private FakeRemoteAlertClientService fakeRemoteAlertClientService;

        @Autowired
        private FakeRemoteUserClientService fakeRemoteUserClientService;


        @Test
        public void contextLoads() {
        }


        @Test
        public void testFake() throws ApiException {

                Assert.assertTrue(fakeRemoteAlertClientService.getConterollerApi().getAlerts(null).getAlerts().size()>0);
                Assert.assertTrue(fakeRemoteUserClientService.getConterollerApi().getUsers(null).getUsers().size()>0);
        }



}
