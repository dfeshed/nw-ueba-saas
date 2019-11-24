package presidio.integration.performance.generators;

import fortscale.common.general.Schema;
import org.apache.commons.lang.time.StopWatch;
import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;
import presidio.integration.performance.generators.printer.EventLogPrinter;
import presidio.integration.performance.generators.printer.EventLogPrinterFactory;
import presidio.integration.performance.scenario.ProcessPerformanceStabilityScenario;
import presidio.integration.performance.scenario.RegistryPerformanceStabilityScenario;

import java.time.Instant;
import java.util.List;

public class EndpointLogsGenerator {
    private static final int EVENTS_GENERATION_CHUNK = 10000;
    private static final int NUM_OF_NORMAL_USERS = 94500;
    private static final int NUM_OF_ADMIN_USERS = 5000;
    private static final int NUM_OF_SERVICE_ACCOUNT_USERS = 500;

    public static void main(String args[]) {
        String startTimeStr = "2019-01-01T00:00:00.00Z";
        String endTimeStr = "2019-01-01T01:00:00.00Z";
        Double probabilityMultiplier = 0.001;
        Double usersMultiplier = 0.001;

        // Read args
        for (int i=0; i < args.length; i++) {
            System.out.println(args[i]);
        }

        generate( startTimeStr, endTimeStr, probabilityMultiplier, usersMultiplier);


    }

    public static void generate(String startTimeStr, String endTimeStr, double probabilityMultiplier, double usersMultiplier) {
        StopWatch stopWatch = new StopWatch();

        System.out.println("=================== TEST PARAMETERS =============== ");
        System.out.println("start_time: " + startTimeStr);
        System.out.println("end_time: " + endTimeStr);
        System.out.println("probability_multiplier: " + probabilityMultiplier);
        System.out.println("users_multiplier: " + usersMultiplier);
        System.out.println("=================================================== ");

        Instant startInstant = Instant.parse(startTimeStr);
        Instant endInstant = Instant.parse(endTimeStr);

        int numOfNormalUsers = (int) (NUM_OF_NORMAL_USERS * usersMultiplier);
        int numOfAdminUsers = (int) (NUM_OF_ADMIN_USERS * usersMultiplier);
        int numOfserviceAccountUsers = (int) (NUM_OF_SERVICE_ACCOUNT_USERS * usersMultiplier);

        stopWatch.start();

        ProcessPerformanceStabilityScenario processScenario =
                new ProcessPerformanceStabilityScenario(
                        startInstant, endInstant,
                        numOfNormalUsers, numOfAdminUsers, numOfserviceAccountUsers,
                        probabilityMultiplier);

        try {
            printDaysOfProcessEvents(processScenario);
        } catch (GeneratorException e) {
            e.printStackTrace();
        }

        RegistryPerformanceStabilityScenario registryScenario =
                new RegistryPerformanceStabilityScenario(
                        startInstant, endInstant,
                        numOfNormalUsers, numOfAdminUsers, numOfserviceAccountUsers,
                        probabilityMultiplier);
        try {
            printDaysOfRegistryEvents(registryScenario);
        } catch (GeneratorException e) {
            e.printStackTrace();
        }

        stopWatch.split();
        System.out.println(stopWatch.toSplitString());
    }


    private static void printDaysOfProcessEvents(ProcessPerformanceStabilityScenario scenario) throws GeneratorException {
        /** Generate and send events **/
        EventLogPrinter logPrinter = new EventLogPrinterFactory().getPrinter(Schema.PROCESS);
        List<Event> events;
        do {
            events = scenario.generateEvents(EVENTS_GENERATION_CHUNK);
            logPrinter.printHourlyFiles(events);
        } while (events.size() == EVENTS_GENERATION_CHUNK);
    }

    private static void printDaysOfRegistryEvents(RegistryPerformanceStabilityScenario scenario) throws GeneratorException {
        /** Generate and send events **/
        EventLogPrinter logPrinter = new EventLogPrinterFactory().getPrinter(Schema.REGISTRY);
        List<Event> events;
        do {
            events = scenario.generateEvents(EVENTS_GENERATION_CHUNK);
            logPrinter.printHourlyFiles(events);
        } while (events.size() == EVENTS_GENERATION_CHUNK);
    }

}
