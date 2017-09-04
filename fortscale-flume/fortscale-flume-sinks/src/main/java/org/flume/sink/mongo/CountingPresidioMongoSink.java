package org.flume.sink.mongo;

import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractDocument;
import fortscale.utils.logging.Logger;
import org.flume.sink.mongo.persistency.SinkMongoRepository;
import org.flume.utils.CountersUtil;
import org.flume.utils.DateUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public abstract class CountingPresidioMongoSink<T extends AbstractDocument> extends PresidioMongoSink<T> {

    private static final Logger logger = Logger.getLogger(CountingPresidioMongoSink.class);

    protected CountersUtil countersUtil = new CountersUtil(7L * 24 * 60 * 60 * 1000); //1 week = default


    public CountingPresidioMongoSink() {
        super();
    }

    public CountingPresidioMongoSink(SinkMongoRepository sinkMongoRepository) {
        super(sinkMongoRepository);
    }

    public CountingPresidioMongoSink(CountersUtil countersUtil) {
        this();
        this.countersUtil = countersUtil;
    }

    public CountingPresidioMongoSink(SinkMongoRepository sinkMongoRepository, CountersUtil countersUtil) {
        super(sinkMongoRepository);
        this.countersUtil = countersUtil;
    }

    @Override
    public synchronized String getName() {
        return "counting-" + super.getName();
    }

    @Override
    protected int saveEvents(List<T> eventsToSave) throws Exception {
        int numOfTotalSavedEvents = 0;
        List<List<List<T>>> hourListsPerSchema = new ArrayList<>(); //list of lists-of-events-by-time by schema

        try {
            /* divide the list of events to groups by time */
            List<List<T>> hourLists = groupBy(eventsToSave, Collectors.groupingBy(x -> DateUtils.ceiling(getEventTimeForCounter(x), ChronoUnit.HOURS)));

            /* divide all the group of events(group-per-time) by schema */
            for (List<T> hourList : hourLists) {
                final List<List<T>> currSchemaHourList = groupBy(hourList, Collectors.groupingBy(this::getEventSchema));
                hourListsPerSchema.add(currSchemaHourList);
            }
        } catch (Exception e) {
            final String errorMessage = String.format("Failed to save events. %s", e.getMessage());
            throw new Exception(errorMessage, e);
        }

        /* saving each group in it's relevant file (by schema) with the relevant time property */
        for (List<List<T>> hourListPerSchema : hourListsPerSchema) { //going over lists of hours per schema
            for (List<T> hourList : hourListPerSchema) {//going over lists of events per hour
                final T exampleEvent = hourList.get(0);
                final Instant endOfHourForTimeDetected = DateUtils.ceiling(getEventTimeForCounter(exampleEvent), ChronoUnit.HOURS);
                final Schema schema = getEventSchema(exampleEvent);
                int numOfSavedEvents = super.saveEvents(hourList);
                numOfTotalSavedEvents += numOfSavedEvents;
                try {
                    countersUtil.addToSinkCounter(endOfHourForTimeDetected, schema, numOfSavedEvents);
                } catch (IOException e1) {
                    final Instant hourEndTime = DateUtils.ceiling(endOfHourForTimeDetected, ChronoUnit.HOURS);
                    logger.warn("Failed to update sink counters for schema {} and time {}. Trying again.", schema, hourEndTime, e1);
                    try {
                        countersUtil.addToSinkCounter(hourEndTime, schema, numOfSavedEvents);
                    } catch (IOException e2) {
                        logger.error("Failed to update sink counters (2nd try) for schema {} and time {}. This means that the adapter will wait the full time (timeout) until it starts processing this hour.", schema, hourEndTime, e2);
                        handleCounterUpdateFailed(e1, e2);
                    }

                }
            }
        }


        return numOfTotalSavedEvents;
    }

    /**
     * How to handle failed counter update failure (twice). Can throw exception (unless changed, this will trigger a transaction rollback)
     *
     * @param firstException  the exception we got the after the 1st try
     * @param secondException the exception we got the after the 2nd try
     */
    protected void handleCounterUpdateFailed(IOException firstException, IOException secondException) throws Exception {
        //do nothing
    }

    /**
     * This method returns the {@link fortscale.common.general.Schema} for the {@code event}
     *
     * @param event the {@link T} whose {@link Schema} we return
     * @return the {@link fortscale.common.general.Schema} of {@code event}
     */
    protected abstract Schema getEventSchema(T event);


    /**
     * This method returns the event-time for {@code event}. The time MUST be normalized to UTC time zone
     *
     * @param event the {@link T} whose time we return
     * @return the time of {@code event}
     */
    protected abstract Instant getEventTimeForCounter(T event);


    private List<List<T>> groupBy(List<T> eventsToSave, Collector<T, ?, Map<Object, List<T>>> groupByFunction) {
        return eventsToSave.stream()
                .collect(groupByFunction)
                .entrySet().stream()
                .map(e -> {
                    List<T> list = new ArrayList<>();
                    list.addAll(e.getValue());
                    return list;
                })
                .collect(Collectors.toList());
    }
}
