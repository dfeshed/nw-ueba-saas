package com.rsa.netwitness.presidio.automation.test.data.preparation;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverter;
import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverterFactory;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.producers.EventsProducer;
import com.rsa.netwitness.presidio.automation.converter.producers.EventsProducerSupplier;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
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

import java.time.Instant;
import java.util.Map;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, NetwitnessEventStoreConfig.class})
public abstract class DataPreparationBase extends AbstractTestNGSpringContextTests {
    private static  Logger LOGGER = (Logger) LoggerFactory.getLogger(DataPreparationBase.class);

    @Autowired
    private NetwitnessEventStore netwitnessEventStore;

    private GeneratorFormat generatorFormat;
    protected int historicalDaysBack;
    protected int anomalyDay;
    protected Map<Schema, Long> generatorResultCount;

    protected abstract Stream<? extends Event> generate() throws GeneratorException;


    @Parameters({"historical_days_back", "anomaly_day", "generator_format"})
    @BeforeClass
    public void setup(@Optional("15") int historicalDaysBack,
                      @Optional("1") int anomalyDay,
                      @Optional("MONGO_ADAPTER") GeneratorFormat generatorFormat) throws GeneratorException {

        setParams(historicalDaysBack, anomalyDay, generatorFormat);
        LOGGER.info("  ++++++ Going to generate.");
        Stream<? extends Event> presidioEvents = generate()
                .filter(event -> event.getDateTime().isAfter(Instant.now().truncatedTo(DAYS).minus(historicalDaysBack, DAYS)))
                .filter(event -> event.getDateTime().isBefore(Instant.now().truncatedTo(DAYS)));

        LOGGER.info("  ++++++ Going to convert.");
        Stream<NetwitnessEvent> converted = presidioEvents.parallel().map(getConverter()::convert);

        LOGGER.info("  ++++++ Going to send.");
        generatorResultCount = getProducer().send(converted);

        LOGGER.info("   ++++++  Sent events count result:");
        generatorResultCount.forEach(
                (schema, count) -> LOGGER.info(schema.toString().concat(" -> ").concat(String.valueOf(count))));
    }

    private EventsProducer<NetwitnessEvent> getProducer() {
        return new EventsProducerSupplier(netwitnessEventStore).get(generatorFormat);
    }

    private EventConverter<Event> getConverter() {
        return new EventConverterFactory().get();
    }


    private void setParams(int historicalDaysBack, int anomalyDay, GeneratorFormat generatorFormat){
        this.historicalDaysBack = historicalDaysBack;
        this.anomalyDay = anomalyDay;
        this.generatorFormat = generatorFormat;
        LOGGER.info("historicalDaysBack=" + this.historicalDaysBack + " anomalyDay=" + this.anomalyDay);
        LOGGER.info("generatorFormat = " + generatorFormat);
    }
}
