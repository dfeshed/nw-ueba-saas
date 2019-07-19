package com.rsa.netwitness.presidio.automation.utils.adapter.log_player.producers;

import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import com.rsa.netwitness.presidio.automation.utils.adapter.log_player.events.ConverterEventBase;
import fortscale.common.general.Schema;
import presidio.nw.flume.domain.test.NetwitnessStoredData;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

public class MongoInputProducerImpl implements NetwitnessEventsProducer {

    public NetwitnessEventStore netwitnessEventStore;

    public MongoInputProducerImpl(NetwitnessEventStore netwitnessEventStore){
        this.netwitnessEventStore = netwitnessEventStore;
    }

    @Override
    public Map<Schema, Long> send(List<ConverterEventBase> eventsList) {

        Map<Schema, List<ConverterEventBase>> eventsPerSchema = eventsList.stream()
                .collect(groupingBy(ConverterEventBase::mongoSchema));

        Set<Schema> schemas = eventsPerSchema.keySet();

        Function<Schema, List<NetwitnessStoredData>> getAsNetwitnessStoredData = schema ->
                eventsPerSchema.get(schema).stream()
                        .map(ConverterEventBase::getAsMongoKeyValue)
                        .map(NetwitnessStoredData::new)
                        .collect(toList());

        schemas.forEach(schema -> netwitnessEventStore.store(
                getAsNetwitnessStoredData.apply(schema), schema));

        return schemas.parallelStream()
                .collect(toMap(schema -> schema, schema -> Long.valueOf(getAsNetwitnessStoredData.apply(schema).size())));
    }
}
