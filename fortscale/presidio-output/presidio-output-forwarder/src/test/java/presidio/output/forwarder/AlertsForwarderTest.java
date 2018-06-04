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
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.forwarder.spring.OutputForwarderTestConfigBeans;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
public class AlertsForwarderTest {

    @Configuration
    @Import(OutputForwarderTestConfigBeans.class)
    static class ContextConfiguration {

        @Autowired
        ForwarderConfiguration forwarderConfiguration;

        @Autowired
        ForwarderStrategyFactory forwarderStrategyFactory;

        @Bean
        public AlertPersistencyService alertPersistencyService() {
            Alert alert =
                    new Alert("userId", "smartId", new ArrayList<>(), "user1", "user1", new Date(1521466653), new Date(1521466653), 95.0d, 3, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 5D);
            alert.setId("c678bb28-f795-402c-8d64-09f26e82807d");
            AlertPersistencyService alertPersistencyService = Mockito.mock(AlertPersistencyService.class);
            Mockito.when(alertPersistencyService.findAlertsByDate(Mockito.any(Instant.class),Mockito.any(Instant.class))).thenReturn(Collections.singletonList(alert).stream());
            return alertPersistencyService;
        }

        @Bean
        public AlertsForwarder alertsForwarder() {
            return new AlertsForwarder(alertPersistencyService(), forwarderConfiguration, forwarderStrategyFactory);
        }
    }

    @Autowired
    AlertsForwarder alertsForwarder;

    @Autowired
    MemoryStrategy memoryStrategy;


    @Test
    public void testAlertsForwarding() {
        alertsForwarder.forward(Instant.now(), Instant.now());
        Assert.assertEquals(1, memoryStrategy.allMessages.size());
        Assert.assertEquals("{\"id\":\"c678bb28-f795-402c-8d64-09f26e82807d\",\"startDate\":\"1970-01-18T14:37:46.653+0000\",\"endDate\":\"1970-01-18T14:37:46.653+0000\",\"entitiyId\":\"user1\",\"score\":95.0,\"severity\":\"HIGH\",\"indicatorsNum\":3,\"indicatorsNames\":null,\"classifications\":[]}", memoryStrategy.allMessages.get(0));
    }


}
