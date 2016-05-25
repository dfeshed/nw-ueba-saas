package fortscale.utils.monitoring.stats.service;

import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


/**
 *
 * This test class tests metrics group without stats service
 *
 * Actually it does not test much, just that there are no crashes
 *
 * Created by gaashh on 5/3/16.
 */

public class StatsNullServiceTest {

    private static final Logger logger = Logger.getLogger(StatsNullServiceTest.class);

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

            // We can't call ManualUpdateFlush() since there is no stats service
            //statsService.ManualUpdatePush();


            // Advance time
            epoch += EPOCH_RATE;


        }


    }

}








