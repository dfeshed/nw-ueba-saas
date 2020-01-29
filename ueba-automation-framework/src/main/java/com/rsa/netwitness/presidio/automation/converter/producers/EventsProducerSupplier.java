package com.rsa.netwitness.presidio.automation.converter.producers;

import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.formatters.BrokerCefFormatter;
import com.rsa.netwitness.presidio.automation.converter.formatters.JsonLineFormatter;
import com.rsa.netwitness.presidio.automation.converter.formatters.MongoKeyValueFormatter;
import com.rsa.netwitness.presidio.automation.converter.formatters.NetwitnessStoredDataFormatter;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import com.rsa.netwitness.presidio.automation.enums.GeneratorFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.rsa.netwitness.presidio.automation.enums.GeneratorFormat.*;

public class EventsProducerSupplier {
    private Map<GeneratorFormat, Supplier<EventsProducer<NetwitnessEvent>>> producers = new HashMap<>();

    public EventsProducerSupplier() {
        this(null);
    }

    public EventsProducerSupplier(NetwitnessEventStore netwitnessEventStore) {
        producers.putIfAbsent(CEF_DAILY_FILE, () -> new DailyCefFileProducer(new BrokerCefFormatter()));
        producers.putIfAbsent(CEF_HOURLY_FILE, () -> new HourlyCefFileProducer(new BrokerCefFormatter()));
        producers.putIfAbsent(CEF_DAILY_BROKER, () -> new DailyBrokerCefProducer(new BrokerCefFormatter()));
        producers.putIfAbsent(CEF_HOURLY_BROKER, () -> new HourlyBrokerCefProducer(new BrokerCefFormatter()));
        producers.putIfAbsent(S3_JSON_GZIP_CHUNKS, () -> new S3JsonGzipChunksProducer(new JsonLineFormatter<>(new NetwitnessStoredDataFormatter())));
        producers.putIfAbsent(S3_JSON_GZIP, () -> new S3JsonGzipProducer(new JsonLineFormatter<>(new NetwitnessStoredDataFormatter())));
        producers.putIfAbsent(MONGO_ADAPTER, () -> new MongoAdapterNetwitnessEventProducer(new MongoKeyValueFormatter(), netwitnessEventStore));
    }


    public EventsProducer<NetwitnessEvent> get(GeneratorFormat generatorFormat) {
        if (producers.containsKey(generatorFormat))
            return producers.get(generatorFormat).get();
        else throw new RuntimeException("Missing generator for: " + generatorFormat);
    }

}
