package com.rsa.netwitness.presidio.automation.converter;

import com.rsa.netwitness.presidio.automation.converter.conveters.PresidioEventConverter;
import com.rsa.netwitness.presidio.automation.converter.conveters.PresidioMongoRawEventConverterImpl;
import com.rsa.netwitness.presidio.automation.converter.conveters.PresidioToNetwitnessEventConverterImpl;
import com.rsa.netwitness.presidio.automation.converter.producers.*;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;

import java.util.HashMap;
import java.util.Map;

import static com.rsa.netwitness.presidio.automation.converter.TestContextSupplier.GENERATOR_FORMAT.*;

public class TestContextSupplier {

    public enum GENERATOR_FORMAT {
        CEF_DAILY, CEF_HOURLY, MONGO_ADAPTER, CEF_DAILY_NOT_SEND
    }

    private Map<GENERATOR_FORMAT, PresidioEventConverter> converters = new HashMap<>();
    private Map<GENERATOR_FORMAT, NetwitnessEventsProducer> producers = new HashMap<>();

    public TestContextSupplier(NetwitnessEventStore netwitnessEventStore) {

        converters.putIfAbsent(CEF_DAILY_NOT_SEND, new PresidioToNetwitnessEventConverterImpl());
        converters.putIfAbsent(CEF_DAILY, new PresidioToNetwitnessEventConverterImpl());
        converters.putIfAbsent(CEF_HOURLY, new PresidioToNetwitnessEventConverterImpl());
        converters.putIfAbsent(MONGO_ADAPTER, new PresidioMongoRawEventConverterImpl());

        producers.putIfAbsent(CEF_DAILY_NOT_SEND,  new DailyCefFilesNotSendPrinterImpl());
        producers.putIfAbsent(CEF_DAILY,  new DailyCefFilesPrinterImpl());
        producers.putIfAbsent(CEF_HOURLY,  new HourlyCefFilesPrinterImpl());
        producers.putIfAbsent(MONGO_ADAPTER,  new MongoInputProducerImpl(netwitnessEventStore));

    }


    public PresidioEventConverter getConverter(GENERATOR_FORMAT generatorFormat) {
        if (converters.containsKey(generatorFormat))
            return converters.get(generatorFormat);
        else throw new RuntimeException("Missing converter for: " + generatorFormat);
    }

    public NetwitnessEventsProducer getDispatcher(GENERATOR_FORMAT generatorFormat) {
        if (producers.containsKey(generatorFormat))
            return producers.get(generatorFormat);
        else throw new RuntimeException("Missing generator for: " + generatorFormat);
    }

}
