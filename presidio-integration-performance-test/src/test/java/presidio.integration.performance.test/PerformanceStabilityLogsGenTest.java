package presidio.integration.performance.test;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverter;
import com.rsa.netwitness.presidio.automation.converter.conveters.EventConverterFactory;
import com.rsa.netwitness.presidio.automation.converter.events.NetwitnessEvent;
import com.rsa.netwitness.presidio.automation.converter.producers.EventsProducer;
import com.rsa.netwitness.presidio.automation.converter.producers.EventsProducerFactory;
import fortscale.common.general.Schema;
import org.apache.commons.lang.time.StopWatch;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import presidio.data.domain.MachineEntity;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.IEventGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.RandomMultiMachineEntityGenerator;
import presidio.integration.performance.generators.printer.EventLogPrinter;
import presidio.integration.performance.generators.printer.EventLogPrinterFactory;
import presidio.integration.performance.scenario.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import static com.rsa.netwitness.presidio.automation.enums.GeneratorFormat.CEF_HOURLY_FILE;
import static java.util.stream.Collectors.toList;

public class PerformanceStabilityLogsGenTest extends AbstractTestNGSpringContextTests {
    private final int EVENTS_GENERATION_CHUNK = 100000;
    private static final int NUM_OF_NORMAL_USERS = 94500;
    private static final int NUM_OF_ADMIN_USERS = 5000;
    private static final int NUM_OF_SERVICE_ACCOUNT_USERS = 500;

    private final int NUM_OF_GLOBAL_SERVER_MACHINES = 100;
    private final int NUM_OF_GLOBAL_SERVER_MACHINES_CLUSTERS = 50;
    private final String GLOBAL_SERVER_MACHINES_CLUSTER_PREFIX = "global_srv_";
    private final int NUM_OF_GLOBAL_SERVER_MACHINES_PER_CLUSTER = 5;
    private final int NUM_OF_ADMIN_SERVER_MACHINES = 200;
    private final int NUM_OF_ADMIN_SERVER_MACHINES_CLUSTERS = 100;
    private final String ADMIN_SERVER_MACHINES_CLUSTER_PREFIX = "admin_srv_";
    private final int NUM_OF_ADMIN_SERVER_MACHINES_PER_CLUSTER = 5;
    private final int NUM_OF_LOCAL_SERVER_MACHINES = 4000;
    private final int NUM_OF_LOCAL_SERVER_MACHINES_CLUSTERS = 1000;
    private final String LOCAL_SERVER_MACHINES_CLUSTER_PREFIX = "local_srv_";
    private final int NUM_OF_LOCAL_SERVER_MACHINES_PER_CLUSTER = 5;

    private int totalTls = 0;
    private StopWatch stopWatch = new StopWatch();
    private StopWatch tlsStopWatch = new StopWatch();

    private EventsProducer<List<NetwitnessEvent>> eventsProducer = new EventsProducerFactory(null).get(CEF_HOURLY_FILE);
    public final EventConverter<Event> eventEventConverter = new EventConverterFactory().get();


    @Parameters({"start_time", "end_time", "probability_multiplier", "users_multiplier",
            "tls_alerts_probability", "tls_groups_to_create", "tls_events_per_day_per_group","schemas"})
    @Test
    public void performance(@Optional("2019-10-30T00:00:00.00Z") String startTimeStr, @Optional("2019-10-31T23:59:00.00Z") String endTimeStr,
                            @Optional("0.005") double probabilityMultiplier, @Optional("0.005") double usersMultiplier,
                            @Optional("0.001") double tlsAlertsProbability, @Optional("1") int groupsToCreate, @Optional("1000") double tlsEventsPerDayPerGroup,
                            @Optional("TLS") String schemas ) throws GeneratorException {

        System.out.println("=================== TEST PARAMETERS =============== ");
        System.out.println("start_time: " + startTimeStr);
        System.out.println("end_time: " + endTimeStr);
        System.out.println("probability_multiplier: " + probabilityMultiplier);
        System.out.println("users_multiplier: " + usersMultiplier);
        System.out.println("tls_alerts_probability: " + tlsAlertsProbability);
        System.out.println("tls_groups_to_create: " + groupsToCreate);
        System.out.println("tls_events_per_day_per_group: " + tlsEventsPerDayPerGroup);
        System.out.println("schemas: " + schemas);
        System.out.println("=================================================== ");

        Instant startInstant = Instant.parse(startTimeStr);
        Instant endInstant = Instant.parse(endTimeStr);

        int numOfNormalUsers = (int) (NUM_OF_NORMAL_USERS * usersMultiplier);
        int numOfAdminUsers = (int) (NUM_OF_ADMIN_USERS * usersMultiplier);
        int numOfserviceAccountUsers = (int) (NUM_OF_SERVICE_ACCOUNT_USERS * usersMultiplier);

        stopWatch.start();

        if (schemas.contains("TLS")) {
            TlsPerformanceStabilityScenario scenario = new TlsPerformanceStabilityScenario(startInstant, endInstant, tlsAlertsProbability, groupsToCreate, tlsEventsPerDayPerGroup);

            Stream<TlsEvent> tlsEventStream = scenario.tlsEventsGenerators.stream()
                    .map(IEventGenerator::generateToStream)
                    .flatMap(e -> e);

            UnmodifiableIterator<List<TlsEvent>> partition = Iterators.partition(tlsEventStream.iterator(), EVENTS_GENERATION_CHUNK);

            tlsStopWatch.start();
            while (partition.hasNext()) {
                List<TlsEvent> tlsEvents = partition.next();
                process(tlsEvents);
            }
            System.out.println("TOTAL TLS: " + totalTls + ". Generation time: " + stopWatch.toString());
            tlsStopWatch.stop();
        }


        if (schemas.contains("PROCESS")) {
            ProcessPerformanceStabilityScenario scenario = new ProcessPerformanceStabilityScenario(startInstant, endInstant,
                    numOfNormalUsers, numOfAdminUsers, numOfserviceAccountUsers, probabilityMultiplier);
            printDaysOfProcessEvents(scenario);
        }

        if (schemas.contains("REGISTRY")) {
            RegistryPerformanceStabilityScenario registryScenario =
                    new RegistryPerformanceStabilityScenario(
                            startInstant, endInstant,
                            numOfNormalUsers, numOfAdminUsers, numOfserviceAccountUsers,
                            probabilityMultiplier);
            printDaysOfRegistryEvents(registryScenario);
        }

        if (schemas.contains("AUTHENTICATION")) {
            AuthenticationPerformanceStabilityScenario authscenario =
                    new AuthenticationPerformanceStabilityScenario(
                            startInstant, endInstant, numOfNormalUsers, numOfAdminUsers, numOfserviceAccountUsers, probabilityMultiplier,
                            createGlobalServerMachinePool(),
                            createLocalServerMachinePool(),
                            createAdminServerMachinePool());
            printDaysOfAuthEvents(authscenario);
        }

        if (schemas.contains("ACTIVE_DIRECTORY")) {
            ActiveDirectoryPerformanceStabilityScenario activDirectoryScenarios = new ActiveDirectoryPerformanceStabilityScenario(startInstant, endInstant,
                    numOfNormalUsers, numOfAdminUsers, numOfserviceAccountUsers, probabilityMultiplier);
            printDaysOfADEvents(activDirectoryScenarios);
        }

        if (schemas.contains("FILE")) {
            FilePerformanceStabilityScenario fileScenarios = new FilePerformanceStabilityScenario(
                    startInstant, endInstant, numOfNormalUsers, numOfAdminUsers, numOfserviceAccountUsers, probabilityMultiplier,
                    createGlobalServerMachinePool(),
                    createLocalServerMachinePool(),
                    createAdminServerMachinePool());
            printDaysOfFileEvents(fileScenarios);
        }

        stopWatch.split();
        System.out.println(stopWatch.toSplitString());
    }


    private void process(List<TlsEvent> bucket){
        List<NetwitnessEvent> convertedEvents = bucket.parallelStream()
                .map(eventEventConverter::convert).collect(toList());

        Map<Schema, Long> sent = eventsProducer.send(convertedEvents);
        totalTls += sent.get(Schema.TLS).intValue();
        tlsStopWatch.split();
        if (Instant.ofEpochMilli(tlsStopWatch.getSplitTime()).minusSeconds(30).toEpochMilli() > 0) {
            System.out.println("TLS EVENTS COUNT -> " + totalTls + ". Took " + tlsStopWatch.getSplitTime() + " msec.");
            tlsStopWatch.reset();
            tlsStopWatch.start();
        }
    }


    private void printDaysOfProcessEvents(ProcessPerformanceStabilityScenario scenario) throws GeneratorException {
        System.out.println("$$$$ Starts Generating Process Events $$$$");
        /** Generate and send events **/
        EventLogPrinter logPrinter = new EventLogPrinterFactory().getPrinter(Schema.PROCESS);
        List<Event> events;
        do {
            events = scenario.generateEvents(EVENTS_GENERATION_CHUNK);
            logPrinter.printHourlyFiles(events);
        } while (events.size() == EVENTS_GENERATION_CHUNK);
    }

    private void printDaysOfRegistryEvents(RegistryPerformanceStabilityScenario scenario) throws GeneratorException {
        /** Generate and send events **/
        System.out.println("$$$$ Starts Generating Registry Events $$$$");

        EventLogPrinter logPrinter = new EventLogPrinterFactory().getPrinter(Schema.REGISTRY);
        List<Event> events;
        do {
            events = scenario.generateEvents(EVENTS_GENERATION_CHUNK);
            //logPrinter.printHourlyFiles(events);
        } while (events.size() == EVENTS_GENERATION_CHUNK);
    }

    private void printDaysOfADEvents(ActiveDirectoryPerformanceStabilityScenario scenario) throws GeneratorException {
        /** Generate and send events **/
        System.out.println("$$$$ Starts Generating Active Directory Events $$$$");

        EventLogPrinter logPrinter = new EventLogPrinterFactory().getPrinter(Schema.ACTIVE_DIRECTORY);
        List<Event> events;
        do {
            events = scenario.generateEvents(EVENTS_GENERATION_CHUNK);
            logPrinter.printHourlyFiles(events);
        } while (events.size() == EVENTS_GENERATION_CHUNK);
    }

    private void printDaysOfFileEvents(FilePerformanceStabilityScenario scenario) throws GeneratorException {
        /** Generate and send events **/
        System.out.println("$$$$ Starts Generating File Events $$$$");

        EventLogPrinter logPrinter = new EventLogPrinterFactory().getPrinter(Schema.FILE);
        List<Event> events;
        do {
            events = scenario.generateEvents(EVENTS_GENERATION_CHUNK);
            Map<String, List<Event>> srcMachineToEvents = new HashMap<>();
            Map<String, List<Event>> srcProcessToEvents = new HashMap<>();
            Map<String, List<Event>> dstProcessToEvents = new HashMap<>();

            for (Event event : events) {
                List<Event> srcMachineEvents = srcMachineToEvents.computeIfAbsent(((FileEvent)event).getMachineEntity().getMachineId(), k -> new ArrayList<>());
                srcMachineEvents.add(event);
                List<Event> srcProcessEvents = srcProcessToEvents.computeIfAbsent(((FileEvent)event).getFileOperation().getSourceFile().getFileName(), k -> new ArrayList<>());
                srcProcessEvents.add(event);
                List<Event> dstProcessEvents = dstProcessToEvents.computeIfAbsent(((FileEvent)event).getFileOperation().getDestinationFile().getFileName(), k -> new ArrayList<>());
                dstProcessEvents.add(event);
            }
            logPrinter.printHourlyFiles(events);
        } while (events.size() == EVENTS_GENERATION_CHUNK);
    }

    private void printDaysOfAuthEvents(AuthenticationPerformanceStabilityScenario scenario) throws GeneratorException {
        /** Generate and send events **/
        System.out.println("$$$$ Starts Generating Authentication Events $$$$");
        EventLogPrinter logPrinter = new EventLogPrinterFactory().getPrinter(Schema.AUTHENTICATION);
        List<Event> events;
        do {
            events = scenario.generateEvents(EVENTS_GENERATION_CHUNK);
            Map<String, List<Event>> srcMachineToEvents = new HashMap<>();
            Map<String, List<Event>> dstMachineToEvents = new HashMap<>();
            for (Event event : events) {
                List<Event> srcMachineEvents = srcMachineToEvents.computeIfAbsent(((AuthenticationEvent) event).getSrcMachineEntity().getMachineId(), k -> new ArrayList<>());
                srcMachineEvents.add(event);
                List<Event> dstMachineEvents = dstMachineToEvents.computeIfAbsent(((AuthenticationEvent) event).getDstMachineEntity().getMachineId(), k -> new ArrayList<>());
                dstMachineEvents.add(event);
            }
            logPrinter.printHourlyFiles(events);
        } while (events.size() == EVENTS_GENERATION_CHUNK);
    }

    private List<MachineEntity> createGlobalServerMachinePool() {
        return createNonDesktopMachinePool(
                NUM_OF_GLOBAL_SERVER_MACHINES_CLUSTERS,
                GLOBAL_SERVER_MACHINES_CLUSTER_PREFIX,
                NUM_OF_GLOBAL_SERVER_MACHINES_PER_CLUSTER,
                NUM_OF_GLOBAL_SERVER_MACHINES);
    }

    private List<MachineEntity> createAdminServerMachinePool() {
        return createNonDesktopMachinePool(
                NUM_OF_ADMIN_SERVER_MACHINES_CLUSTERS,
                ADMIN_SERVER_MACHINES_CLUSTER_PREFIX,
                NUM_OF_ADMIN_SERVER_MACHINES_PER_CLUSTER,
                NUM_OF_ADMIN_SERVER_MACHINES);
    }

    private List<MachineEntity> createLocalServerMachinePool() {
        return createNonDesktopMachinePool(
                NUM_OF_LOCAL_SERVER_MACHINES_CLUSTERS,
                LOCAL_SERVER_MACHINES_CLUSTER_PREFIX,
                NUM_OF_LOCAL_SERVER_MACHINES_PER_CLUSTER,
                NUM_OF_LOCAL_SERVER_MACHINES);
    }

    private List<MachineEntity> createNonDesktopMachinePool(int numOfClusters, String clusterPrefix,
                                                            int numOfMachinesPerCluster, int numOfMachines) {
        IMachineGenerator generator =
                new RandomMultiMachineEntityGenerator(
                        Arrays.asList("domain1"),
                        numOfClusters, clusterPrefix,
                        numOfMachinesPerCluster, "");
        Map<String, MachineEntity> nameToMachineEntityMap = new HashMap<>();
        while (nameToMachineEntityMap.size() < numOfMachines) {
            MachineEntity machineEntity = generator.getNext();
            nameToMachineEntityMap.put(machineEntity.getMachineId(), machineEntity);
        }

        return new ArrayList<>(nameToMachineEntityMap.values());
    }

}
