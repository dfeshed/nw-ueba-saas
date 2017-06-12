package fortscale.utils.fixedduration;

import fortscale.utils.time.TimeRange;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by barak_schuster on 6/11/17.
 */
public class FixedDurationStrategyExecutorTest {
    public class TestFixedDurationStrategyExecutor extends FixedDurationStrategyExecutor
    {
        int amountOfCalls;
        TestFixedDurationStrategyExecutor(FixedDurationStrategy strategy) {
            super(strategy);
        }

        @Override
        public void executeSingleTimeRange(TimeRange timeRange, String dataSource) {
            amountOfCalls++;
        }
    }

    @Test
    public void execute() throws Exception {
        TestFixedDurationStrategyExecutor fixedDurationStrategyExecutor = new TestFixedDurationStrategyExecutor(FixedDurationStrategy.HOURLY);
        Instant startTime = Instant.EPOCH;
        Instant endTime = startTime.plus(Duration.ofDays(2));

        TimeRange executionTimeRange = new TimeRange(startTime, endTime);
        fixedDurationStrategyExecutor.execute(executionTimeRange,"" );
        Assert.assertEquals(48,fixedDurationStrategyExecutor.amountOfCalls );
    }

}