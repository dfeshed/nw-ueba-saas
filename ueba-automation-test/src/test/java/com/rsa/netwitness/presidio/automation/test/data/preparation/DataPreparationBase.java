package com.rsa.netwitness.presidio.automation.test.data.preparation;

import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import com.rsa.netwitness.presidio.automation.converter.conveters.PresidioEventConverter;
import com.rsa.netwitness.presidio.automation.converter.events.ConverterEventBase;
import com.rsa.netwitness.presidio.automation.converter.producers.NetwitnessEventsProducer;
import com.rsa.netwitness.presidio.automation.converter.TestContextSupplier;
import com.rsa.netwitness.presidio.automation.enums.GeneratorFormat;
import fortscale.common.general.Schema;
import fortscale.utils.mongodb.config.MongoConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, NetwitnessEventStoreConfig.class})
public abstract class DataPreparationBase extends AbstractTestNGSpringContextTests {
    private static  ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(DataPreparationBase.class.getName());

    @Autowired
    private NetwitnessEventStore netwitnessEventStore;


    protected int historicalDaysBack;
    protected int anomalyDay;
    protected GeneratorFormat generatorFormat;
    protected Map<Schema, Long> generatorResultCount;

    protected abstract List<? extends Event> generate() throws GeneratorException;


    @Parameters({"historical_days_back", "anomaly_day", "generator_format"})
    @BeforeClass
    public void setup(@Optional("30") int historicalDaysBack,
                      @Optional("1") int anomalyDay,
                      @Optional("MONGO_ADAPTER") GeneratorFormat generatorFormat) throws GeneratorException {

        setParams(historicalDaysBack, anomalyDay, generatorFormat);
        LOGGER.info(" #######   Generate and send");
        List<? extends Event> precidioEvents = generate();
        Stream<ConverterEventBase> convertedEvents = convert(precidioEvents.stream().map(e -> (Event) e).collect(Collectors.toList()));
        generatorResultCount = send(convertedEvents);
        LOGGER.info("   ++++++   Generated count: ");
        generatorResultCount.forEach(
                (schema, count) -> LOGGER.info(schema.toString().concat(" -> ").concat(String.valueOf(count))));
    }

    private PresidioEventConverter getConverter() {
        return new TestContextSupplier(netwitnessEventStore).getConverter(generatorFormat);
    }

    private NetwitnessEventsProducer getProducer() {
        return new TestContextSupplier(netwitnessEventStore).getDispatcher(generatorFormat);
    }


    private Stream<ConverterEventBase> convert(List<Event> netwitnessEvents){
        return netwitnessEvents.stream().map(getConverter()::convert);
    }

    private Map<Schema, Long> send(Stream<ConverterEventBase> convertedEvents){
        List<ConverterEventBase> collect = convertedEvents.collect(Collectors.toList());
        return getProducer().send(collect);
    }

    private void setParams(int historicalDaysBack, int anomalyDay, GeneratorFormat generatorFormat){
        this.historicalDaysBack = historicalDaysBack;
        this.anomalyDay = anomalyDay;
        this.generatorFormat = generatorFormat;
        LOGGER.info("historicalDaysBack=" + this.historicalDaysBack + " anomalyDay=" + this.anomalyDay);
        LOGGER.info("generatorFormat = " + generatorFormat);
    }
}
