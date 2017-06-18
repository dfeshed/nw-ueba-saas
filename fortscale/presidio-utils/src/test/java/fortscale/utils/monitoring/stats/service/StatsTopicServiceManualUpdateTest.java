
package fortscale.utils.monitoring.stats.service;

import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.config.StandardStatsServiceConfig;
import fortscale.utils.process.hostnameService.config.HostnameServiceConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
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
 * This test class tests the stats service with topic engine using the manual update API
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
public class StatsTopicServiceManualUpdateTest {

    private static final Logger logger = Logger.getLogger(StatsTopicServicePeriodicTest.class);

    final long FAST_DEGREE_RATE = 10;
    final long SLOW_DEGREE_RATE = FAST_DEGREE_RATE / 3;
    final long EPOCH_RATE = 60 * 10;

    @Configuration
    @PropertySource("classpath:META-INF/fortscale-config.properties")
    @Import( { StandardStatsServiceConfig.class, HostnameServiceConfig.class } )
    static public class StatSpringConfig {

        @Bean
        public static TestPropertiesPlaceholderConfigurer mainProcessPropertiesConfigurer() {
            Properties properties = new Properties();
            // properties.put("kafka.broker.list", "dev-gaash:9092");

            // Accelerate things but disable periodic metrics update
            properties.put("fortscale.monitoring.stats.service.tick.seconds", 1);

            properties.put("fortscale.monitoring.stats.service.periodicMetricsUpdate.seconds", 1);
            properties.put("fortscale.monitoring.stats.service.periodicMetricsUpdate.slip",    1);

            properties.put("fortscale.monitoring.stats.service.enginePush.seconds", 5);
            properties.put("fortscale.monitoring.stats.service.enginePush.slip",    2);

            TestPropertiesPlaceholderConfigurer configurer = new TestPropertiesPlaceholderConfigurer(properties);

            return configurer;
        }
    }


    @Autowired
    //@Qualifier("standardStatsService")
    StatsService statsService;


    @Test
    @Ignore
    public void testManualUpdates() throws InterruptedException {

        final long pointCount = 100;

        Assert.assertNotNull(statsService);

        StatsServiceTestingTrigoService fastTrigoService =
                new StatsServiceTestingTrigoService(statsService, "manual", "slow", FAST_DEGREE_RATE, true);

        StatsServiceTestingTrigoService slowTrigoService =
                new StatsServiceTestingTrigoService(statsService, "manual", "fast", SLOW_DEGREE_RATE, true);

        long epoch = LocalDateTime.of(2018,1,1,0,0,0,0).toEpochSecond(ZoneOffset.UTC);

        for (long n = 0 ; n < pointCount ; n++) {

            fastTrigoService.doIt();
            slowTrigoService.doIt();

            // Manual update
            if ( n % 2 == 0) {
                fastTrigoService.manualUpdate(epoch);
            }
            else {
                slowTrigoService.manualUpdate(epoch);
            }

            // Advance time
            epoch += EPOCH_RATE;

        }

        // Sleep to make sure engine push tick occurred
        Thread.sleep(8 * 1000);

        // ManualUpdate flush - typically not in use, hence commented out
        //statsService.manualUpdatePush();



    }

}










