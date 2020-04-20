package fortscale.utils.time.impl;

import fortscale.utils.time.SystemDateService;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by gaashh on 7/10/16.
 */
public class TestSystemDateServiceImpl {

    public void checkNormalOperation(SystemDateService dateService) throws InterruptedException {

        // Get the time current time for service
        long epoch = dateService.getEpoch();
        long epochMilli = dateService.getEpochMilli();
        Instant date = dateService.getInstant();

        // Sleep for 2 seconds
        Thread.sleep(2 * 1000);

        // Get the time again and calc the difference
        long delta = dateService.getEpoch() - epoch;
        long deltaMilli = dateService.getEpochMilli() - epochMilli;
        Duration deltaDuration = Duration.between(date, dateService.getInstant());

        // Verify delta time is the range 2..10 seconds
        Assert.assertTrue( delta >= 2 );
        Assert.assertTrue( delta < 10 );

        Assert.assertTrue( deltaMilli >= 2 * 1000 );
        Assert.assertTrue( deltaMilli < 10 * 1000 );

        Assert.assertTrue( deltaDuration.compareTo(Duration.ofSeconds(2)) >= 0);
        Assert.assertTrue( deltaDuration.compareTo(Duration.ofSeconds(10)) < 0);
    }

    @Test
    public void testNormalOperation() throws InterruptedException {

        // Create the date dervice
        SystemDateService dateService = new SystemDateServiceImpl();

        // Check normal operation
        checkNormalOperation(dateService);
    }


    @Test
    public void testForcedOperationInstant() throws InterruptedException {

        // Create the date dervice
        SystemDateService dateService = new SystemDateServiceImpl();

        // Force seconds
        final long FORCED_EPOCH_MILLI = 1_456_000_000_123L;
        final Instant FORCE_DATE = Instant.ofEpochMilli(FORCED_EPOCH_MILLI);
        dateService.forceInstant(FORCE_DATE);

        // Check forced
        Assert.assertEquals( FORCED_EPOCH_MILLI / 1000, dateService.getEpoch() );
        Assert.assertEquals( FORCED_EPOCH_MILLI , dateService.getEpochMilli() );
        Assert.assertEquals( FORCE_DATE , dateService.getInstant() );

        // Restore normal operation
        dateService.forceEpoch(null);

        // Check normal operation
        checkNormalOperation(dateService);

    }


    @Test
    public void testForcedOperationSeconds() throws InterruptedException {

        // Create the date dervice
        SystemDateService dateService = new SystemDateServiceImpl();

        // Force seconds
        final long FORCED_EPOCH = 1_400_000_000;
        dateService.forceEpoch(FORCED_EPOCH);

        // Check forced
        Assert.assertEquals( FORCED_EPOCH, dateService.getEpoch() );
        Assert.assertEquals( FORCED_EPOCH * 1000, dateService.getEpochMilli() );
        Assert.assertEquals( FORCED_EPOCH * 1000, dateService.getInstant().toEpochMilli() );

        // Restore normal operation
        dateService.forceEpoch(null);

        // Check normal operation
        checkNormalOperation(dateService);

    }

    @Test
    public void testForcedOperationMilliSeconds() throws InterruptedException {

        // Create the date dervice
        SystemDateService dateService = new SystemDateServiceImpl();

        // Force seconds
        final long FORCED_EPOCH_MILLI = 1_456_000_000_123L;
        dateService.forceEpochMilli(FORCED_EPOCH_MILLI);

        // Check forced
        Assert.assertEquals( FORCED_EPOCH_MILLI / 1000, dateService.getEpoch() );
        Assert.assertEquals( FORCED_EPOCH_MILLI , dateService.getEpochMilli() );

        // Restore normal operation
        dateService.forceEpoch(null);

        // Check normal operation
        checkNormalOperation(dateService);

    }

    @Test
    public void testForceDurationAdvance() {

        // Create the date dervice
        SystemDateService dateService = new SystemDateServiceImpl();

        // Force seconds
        final long FORCED_EPOCH_MILLI = 1_456_000_000_123L;
        dateService.forceEpochMilli(FORCED_EPOCH_MILLI);

        // Advance
        final long DELTA = 12_345L;
        dateService.forceDurationAdvance( Duration.ZERO.ofMillis(DELTA));
        Assert.assertEquals( FORCED_EPOCH_MILLI + DELTA, dateService.getEpochMilli() );

    }

    @Test
    public void testForceAdvanceMillis() {

        // Create the date dervice
        SystemDateService dateService = new SystemDateServiceImpl();

        // Force seconds
        final long FORCED_EPOCH_MILLI = 1_456_000_000_123L;
        dateService.forceEpochMilli(FORCED_EPOCH_MILLI);

        // Advance
        final long DELTA = 12_345L;
        dateService.forceAdvanceMilli(DELTA);
        Assert.assertEquals( FORCED_EPOCH_MILLI + DELTA, dateService.getEpochMilli() );

    }

}
