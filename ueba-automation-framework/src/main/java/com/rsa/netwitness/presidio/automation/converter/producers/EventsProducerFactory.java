package com.rsa.netwitness.presidio.automation.converter.producers;

import com.rsa.netwitness.presidio.automation.converter.formatters.BrokerCefFormatter;
import com.rsa.netwitness.presidio.automation.converter.formatters.MongoAdapterFormatter;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import com.rsa.netwitness.presidio.automation.enums.GeneratorFormat;

import java.util.HashMap;
import java.util.Map;

import static com.rsa.netwitness.presidio.automation.enums.GeneratorFormat.*;

public class EventsProducerFactory {

    private Map<GeneratorFormat, EventsProducer> producers = new HashMap<>();

    public EventsProducerFactory(NetwitnessEventStore netwitnessEventStore) {

        producers.putIfAbsent(CEF_DAILY_FILE, new DailyCefFileProducer(new BrokerCefFormatter()));
        producers.putIfAbsent(CEF_DAILY_BROKER, new DailyBrokerCefProducer(new BrokerCefFormatter()));
        producers.putIfAbsent(CEF_HOURLY_BROKER, new HourlyBrokerCefProducer(new BrokerCefFormatter()));
        producers.putIfAbsent(MONGO_ADAPTER, new MongoAdapterNetwitnessEventProducer(new MongoAdapterFormatter(), netwitnessEventStore));

    }


    public EventsProducer get(GeneratorFormat generatorFormat) {
        if (producers.containsKey(generatorFormat))
            return producers.get(generatorFormat);
        else throw new RuntimeException("Missing generator for: " + generatorFormat);
    }

}
