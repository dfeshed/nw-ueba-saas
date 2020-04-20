package com.rsa.netwitness.presidio.automation.converter.producers;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import fortscale.common.general.Schema;
import org.slf4j.LoggerFactory;
import presidio.nw.flume.domain.test.NetwitnessStoredData;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

class MongoAdapterNetwitnessEventProducer implements EventsProducer<NetwitnessEvent> {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(MongoAdapterNetwitnessEventProducer.class);

    private NetwitnessEventStore netwitnessEventStore;
    private EventFormatter<NetwitnessEvent, Map<String, Object>> formatter;

    MongoAdapterNetwitnessEventProducer(EventFormatter<NetwitnessEvent, Map<String, Object>> formatter, NetwitnessEventStore netwitnessEventStore){
        this.netwitnessEventStore = Objects.requireNonNull(netwitnessEventStore);
        this.formatter = Objects.requireNonNull(formatter);
    }

    @Override
    public Map<Schema, Long> send(Stream<NetwitnessEvent> eventsList ) {
        Map<Schema, Long> totalResult = new HashMap<>();

        List<NetwitnessEvent> events = eventsList.parallel().collect(toList());

        LOGGER.debug("Going to group bucket by schema and add file path");
        Map<Schema, List<NetwitnessEvent>> eventsPerSchema = events.parallelStream()
                .collect(groupingBy(e -> e.schema));
        LOGGER.debug("Done grouping bucket by schema");
        Set<Schema> schemas = eventsPerSchema.keySet();

        Function<Schema, List<NetwitnessStoredData>> getAsNetwitnessStoredData = schema ->
                eventsPerSchema.get(schema).stream()
                        .map(event -> formatter.format(event))
                        .map(NetwitnessStoredData::new)
                        .collect(toList());

        schemas.forEach(schema -> netwitnessEventStore.store(getAsNetwitnessStoredData.apply(schema), schema));

        Map<Schema, Long> update = events.parallelStream().collect(groupingBy(ev -> ev.schema, counting()));
        update.forEach((key, value) -> totalResult.compute(key, (k, v) -> totalResult.getOrDefault(key, 0L) + value));

        totalResult.forEach((k, v) -> System.out.println(k + ": " + v));
        LOGGER.info("Finished upload " + events.size() + " events.");

        return totalResult;
    }
}
