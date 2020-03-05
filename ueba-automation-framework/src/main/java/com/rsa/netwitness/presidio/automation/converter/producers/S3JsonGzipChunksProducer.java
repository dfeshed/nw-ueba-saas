package com.rsa.netwitness.presidio.automation.converter.producers;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.config.AWS_Config;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import com.rsa.netwitness.presidio.automation.s3.S3_Helper;
import com.rsa.netwitness.presidio.automation.s3.S3_Interval;
import fortscale.common.general.Schema;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class S3JsonGzipChunksProducer implements EventsProducer<NetwitnessEvent> {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(S3JsonGzipChunksProducer.class);

    private boolean IS_PARALLEL = false;

    private final EventFormatter<NetwitnessEvent, String> formatter;
    private AtomicInteger totalUploaded = new AtomicInteger(0);
    private S3_Interval previousIntervalObj;
    private S3_Helper s3_helper = new S3_Helper();
    private Schema schema;

    S3JsonGzipChunksProducer(EventFormatter<NetwitnessEvent, String> formatter) {
        requireNonNull(formatter);
        this.formatter = formatter;
    }

    @Override
    public Map<Schema, Long> send(Stream<NetwitnessEvent> eventsList) {
        List<NetwitnessEvent> events = eventsList.parallel().collect(toList());
        setSchema(events);

        Map<Instant, List<NetwitnessEvent>> eventsByInterval = events.parallelStream()
                .collect(groupingBy(event -> S3_Helper.toChunkInterval.apply(event.eventTimeEpoch)));

        testEventTimeLessThenInterval(eventsByInterval);

        List<S3_Interval> intervalObjects = createIntervalsMatchingEventsMinMaxTime(events);

        if (previousIntervalObj != null) {
            intervalObjects.add(previousIntervalObj);
        }

        List<S3_Interval> chunksSorted = intervalObjects.parallelStream().sorted().collect(toList());

        processIntervals(eventsByInterval, chunksSorted);

        closeIntervals(intervalObjects, chunksSorted);

        previousIntervalObj = chunksSorted.get(intervalObjects.size() - 1);

        LOGGER.info("[" + schema + "] -- " + "Uploaded till now: " + totalUploaded.get());

        return new HashMap<>();
    }

    @Override
    public void close() {
        previousIntervalObj.close();
        LOGGER.info("[" + schema + "] -- " + "TOTAL EVENTS UPLOADED: " + totalUploaded.addAndGet(previousIntervalObj.getTotalUploaded()));
    }




    private void testEventTimeLessThenInterval(Map<Instant, List<NetwitnessEvent>> eventsByInterval) {
        boolean timeTest = eventsByInterval.entrySet().parallelStream()
                .allMatch(e -> e.getValue().parallelStream().map(ev -> ev.eventTimeEpoch).max(Instant::compareTo).orElseThrow().isBefore(e.getKey()));
        assertThat(timeTest).as("Some file contains even with time not matching it.").isTrue();
    }

    private void setSchema(List<NetwitnessEvent> events) {
        List<Schema> schema = events.parallelStream().map(e -> e.schema).distinct().collect(toList());
        assertThat(schema).as("Multiple schemas not allowed here").hasSize(1);
        this.schema = schema.get(0);
    }

    private List<S3_Interval> createIntervalsMatchingEventsMinMaxTime(List<NetwitnessEvent> events) {
        Instant firstInterval = (previousIntervalObj == null) ? firstChunkFromEvents(events) : previousIntervalObj.getInterval().plus(AWS_Config.UPLOAD_INTERVAL_MINUTES.intValue(), MINUTES);
        Instant lastInterval = S3_Helper.toChunkInterval.apply(events.parallelStream().map(e -> e.eventTimeEpoch).max(Instant::compareTo).orElseThrow());
        List<Instant> intervals = s3_helper.divideToIntervals(firstInterval, lastInterval);

        return intervals.parallelStream()
                .map(interval -> new S3_Interval(interval, schema))
                .collect(toList());
    }

    private Instant firstChunkFromEvents(List<NetwitnessEvent> events) {
        Instant minEventTime = events.parallelStream().map(e -> e.eventTimeEpoch).min(Instant::compareTo).orElseThrow();
        return S3_Helper.toChunkInterval.apply(minEventTime);
    }


    private void closeIntervals(List<S3_Interval> intervals, List<S3_Interval> intervalsSorted) {
        List<S3_Interval> close = intervalsSorted.subList(0, intervals.size() - 1);
        Stream<S3_Interval> intervalsToClose = IS_PARALLEL ? close.parallelStream() : close.stream();
        intervalsToClose.forEach(intervalObj -> {
            intervalObj.close();
            totalUploaded.addAndGet(intervalObj.getTotalUploaded());
        });

        LOGGER.info("[" + schema + "] -- " + close.size() + " intervals upload accomplished.");
    }

    private void processIntervals(Map<Instant, List<NetwitnessEvent>> eventsByInterval, List<S3_Interval> intervalsSorted) {
        LOGGER.info("[" + schema + "] -- " + "Going to process events chunk from "
                + intervalsSorted.get(0).getInterval() + " to " + intervalsSorted.get(intervalsSorted.size() - 1).getInterval());

        Stream<S3_Interval> process = IS_PARALLEL ? intervalsSorted.parallelStream() : intervalsSorted.stream();
        process.forEach(intervalObj -> {
            Instant interval = intervalObj.getInterval();
            if (eventsByInterval.containsKey(interval)) {
                intervalObj.process(toStringLines(eventsByInterval.get(interval)));
                totalUploaded.addAndGet(intervalObj.getTotalUploaded());
            }
        });
    }

    private List<String> toStringLines(List<NetwitnessEvent> netwitnessEvents) {
        return netwitnessEvents.parallelStream().map(formatter::format).collect(toList());
    }

}
