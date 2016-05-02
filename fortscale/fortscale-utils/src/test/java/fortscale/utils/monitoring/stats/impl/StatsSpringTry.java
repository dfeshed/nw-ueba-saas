package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.config.StandardStatsServiceConfig;
import fortscale.utils.monitoring.stats.engine.StatsEngineBaseTest;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.monitoring.stats.engine.topic.StatsTopicEngine;

import fortscale.utils.monitoring.stats.impl.engine.testing.StatsTestingEngine;
import fortscale.utils.monitoring.stats.models.engine.EngineData;
import fortscale.utils.spring.MainProcessPropertiesConfigurer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
// See https://spring.io/blog/2011/06/21/spring-3-1-m2-testing-with-configuration-classes-and-profiles

public class StatsSpringTry {

    private static final Logger logger = Logger.getLogger(StatsSpringTry.class);


    @Configuration
    @PropertySource("classpath:META-INF/fortscale-config.properties")
    @Import(StandardStatsServiceConfig.class)
    static public class StatSpringConfig {

        @Bean
        public static MainProcessPropertiesConfigurer mainProcessPropertiesConfigurer() {
            Properties properties = new Properties();
           // properties.put("kafka.broker.list", "dev-gaash:9092");
           // properties.put("fortscale.monitoring.stats.engine.topic.topicName", "try");
            MainProcessPropertiesConfigurer configurer = new MainProcessPropertiesConfigurer(properties);

            return configurer;
        }
    }


    @Autowired
    @Qualifier("standardStatsService")
    StatsService statsService;

    ////@Test
    public void testSpring1() {


        StatsTopicEngine engine = (StatsTopicEngine)statsService.getStatsEngine();

        List<StatsEngineMetricsGroupData> metricGroupDataList = StatsEngineBaseTest.getMetricGroupData();
        EngineData engineData = engine.statsEngineDataToModelData(metricGroupDataList);

        String msg = engine.engineDataToMetricsTopicMessageString(engineData, 77777);
        System.out.println(msg);

        engine.writeEngineDataToMetricsTopic(engineData, 88888);

    }


}




