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

    @Configuration
    @PropertySource("classpath:META-INF/fortscale-config.properties")
    @Import(StandardStatsServiceConfig.class)
    static public class StatSpringConfig {

        @Bean
        public static MainProcessPropertiesConfigurer mainProcessPropertiesConfigurer() {
            Properties properties = new Properties();

            //properties.put("kafka.broker.list", "dev-gaash:9092");

            // Accelerate things
            properties.put("fortscale.monitoring.stats.service.tick.seconds", 1);

            properties.put("fortscale.monitoring.stats.service.periodicMetricsUpdate.seconds", 1);
            properties.put("fortscale.monitoring.stats.service.periodicMetricsUpdate.slip",    1);

            properties.put("fortscale.monitoring.stats.service.enginePush.seconds", 5);
            properties.put("fortscale.monitoring.stats.service.enginePush.slip",    2);

            MainProcessPropertiesConfigurer configurer = new MainProcessPropertiesConfigurer(properties);

            return configurer;
        }
    }

    @Autowired
    //@Qualifier("standardStatsService")
    StatsService statsService;

    @Test
    //@Ignore
    public void testPeriodicUpdates() throws InterruptedException {

        final long pointCount = 100;

        Assert.assertNotNull(statsService);

        StatsServiceTestingTrigoService fastTrigoService =
                 new StatsServiceTestingTrigoService(statsService, "periodic", "slow", FAST_DEGREE_RATE);

        StatsServiceTestingTrigoService slowTrigoService =
                 new StatsServiceTestingTrigoService(statsService, "periodic", "fast", SLOW_DEGREE_RATE);

        for (long n = 0 ; n < pointCount ; n++) {

            fastTrigoService.doIt();
            slowTrigoService.doIt();

            // Sleep to allow periodic updates
            Thread.sleep(1000 / 4); // 1000 mSec / 4 => 4 sample per second

        }

        // Sleep to ensure engine push occurred
        Thread.sleep(8 * 1000 );

    }

}







