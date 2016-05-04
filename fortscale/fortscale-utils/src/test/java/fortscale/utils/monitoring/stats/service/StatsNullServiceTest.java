package fortscale.utils.monitoring.stats.service;

import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.config.StandardStatsServiceConfig;
import fortscale.utils.spring.MainProcessPropertiesConfigurer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
 * This test class tests metrics group without stats service
 *
 * Actually it does not test much, just that there are no crashes
 *
 * Created by gaashh on 5/3/16.
 */

public class StatsNullServiceTest {

    private static final Logger logger = Logger.getLogger(StatsTopicServiceTest.class);

    final long FAST_DEGREE_RATE = 10;
    final long SLOW_DEGREE_RATE = FAST_DEGREE_RATE / 3;
    final long EPOCH_RATE = 60 * 10;

    final StatsService statsService = null;

    @Test
    public void testManualUpdates() {

        final long pointCount = 100;

        // Make sure stats service is null
        Assert.assertNull(statsService);

        StatsServiceTestingTrigoService fastTrigoService =
                new StatsServiceTestingTrigoService(statsService, "manual", "slow", FAST_DEGREE_RATE);

        StatsServiceTestingTrigoService slowTrigoService =
                new StatsServiceTestingTrigoService(statsService, "manual", "fast", SLOW_DEGREE_RATE);

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

            // We can't call ManualUpdateFlush() since there is no stats service
            //statsService.ManualUpdatePush();


            // Advance time
            epoch += EPOCH_RATE;


        }


    }

}








