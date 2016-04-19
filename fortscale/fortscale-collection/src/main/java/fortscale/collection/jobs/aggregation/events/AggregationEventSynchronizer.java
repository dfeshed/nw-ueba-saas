package fortscale.collection.jobs.aggregation.events;

import fortscale.aggregation.feature.event.ScoredAggrEventsCounterReader;
import fortscale.utils.kafka.IKafkaSynchronizer;
import fortscale.utils.kafka.SimpleMetricsReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by amira on 17/04/2016.
 */
public class AggregationEventSynchronizer implements IKafkaSynchronizer {
    private static final long MILLIS_TO_SLEEP = 60000;

    private SimpleMetricsReader entityEventsTaskMetricsReader;

    @Autowired
    private ScoredAggrEventsCounterReader scoredAggrEventsCounterReader;

    private long timeoutInMillis;
    private String metric;
    private long initialNumberOfProcessedEvents;
    private long initialNumberOfScoredAggrEvents;

    public AggregationEventSynchronizer(String jobClassToMonitor, String jobToMonitor, long timeToWaitInMilliseconds) throws TimeoutException {
        this.timeoutInMillis = timeToWaitInMilliseconds;
        metric = String.format("%s-received-message-count", jobToMonitor);
        
        entityEventsTaskMetricsReader = new SimpleMetricsReader("entityEventsTaskMetricsReader", 0,
                jobToMonitor, jobClassToMonitor, Collections.singleton(metric));

        setInitialCounters();
    }

    private void setInitialCounters() throws TimeoutException {
        long startTimeInMillis = System.currentTimeMillis();
        while (entityEventsTaskMetricsReader.getLong(metric) == null) {
            if (timeoutInMillis > 0 && System.currentTimeMillis() - startTimeInMillis > timeoutInMillis) {
                throwTimeoutException();
            }
            try {
                Thread.sleep(MILLIS_TO_SLEEP);
            } catch (InterruptedException e) {}
        }
        initialNumberOfProcessedEvents = entityEventsTaskMetricsReader.getLong(metric);

        initialNumberOfScoredAggrEvents = scoredAggrEventsCounterReader.getTotalNumberOfScoredEvents();

    }

    private boolean isNumberOfProcessedEventsInEntityEventTaskEqualsTo(long numberOfEvents) {
        Long numberOfProcessedEvents = entityEventsTaskMetricsReader.getLong(metric);
        return (numberOfProcessedEvents!=null &&
                numberOfProcessedEvents >= initialNumberOfProcessedEvents + numberOfEvents);
    }

    private boolean isNumberOfScoredAggrEventsEqualsTo(long numberOfEvents) {
        return (scoredAggrEventsCounterReader.getTotalNumberOfScoredEvents() >= initialNumberOfScoredAggrEvents + numberOfEvents);
    }


    @Override
    public boolean synchronize(long numberOfEvents) throws TimeoutException {
        long startTimeInMillis = System.currentTimeMillis();

        while(isNumberOfProcessedEventsInEntityEventTaskEqualsTo(numberOfEvents) ||
              isNumberOfScoredAggrEventsEqualsTo(numberOfEvents) ) {
            if (timeoutInMillis > 0 && System.currentTimeMillis() - startTimeInMillis > timeoutInMillis) {
                throwTimeoutException();
            }

            try {
                Thread.sleep(MILLIS_TO_SLEEP);
            } catch (InterruptedException e) {}
        }

        return true;
    }


    private void throwTimeoutException() throws TimeoutException {
        String msg = String.format("Got timeout exception while waiting for the entity events task to process aggregated events. Waited %d seconds",
                TimeUnit.MILLISECONDS.toSeconds(timeoutInMillis));
        throw new TimeoutException(msg);
    }
}
