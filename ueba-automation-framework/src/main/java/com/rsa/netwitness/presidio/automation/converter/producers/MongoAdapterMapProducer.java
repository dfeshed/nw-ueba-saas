package com.rsa.netwitness.presidio.automation.converter.producers;

import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import fortscale.common.general.Schema;
import org.assertj.core.util.Maps;
import presidio.nw.flume.domain.test.NetwitnessStoredData;

import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class MongoAdapterMapProducer implements EventsProducer<List<Map<String, Object>>> {

    private NetwitnessEventStore netwitnessEventStore;
    private final Schema schema;

    public MongoAdapterMapProducer(NetwitnessEventStore netwitnessEventStore, Schema schema){
        this.netwitnessEventStore = requireNonNull(netwitnessEventStore);
        this.schema = requireNonNull(schema);
    }

    @Override
    public Map<Schema, Long> send(List<Map<String, Object>> eventsList) {

        List<NetwitnessStoredData> converted = eventsList.parallelStream()
                .map(NetwitnessStoredData::new)
                .collect(toList());

        netwitnessEventStore.store(converted, schema);
        return Maps.newHashMap(schema, (long) converted.size());
    }
}
