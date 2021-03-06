package com.rsa.netwitness.presidio.datagen.scenarios;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverter;
import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverterFactory;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.producers.EventsProducer;
import com.rsa.netwitness.presidio.automation.converter.producers.EventsProducerSupplier;
import com.rsa.netwitness.presidio.automation.enums.GeneratorFormat;
import fortscale.common.general.Schema;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public abstract class DataPreparationBaseNoSping extends AbstractTestNGSpringContextTests {
    private static  Logger LOGGER = (Logger) LoggerFactory.getLogger(DataPreparationBaseNoSping.class);

    private GeneratorFormat generatorFormat;
    protected int historicalDaysBack;
    protected int anomalyDay;
    protected Map<Schema, Long> generatorResultCount;
    protected String schemas;

    protected abstract List<? extends Event> generate() throws GeneratorException;


    @Parameters({"historical_days_back", "anomaly_day", "generator_format", "schemas"})
    @BeforeClass
    public void setup(@Optional("15") int historicalDaysBack,
                      @Optional("1") int anomalyDay,
                      @Optional("CEF_DAILY_FILE") GeneratorFormat generatorFormat,
                      @Optional("PROCESS,REGISTRY,AUTHENTICATION,ACTIVE_DIRECTORY,FILE,TLS") String schemas)
            throws GeneratorException {

        setParams(historicalDaysBack, anomalyDay, generatorFormat, schemas);
        LOGGER.info("  ++++++ Going to generate.");
        List<? extends Event> precidioEvents = generate();

        LOGGER.info("  ++++++ Going to convert.");
        Stream<NetwitnessEvent> converted = precidioEvents.parallelStream()
                .map(getConverter()::convert);

        LOGGER.info("  ++++++ Going to send.");
        generatorResultCount = getProducer().send(converted);

        LOGGER.info("   ++++++  Sent events count result:");
        generatorResultCount.forEach(
                (schema, count) -> LOGGER.info(schema.toString().concat(" -> ").concat(String.valueOf(count))));

        System.out.println("Done.");
        System.out.println("Generator target directory: ./target");
        System.exit(0);
    }

    private EventsProducer<NetwitnessEvent> getProducer() {
        return new EventsProducerSupplier().get(generatorFormat);
    }

    private EventConverter<Event> getConverter() {
        return new EventConverterFactory().get();
    }


    private void setParams(int historicalDaysBack, int anomalyDay, GeneratorFormat generatorFormat, String schemas){
        this.historicalDaysBack = historicalDaysBack;
        this.anomalyDay = anomalyDay;
        this.generatorFormat = generatorFormat;
        this.schemas = schemas;
        LOGGER.info("historicalDaysBack=" + this.historicalDaysBack + " anomalyDay=" + this.anomalyDay);
        LOGGER.info("generatorFormat = " + generatorFormat);
    }
}
