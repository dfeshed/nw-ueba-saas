package com.rsa.netwitness.presidio.automation.converter.producers;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import com.rsa.netwitness.presidio.automation.s3.S3_Helper;
import com.rsa.netwitness.presidio.automation.s3.S3_Interval;
import fortscale.common.general.Schema;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class S3JsonGzipProducer implements EventsProducer<NetwitnessEvent> {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(S3JsonGzipProducer.class);

    private final EventFormatter<NetwitnessEvent, String> formatter;
    private ConcurrentHashMap<Schema, Long> resultingCount = new ConcurrentHashMap<>();
    private boolean IS_PARALLEL = true;
    private S3_Helper s3_helper = new S3_Helper();

    S3JsonGzipProducer(EventFormatter<NetwitnessEvent, String> formatter) {
        requireNonNull(formatter);
        this.formatter = formatter;
    }

    @Override
    public Map<Schema, Long> send(Stream<NetwitnessEvent> eventsList) {
        Map<Schema, List<NetwitnessEvent>> eventsBySchema = eventsList.parallel().collect(groupingBy(e -> e.schema));
        LOGGER.info("Collected events count:");
        eventsBySchema.forEach((key, value) -> System.out.println(String.join("\n", key + " -> " + value.size())));

        eventsBySchema.forEach((schema, events) ->
        {
            Map<Instant, List<NetwitnessEvent>> eventsByInterval = events.parallelStream()
                    .collect(groupingBy(event -> S3_Helper.toChunkInterval.apply(event.eventTimeEpoch)));
            Stream<S3_Interval> allIntervals = createIntervalsMatchingEventsMinMaxTime(events, schema);
            processAllIntervals(eventsByInterval, allIntervals, schema);
        });

        return resultingCount;
    }




    private Stream<S3_Interval> createIntervalsMatchingEventsMinMaxTime(List<NetwitnessEvent> events, Schema schema) {
        Instant firstInterval = firstChunkFromEvents(events);
        Instant lastInterval = S3_Helper.toChunkInterval.apply(events.parallelStream().map(e -> e.eventTimeEpoch).max(Instant::compareTo).orElseThrow());
        return s3_helper.divideToIntervals(firstInterval, lastInterval).parallelStream()
                .map(interval -> new S3_Interval(interval, schema));
    }

    private Instant firstChunkFromEvents(List<NetwitnessEvent> events) {
        Instant minEventTime = events.parallelStream().map(e -> e.eventTimeEpoch).min(Instant::compareTo).orElseThrow();
        return S3_Helper.toChunkInterval.apply(minEventTime);
    }

    private void processAllIntervals(Map<Instant, List<NetwitnessEvent>> eventsByInterval, Stream<S3_Interval> intervals, Schema schema) {
        Stream<S3_Interval> process = IS_PARALLEL ? intervals.parallel() : intervals.sequential();

        process.forEach(intervalObj -> {
            Instant interval = intervalObj.getInterval();
            if (eventsByInterval.containsKey(interval)) {
                intervalObj.process(toStringLines(eventsByInterval.get(interval)));
                resultingCount.putIfAbsent(schema, 0L);
                resultingCount.computeIfPresent(schema, (s, i) -> i + intervalObj.getTotalUploaded());
            }
            intervalObj.close();
            resultingCount.putIfAbsent(schema, 0L);
            resultingCount.computeIfPresent(schema, (s, i) -> i + intervalObj.getTotalUploaded());
        });

        LOGGER.info("[" + schema + "] -- " + (int) intervals.count() + " intervals upload is completed.");
    }

    private List<String> toStringLines(List<NetwitnessEvent> netwitnessEvents) {
        return netwitnessEvents.parallelStream().map(formatter::format).collect(toList());
    }

}
