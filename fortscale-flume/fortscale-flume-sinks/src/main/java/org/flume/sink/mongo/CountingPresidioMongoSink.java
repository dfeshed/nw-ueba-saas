package org.flume.sink.mongo;

import fortscale.domain.core.AbstractDocument;
import fortscale.utils.logging.Logger;
import org.flume.utils.CountersUtil;
import org.flume.utils.DateUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CountingPresidioMongoSink<T extends AbstractDocument> extends PresidioMongoSink<T> {

    private static final Logger logger = Logger.getLogger(CountingPresidioMongoSink.class);

    protected CountersUtil countersUtil = new CountersUtil();

    @Override
    public synchronized String getName() {
        return "counting-" + super.getName();
    }

    @Override
    protected int saveEvents(List<T> eventsToSave) throws Exception {
        /* dividing to groups by time and schema */
        List<List<T>> hourLists = eventsToSave.stream()
                .collect(Collectors.groupingBy(x -> DateUtils.ceiling(getEventTimeForCounter(x), ChronoUnit.HOURS)))
                .entrySet().stream()
                .map(e -> {
                    List<T> hourList = new ArrayList<>();
                    hourList.addAll(e.getValue());
                    return hourList;
                })
                .collect(Collectors.toList());


        /* saving each group in it's relevant file with the relevant */
        for (List<T> list : hourLists) {
            final T exampleEvent = list.get(0);
            final Instant timeDetected = getEventTimeForCounter(exampleEvent);
            final String schemaName = getEventSchemaName(exampleEvent);
            final int numOfSavedEvents = super.saveEvents(list);
            try {
                countersUtil.addToSinkCounter(timeDetected, schemaName, numOfSavedEvents);
            } catch (IOException e1) {
                final Instant hourEndTime = DateUtils.ceiling(timeDetected, ChronoUnit.HOURS);
                logger.warn("Failed to update sink counters for schema {} and time {}. Trying again.", schemaName, hourEndTime, e1);
                try {
                    countersUtil.addToSinkCounter(hourEndTime, schemaName, numOfSavedEvents);
                } catch (IOException e2) {
                    logger.error("Failed to update sink counters (2nd try) for schema {} and time {}. This means that the adapter will wait the full time (timeout) until it starts processing this hour.", schemaName, hourEndTime, e2);
                    handleCounterUpdateFailed(e1, e2);
                }

            }
        }


        return 0;
    }

    /**
     * how to handle failed counter update failure (twice). Can throw exception (unless changed, this will trigger a transaction rollback)
     *
     * @param firstException  the exception we got the after the 1st try
     * @param secondException the exception we got the after the 2nd try
     */
    protected void handleCounterUpdateFailed(IOException firstException, IOException secondException) throws Exception {
        //do nothing
    }

    protected abstract String getEventSchemaName(T exampleEvent);

    protected abstract Instant getEventTimeForCounter(T x);


    /**
     * for tests
     */
    public void setCountersUtilForTests(CountersUtil countersUtil) {
        this.countersUtil = countersUtil;
    }
}
