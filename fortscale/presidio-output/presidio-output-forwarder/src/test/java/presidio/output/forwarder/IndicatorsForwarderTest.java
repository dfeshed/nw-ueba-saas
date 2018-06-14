package presidio.output.forwarder;

import fortscale.common.general.Schema;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.forwarder.spring.OutputForwarderTestConfigBeans;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
public class IndicatorsForwarderTest {

    @Configuration
    @Import(OutputForwarderTestConfigBeans.class)
    static class ContextConfiguration {

        @Autowired
        ForwarderConfiguration forwarderConfiguration;

        @Autowired
        ForwarderStrategyFactory forwarderStrategyFactory;

        @Bean
        public AlertPersistencyService alertPersistencyService() {
            Indicator indicator = new Indicator();
            indicator.setAlertId("c678bb28-f795-402c-8d64-09f26e82807d");
            indicator.setName("high_number_of_distinct_src_computer_clusters_print");
            indicator.setStartDate( new Date(1521466653));
            indicator.setEndDate( new Date(1521466653));
            indicator.setAnomalyValue("60.0");
            indicator.setSchema(Schema.PRINT);
            indicator.setType(AlertEnums.IndicatorTypes.FEATURE_AGGREGATION);
            indicator.setScoreContribution(0.19593662136570342);
            indicator.setId("c678bb28-f795-402c-8d64-09f26e82807c");
            AlertPersistencyService alertPersistencyService = Mockito.mock(AlertPersistencyService.class);
            Mockito.when(alertPersistencyService.findIndicatorByDate(Mockito.any(Instant.class),Mockito.any(Instant.class))).thenReturn(Collections.singletonList(indicator).stream());
            return alertPersistencyService;
        }

        @Bean
        public IndicatorsForwarder indicatorsForwarder() {
            return new IndicatorsForwarder(alertPersistencyService(), forwarderConfiguration, forwarderStrategyFactory);
        }
    }

    @Autowired
    IndicatorsForwarder indicatorsForwarder;

    @Autowired
    MemoryStrategy memoryStrategy;


    @Test
    public void testAlertsForwarding() {
        indicatorsForwarder.forward(Instant.now(), Instant.now());
        Assert.assertEquals(1, memoryStrategy.allMessages.size());
        Assert.assertEquals("{\"id\":\"c678bb28-f795-402c-8d64-09f26e82807c\",\"startDate\":\"1970-01-18T14:37:46.653+0000\",\"endDate\":\"1970-01-18T14:37:46.653+0000\",\"schema\":\"PRINT\",\"UebaAlertId\":\"c678bb28-f795-402c-8d64-09f26e82807d\",\"score\":0.0,\"scoreContribution\":0.19593662136570342,\"anomalyValue\":\"60.0\",\"eventsNum\":0}", memoryStrategy.allMessages.get(0));
    }


}
