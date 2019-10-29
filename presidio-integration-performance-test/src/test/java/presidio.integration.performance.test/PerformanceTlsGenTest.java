package presidio.integration.performance.test;

import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverter;
import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverterFactory;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.producers.EventsProducer;
import com.rsa.netwitness.presidio.automation.converter.producers.EventsProducerFactory;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import fortscale.common.general.Schema;
import fortscale.utils.mongodb.config.MongoConfig;
import org.apache.commons.lang.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.performance.tls.clusters.TlsEventsSimplePerfGen;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.rsa.netwitness.presidio.automation.enums.GeneratorFormat.CEF_DAILY_FILE;
import static java.util.stream.Collectors.toList;
import static presidio.integration.performance.generators.tls.ClusterSizeFactory.*;


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, NetwitnessEventStoreConfig.class})
public class PerformanceTlsGenTest extends AbstractTestNGSpringContextTests {

    private static final int MAX_BUCKET_SIZE = 1000;

    @Autowired
    private NetwitnessEventStore netwitnessEventStore;


    private final int LOW_SIZE_CLUSTERS = 10000;
    private final int MEDIUM_SIZE_CLUSTERS = 200;
    private final int LARGE_SIZE_CLUSTERS = 3;


    private StopWatch stopWatch = new StopWatch();

    @Parameters({"start_time", "end_time", "probability_multiplier", "users_multiplier","schemas"})
    @Test
    public void performance(@Optional("2018-04-03T23:58:00.00Z") String startTimeStr, @Optional("2018-04-04T01:30:00.00Z")
            String endTimeStr, @Optional("0.005") double probabilityMultiplier, @Optional("0.005") double usersMultiplier,
                            @Optional("AUTHENTICATION") String schemas ) {

        System.out.println("=================== TEST PARAMETERS =============== ");
        System.out.println("start_time: " + startTimeStr);
        System.out.println("end_time: " + endTimeStr);
        System.out.println("probability_multiplier: " + probabilityMultiplier);
        System.out.println("users_multiplier: " + usersMultiplier);
        System.out.println("=================================================== ");

        stopWatch.start();

        List<TlsEventsSimplePerfGen> tlsGroupSmall = IntStream.range(0, LOW_SIZE_CLUSTERS).boxed().map(index -> new TlsEventsSimplePerfGen(getSmallClusterParams())).collect(toList());
        List<TlsEventsSimplePerfGen> tlsGroupMedium = IntStream.range(0, MEDIUM_SIZE_CLUSTERS).boxed().map(index -> new TlsEventsSimplePerfGen(getMediumClusterParams())).collect(toList());
        List<TlsEventsSimplePerfGen> tlsGroupLarge = IntStream.range(0, LARGE_SIZE_CLUSTERS).boxed().map(index -> new TlsEventsSimplePerfGen(getLargeClusterParams())).collect(toList());

        List<TlsEventsSimplePerfGen> eventGens = Stream.of(
                tlsGroupSmall.stream(),
                tlsGroupMedium.stream(),
                tlsGroupLarge.stream())
                .flatMap(a -> a).collect(Collectors.toList());

        EventConverter<Event> eventEventConverter = new EventConverterFactory().get();

        for (TlsEventsSimplePerfGen gen : eventGens){
            while (true) {
                Stream<TlsEvent> tlsEvents = generateBucket.apply(gen);
                List<NetwitnessEvent> convertedEvents = tlsEvents.parallel().map(eventEventConverter::convert).collect(toList());
                if (convertedEvents.isEmpty()) break;
                process(convertedEvents);
            }
        }

    }


    private void process(List<NetwitnessEvent> chunk){
        EventsProducer<List<NetwitnessEvent>> eventsProducer = new EventsProducerFactory(netwitnessEventStore).get(CEF_DAILY_FILE);
        Map<Schema, Long> sent = eventsProducer.send(chunk);

        System.out.println(chunk.size());
        sent.forEach((key, value) -> System.out.println(key + " -> " + value));
    }

    private Function<TlsEventsSimplePerfGen, Stream<TlsEvent>> generateBucket = gen -> {
        try {
            return gen.generate(MAX_BUCKET_SIZE).stream();
        } catch (GeneratorException e) {
            e.printStackTrace();
        }

        return Stream.empty();
    };


}
