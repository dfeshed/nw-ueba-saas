package presidio.integration.performance.test;

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
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import presidio.data.domain.event.Event;
import presidio.integration.performance.utils.ChunksGenerator;
import presidio.integration.performance.utils.PerformanceScenario;
import presidio.integration.performance.utils.PerformanceScenariosSupplier;
import presidio.integration.performance.utils.TestProperties;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static presidio.integration.performance.utils.TestProperties.PARALLEL_SCENARIOS_INSERT;

public class PerfLogsNoSpringGenTest extends AbstractTestNGSpringContextTests {
    public static final TestProperties test = new TestProperties();
    public static final int EVENTS_GENERATION_CHUNK = Integer.parseInt(System.getenv().getOrDefault("CHUNK_SIZE", "51000"));
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(PerfLogsNoSpringGenTest.class);

    private void setTestProperties() {

        test.NUM_OF_GLOBAL_SERVER_MACHINES = 100;
        test.NUM_OF_GLOBAL_SERVER_MACHINES_CLUSTERS = 50;
        test.GLOBAL_SERVER_MACHINES_CLUSTER_PREFIX = "global_srv_";
        test.NUM_OF_GLOBAL_SERVER_MACHINES_PER_CLUSTER = 5;
        test.NUM_OF_ADMIN_SERVER_MACHINES = 200;
        test.NUM_OF_ADMIN_SERVER_MACHINES_CLUSTERS = 100;
        test.ADMIN_SERVER_MACHINES_CLUSTER_PREFIX = "admin_srv_";
        test.NUM_OF_ADMIN_SERVER_MACHINES_PER_CLUSTER = 5;
        test.NUM_OF_LOCAL_SERVER_MACHINES = 4000;
        test.NUM_OF_LOCAL_SERVER_MACHINES_CLUSTERS = 1000;
        test.LOCAL_SERVER_MACHINES_CLUSTER_PREFIX = "local_srv_";
        test.NUM_OF_LOCAL_SERVER_MACHINES_PER_CLUSTER = 5;

        test.AUTHENTICATION_NUM_OF_NORMAL_USERS = 20000;
        test.AUTHENTICATION_NUM_OF_ADMIN_USERS = 5000;
        test.AUTHENTICATION_NUM_OF_SERVICE_ACCOUNT_USERS = 1000;
        test.FILE_NUM_OF_NORMAL_USERS = 18000;
        test.FILE_NUM_OF_ADMIN_USERS = 1500;
        test.FILE_NUM_OF_SERVICE_ACCOUNT_USERS = 500;
        test.ACTIVE_DIRECTORY_NUM_OF_NORMAL_USERS = 19000;
        test.ACTIVE_DIRECTORY_NUM_OF_ADMIN_USERS = 1000;
        test.ACTIVE_DIRECTORY_NUM_OF_SERVICE_ACCOUNT_USERS = 100;
        test.PROCESS_NUM_OF_NORMAL_USERS = 20000;
        test.PROCESS_NUM_OF_ADMIN_USERS = 1000;
        test.PROCESS_NUM_OF_SERVICE_ACCOUNT_USERS = 100;
        test.REGISTRY_NUM_OF_NORMAL_USERS = 20000;
        test.REGISTRY_NUM_OF_ADMIN_USERS = 1000;
        test.REGISTRY_NUM_OF_SERVICE_ACCOUNT_USERS = 100;
    }


    @Parameters({"start_time", "end_time", "users_probability_multiplier", "users_multiplier", "tls_alerts_probability",
            "tls_groups_to_create", "tls_events_per_day_per_group", "schemas", "generator_format"})
    @Test
    public void performance(@Optional("2020-01-01T00:00:00.00Z") String startTimeStr,
                            @Optional("2020-01-02T00:00:00.00Z") String endTimeStr,
                            @Optional("1") double usersProbabilityMultiplier,
                            @Optional("1") double usersMultiplier,
                            @Optional("0.001") double tlsAlertsProbability,
                            @Optional("1") int tlsGroupsToCreate,
                            @Optional("1000") double tlsEventsPerDayPerGroup,
                            @Optional("FILE,ACTIVE_DIRECTORY,AUTHENTICATION,REGISTRY,PROCESS") String schemas,
                            @Optional("S3_JSON_GZIP_CHUNKS") GeneratorFormat generatorFormat) {

        setTestProperties();
        test.startInstant = Instant.parse(startTimeStr);
        test.endInstant = Instant.parse(endTimeStr);
        test.usersProbabilityMultiplier = usersProbabilityMultiplier;
        test.usersMultiplier = usersMultiplier;
        test.tlsAlertsProbability = tlsAlertsProbability;
        test.tlsGroupsToCreate = tlsGroupsToCreate;
        test.tlsEventsPerDayPerGroup = tlsEventsPerDayPerGroup;
        test.schemas = schemas;
        test.generatorFormat = generatorFormat;
        test.print();

        List<Schema> schemasToProcess = Arrays.stream(schemas.split(",")).map(String::trim).map(Schema::valueOf).collect(toList());

        List<PerformanceScenario> scenarios = schemasToProcess.stream()
                .map(e -> new PerformanceScenariosSupplier(e, test).get())
                .collect(toList());

        LOGGER.info(" *****  Created scenarios  *****");
        scenarios.forEach(System.out::println);

        Stream<PerformanceScenario> scenariosStream = PARALLEL_SCENARIOS_INSERT ? scenarios.parallelStream() : scenarios.stream().sequential();
        scenariosStream.forEach(scenario ->
        {
            ChunksGenerator gen = new ChunksGenerator(scenario, EVENTS_GENERATION_CHUNK);
            EventsProducer<NetwitnessEvent> producer = getProducer();

            LOGGER.info("[" + scenario.getSchema() + "] -- Going to generate next chunk");
            List<? extends Event> nextChunk = gen.getNextChunk();
            LOGGER.info("[" + scenario.getSchema() + "] -- Generated chunk");

            while (!nextChunk.isEmpty()) {
                LOGGER.info("[" + scenario.getSchema() + "] -- Going to convert and insert chunk");
                Stream<NetwitnessEvent> converted = nextChunk.parallelStream().map(getConverter()::convert);
                producer.send(converted);
                LOGGER.info("[" + scenario.getSchema() + "] -- Chunk insert is done");

                LOGGER.info("[" + scenario.getSchema() + "] -- Going to generate next chunk");
                nextChunk = gen.getNextChunk();
                LOGGER.info("[" + scenario.getSchema() + "] -- Generated chunk");
            }
            producer.close();
        });

        LOGGER.info("Finished.");
    }


    private EventsProducer<NetwitnessEvent> getProducer() {
        return new EventsProducerSupplier().get(test.generatorFormat);
    }

    private EventConverter<Event> getConverter() {
        return new EventConverterFactory().get();
    }


}
