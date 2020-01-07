package com.rsa.netwitness.presidio.automation.converter.producers;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import fortscale.common.general.Schema;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.LoggerFactory;
import presidio.nw.flume.domain.test.NetwitnessStoredData;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

class MongoAdapterNetwitnessEventProducer implements EventsProducer<NetwitnessEvent> {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(MongoAdapterNetwitnessEventProducer.class);

    private NetwitnessEventStore netwitnessEventStore;
    private EventFormatter<NetwitnessEvent, Map<String, Object>> formatter;
    private static final int EVENTS_CHUNK = 50000;
    private StopWatch tlsStopWatch = new StopWatch();

    MongoAdapterNetwitnessEventProducer(EventFormatter<NetwitnessEvent, Map<String, Object>> formatter, NetwitnessEventStore netwitnessEventStore){
        this.netwitnessEventStore = Objects.requireNonNull(netwitnessEventStore);
        this.formatter = Objects.requireNonNull(formatter);
    }

    @Override
    public Map<Schema, Long> send(Stream<NetwitnessEvent> eventsList) {

        UnmodifiableIterator<List<NetwitnessEvent>> partition = Iterators.partition(eventsList.iterator(), EVENTS_CHUNK);
        Map<Schema, Long> totalResult = new HashMap<>();
        tlsStopWatch.start();

        while (partition.hasNext()) {
            LOGGER.debug("Going to collect next bucket");
            List<NetwitnessEvent> nextBucket = partition.next();
            LOGGER.debug("Next bucket is collected");

            LOGGER.debug("Going to group bucket by schema and add file path");
            Map<Schema, List<NetwitnessEvent>> eventsPerSchema = eventsList.collect(groupingBy(e -> e.schema));
            LOGGER.debug("Done grouping bucket by schema");
            Set<Schema> schemas = eventsPerSchema.keySet();

            Function<Schema, List<NetwitnessStoredData>> getAsNetwitnessStoredData = schema ->
                    eventsPerSchema.get(schema).stream()
                            .map(event -> formatter.format(event))
                            .map(NetwitnessStoredData::new)
                            .collect(toList());

            LOGGER.debug("Going to insert " + nextBucket.size() + " events");
            schemas.forEach(schema -> netwitnessEventStore.store(
                    getAsNetwitnessStoredData.apply(schema), schema));
            LOGGER.debug("Finished to insert " + nextBucket.size() + " events");

            Map<Schema, Long> update = nextBucket.parallelStream().collect(groupingBy(ev -> ev.schema, counting()));
            update.forEach((key, value) -> totalResult.compute(key, (k, v) -> totalResult.getOrDefault(key, 0L) + value));

            tlsStopWatch.split();
            if (Instant.ofEpochMilli(tlsStopWatch.getSplitTime()).minusSeconds(30).toEpochMilli() > 0) {
                LOGGER.info("  >>>>>>> Intermidiate result <<<<<<<");
                totalResult.forEach((k, v) -> System.out.println(k + ": " + v));
                LOGGER.info("  >>>>>>>>>>>>>>>>  <<<<<<<<<<<<<<<<<");
                tlsStopWatch.reset();
                tlsStopWatch.start();
            }
        }

        return totalResult;
    }
}
