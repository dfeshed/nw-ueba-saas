package fortscale.utils.monitoring.stats.service;

import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.config.StandardStatsServiceConfig;
import fortscale.utils.spring.MainProcessPropertiesConfigurer;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Properties;

/**
 *
 * This test class tests the stats service with topic engine.
 *
 * Actually it does not test the output, it just makes sure that everything works as a service (e.g. spring problems)
 *
 * The test requires Kafka, hence it would be disabled by default
 *
 * Created by gaashh on 5/3/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
// See https://spring.io/blog/2011/06/21/spring-3-1-m2-testing-with-configuration-classes-and-profiles
public class StatsTopicServicePeriodicTest {

    private static final Logger logger = Logger.getLogger(StatsTopicServicePeriodicTest.class);

    final long FAST_DEGREE_RATE = 10;
    final long SLOW_DEGREE_RATE = FAST_DEGREE_RATE / 3;
    final long EPOCH_RATE = 60 * 10;

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
    //@Qualifier("standardStatsService")
    StatsService statsService;



    @Test
    //@Ignore
    public void testPeriodicUpdates() {

        final long pointCount = 100;

        Assert.assertNotNull(statsService);

        StatsServiceTestingTrigoService fastTrigoService =
                 new StatsServiceTestingTrigoService(statsService, "periodic", "slow", FAST_DEGREE_RATE);

        StatsServiceTestingTrigoService slowTrigoService =
                 new StatsServiceTestingTrigoService(statsService, "periodic", "fast", SLOW_DEGREE_RATE);

        long epoch = LocalDateTime.of(2016,1,1,0,0,0,0).toEpochSecond(ZoneOffset.UTC);

        for (long n = 0 ; n < pointCount ; n++) {

            fastTrigoService.doIt();
            slowTrigoService.doIt();

            // Simulate periodic update
            statsService.writeMetricsGroupsToEngine(epoch);

            // Advance time
            epoch += EPOCH_RATE;
        }

        // Do one big push to check message split
        statsService.ManualUpdatePush();


    }

}







