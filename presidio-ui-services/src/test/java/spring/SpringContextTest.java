package spring;

import fortscale.presidio.output.client.api.AlertsPresidioOutputClient;
import fortscale.presidio.output.client.api.EntitiesPresidioOutputClient;
import fortscale.spring.PresidioUiServiceConfiguration;
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
@ActiveProfiles("mock-data")
@ContextConfiguration(classes = {PresidioUiServiceConfiguration.class})
@TestPropertySource("classpath:test.properties")
@Import({SpringContextTestsConfiguration.class,FongoTestsConfiguration.class})
public class SpringContextTest {

        @Autowired
        private AlertsPresidioOutputClient fakeRemoteAlertClientService;

        @Autowired
        private EntitiesPresidioOutputClient fakeRemoteUserClientService;


        @Test
        public void contextLoads() {
        }


        @Test
        public void testFake() throws ApiException {

                Assert.assertTrue(fakeRemoteAlertClientService.getConterollerApi().getAlerts(null).getAlerts().size()>0);
                Assert.assertTrue(fakeRemoteUserClientService.getConterollerApi().getEntities(null).getEntities().size()>0);
        }



}
