package fortscale.collection.jobs.smart;

import fortscale.aggregation.feature.event.ScoredEventsCounterReader;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@Configurable(preConstruction = true)
public class EntityEventCreationThrottler {
    private static final Logger logger = Logger.getLogger(EntityEventCreationThrottler.class);
    private static final long MILLIS_TO_SLEEP = 60000;

    @Autowired
    private ScoredEventsCounterReader scoredEntityEventsCounterReader;

    private long timeoutInMillis;
    private long initialNumberOfScoredEvents;

    public EntityEventCreationThrottler(long timeToWaitInMilliseconds) throws TimeoutException {
        this.timeoutInMillis = timeToWaitInMilliseconds;
        setInitialCounters();
    }

    private void setInitialCounters() throws TimeoutException {
        initialNumberOfScoredEvents = scoredEntityEventsCounterReader.getTotalNumberOfScoredEvents();
        logger.info(String.format("initialNumberOfScoredEvents = %d", initialNumberOfScoredEvents));
    }

    private boolean isNumberOfNewScoredEnityEventsGTE(long numberOfEvents) {
        long totalNumberOfScoredEvents = scoredEntityEventsCounterReader.getTotalNumberOfScoredEvents();
        logger.info(String.format("Total numbe of scored aggregated events in mongo: %d", totalNumberOfScoredEvents));
        logger.info(String.format("Synchronizer waiting for the number of processed events to reach %d", initialNumberOfScoredEvents + numberOfEvents));
        return (totalNumberOfScoredEvents >= initialNumberOfScoredEvents + numberOfEvents);
    }


    public void throttle(long numberOfEvents) throws TimeoutException {
        long startTimeInMillis = System.currentTimeMillis();

        while( ! isNumberOfNewScoredEnityEventsGTE(numberOfEvents) ) {
            if (timeoutInMillis > 0 && System.currentTimeMillis() - startTimeInMillis > timeoutInMillis) {
                throwTimeoutException();
            }

            logger.info("EntityEventCreationThrottler is going to sleep for awhile...");

            try {
                Thread.sleep(MILLIS_TO_SLEEP);
            } catch (InterruptedException e) {}
        }
        logger.info("EntityEventCreationThrottler woke up");
    }


    private void throwTimeoutException() throws TimeoutException {
        String msg = String.format("Got timeout exception while waiting for the scored entity events to be saved in mongo. Waited %d seconds",
                TimeUnit.MILLISECONDS.toSeconds(timeoutInMillis));
        throw new TimeoutException(msg);
    }
}
