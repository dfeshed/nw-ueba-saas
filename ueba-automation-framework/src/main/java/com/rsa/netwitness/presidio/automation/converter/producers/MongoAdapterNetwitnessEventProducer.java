package com.rsa.netwitness.presidio.automation.converter.producers;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.EventFormatter;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import fortscale.common.general.Schema;
import presidio.nw.flume.domain.test.NetwitnessStoredData;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

class MongoAdapterNetwitnessEventProducer implements EventsProducer<List<NetwitnessEvent>> {

    private NetwitnessEventStore netwitnessEventStore;
    private EventFormatter<Map<String, Object>> formatter;

    MongoAdapterNetwitnessEventProducer(EventFormatter<Map<String, Object>> formatter, NetwitnessEventStore netwitnessEventStore){
        this.netwitnessEventStore = Objects.requireNonNull(netwitnessEventStore);
        this.formatter = Objects.requireNonNull(formatter);
    }

    @Override
    public Map<Schema, Long> send(List<NetwitnessEvent> eventsList) {

        Map<Schema, List<NetwitnessEvent>> eventsPerSchema = eventsList.stream()
                .collect(groupingBy(e -> e.schema));

        Set<Schema> schemas = eventsPerSchema.keySet();

        Function<Schema, List<NetwitnessStoredData>> getAsNetwitnessStoredData = schema ->
                eventsPerSchema.get(schema).stream()
                        .map(event -> formatter.format(event))
                        .map(NetwitnessStoredData::new)
                        .collect(toList());

        schemas.forEach(schema -> netwitnessEventStore.store(
                getAsNetwitnessStoredData.apply(schema), schema));

        return schemas.parallelStream()
                .collect(toMap(schema -> schema, schema -> Long.valueOf(getAsNetwitnessStoredData.apply(schema).size())));
    }
}
