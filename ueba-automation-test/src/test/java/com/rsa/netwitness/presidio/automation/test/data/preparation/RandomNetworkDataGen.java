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
import org.testng.annotations.Test;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.SingleTimeGeneratorFactory;
import presidio.data.generators.event.tls.TlsRangeEventsGen;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, NetwitnessEventStoreConfig.class})
public class RandomNetworkDataGen extends AbstractTestNGSpringContextTests {
    private static  Logger LOGGER = (Logger) LoggerFactory.getLogger(RandomNetworkDataGen.class);

    @Autowired
    private NetwitnessEventStore netwitnessEventStore;

    protected int MAX_BUCKET_SIZE = 300;
    protected int TOTAL = 5000000;
    protected int startHourOfDay = 5;
    protected int endHourOfDay = 10;
    protected int daysBackTo = -1;   // -1 is current day
    protected int intervalMinutes = 1;


    private GeneratorFormat generatorFormat;
    protected int historicalDaysBack;
    protected int anomalyDay;
    protected Map<Schema, Long> generatorResultCount;


    @Parameters({"historical_days_back", "anomaly_day", "generator_format"})
    @BeforeClass
    public void setup(@Optional("0") int historicalDaysBack,
                      @Optional("5000000") int numOfEvents,
                      @Optional("CEF_DAILY_FILE") GeneratorFormat generatorFormat) throws GeneratorException, InterruptedException {


        numOfEvents = TOTAL;

        setParams(historicalDaysBack, anomalyDay, generatorFormat);
        TlsRangeEventsGen gen = getGen();
        TOTAL = numOfEvents;
        int eventsCount = 0;

        while (eventsCount < TOTAL) {
            LOGGER.info("  ++++++ Going to generate.");

            int BUCKET_SIZE = (TOTAL - eventsCount) < MAX_BUCKET_SIZE ? TOTAL - eventsCount : MAX_BUCKET_SIZE;
            List<TlsEvent> precidioEvents = new LinkedList<>();

            while (precidioEvents.size() < BUCKET_SIZE) {
                precidioEvents.addAll(gen.generate(BUCKET_SIZE));
                eventsCount += precidioEvents.size();
                LOGGER.info("  ++++++ eventsCount: " + eventsCount);

                if (precidioEvents.size() < BUCKET_SIZE) {
                    gen.setTimeGenerator(getCommonValuesTimeGen());
                }
            }


            Stream<NetwitnessEvent> converted = precidioEvents.parallelStream().map(getConverter()::convert);
            LOGGER.info("  ++++++ Going to send.");
            generatorResultCount = getProducer().send(converted);
        }
    }

    @Test
    void test(){
        LOGGER.info("  ++++++ Done");
    }

    private TlsRangeEventsGen getGen(){
        int DEFAULT_RANGE = 500;

        TlsRangeEventsGen tlsEventsGen = new TlsRangeEventsGen(1);
        tlsEventsGen.hostnameGen.nextRangeGenCyclic(DEFAULT_RANGE);
        tlsEventsGen.dstPortGen.nextRangeGenCyclic(DEFAULT_RANGE);
        tlsEventsGen.ja3Gen.nextRangeGenCyclic(DEFAULT_RANGE);
        tlsEventsGen.sslSubjectGen.nextRangeGenCyclic(DEFAULT_RANGE);
        tlsEventsGen.dstOrgGen.nextRangeGenCyclic(DEFAULT_RANGE);
        tlsEventsGen.srcNetnameGen.nextRangeGenCyclic(DEFAULT_RANGE);
        tlsEventsGen.locationGen.nextRangeGenCyclic(100);
        tlsEventsGen.srcIpGenerator.nextRangeGenCyclic(DEFAULT_RANGE);

        tlsEventsGen.setTimeGenerator(getCommonValuesTimeGen());

        return  tlsEventsGen;
    }



    protected ITimeGenerator getCommonValuesTimeGen() {
        return getTimeGen(startHourOfDay, endHourOfDay, historicalDaysBack, daysBackTo, intervalMinutes);
    }


    protected ITimeGenerator getTimeGen(int startHourOfDay, int endHourOfDay, int daysBackFrom, int daysBackTo, int intervalMinutes) {
        try {
            return new SingleTimeGeneratorFactory(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, intervalMinutes).createTimeGenerator();
        } catch (GeneratorException e) {
            e.printStackTrace();
        }
        return null;
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
