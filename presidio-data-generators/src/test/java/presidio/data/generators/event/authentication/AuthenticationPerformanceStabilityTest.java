package presidio.data.generators.event.authentication;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Test;
import presidio.data.domain.MachineEntity;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.RandomMultiMachineEntityGenerator;

import java.time.Instant;
import java.util.*;


public class AuthenticationPerformanceStabilityTest {

    private final int EVENTS_GENERATION_CHUNK = 1000000;
    private final int NUM_OF_GLOBAL_SERVER_MACHINES = 10000;
    private final int NUM_OF_GLOBAL_SERVER_MACHINES_CLUSTERS = 5000;
    private final String GLOBAL_SERVER_MACHINES_CLUSTER_PREFIX = "global_srv_";
    private final int NUM_OF_GLOBAL_SERVER_MACHINES_PER_CLUSTER = 50;
    private final int NUM_OF_ADMIN_SERVER_MACHINES = 20000;
    private final int NUM_OF_ADMIN_SERVER_MACHINES_CLUSTERS = 10000;
    private final String ADMIN_SERVER_MACHINES_CLUSTER_PREFIX = "admin_srv_";
    private final int NUM_OF_ADMIN_SERVER_MACHINES_PER_CLUSTER = 50;
    private final int NUM_OF_LOCAL_SERVER_MACHINES = 400000;
    private final int NUM_OF_LOCAL_SERVER_MACHINES_CLUSTERS = 100000;
    private final String LOCAL_SERVER_MACHINES_CLUSTER_PREFIX = "local_srv_";
    private final int NUM_OF_LOCAL_SERVER_MACHINES_PER_CLUSTER = 50;

    private StopWatch stopWatch = new StopWatch();

    private List<MachineEntity> createNonDesktopMachinePool(int numOfClusters, String clusterPrefix,
                                                            int numOfMachinesPerCluster, int numOfMachines){
        IMachineGenerator generator =
                new RandomMultiMachineEntityGenerator(
                        Arrays.asList("domain1"),
                        numOfClusters, clusterPrefix,
                        numOfMachinesPerCluster, "");
        Map<String, MachineEntity> nameToMachineEntityMap = new HashMap<>();
        while(nameToMachineEntityMap.size() < numOfMachines){
            MachineEntity machineEntity = generator.getNext();
            nameToMachineEntityMap.put(machineEntity.getMachineId(), machineEntity);
        }

        return new ArrayList<>(nameToMachineEntityMap.values());
    }

//    private List<MachineEntity> createMachineEntitiesPool(List<String> names){
//        List<MachineEntity> ret = new ArrayList<>();
//        for(String name: names){
//            FixedMachineGenerator fixedMachineGenerator = new FixedMachineGenerator(name);
//            ret.add(fixedMachineGenerator.getNext());
//        }
//        return ret;
//    }

    private List<MachineEntity> createGlobalServerMachinePool(){
        return createNonDesktopMachinePool(
                NUM_OF_GLOBAL_SERVER_MACHINES_CLUSTERS,
                GLOBAL_SERVER_MACHINES_CLUSTER_PREFIX,
                NUM_OF_GLOBAL_SERVER_MACHINES_PER_CLUSTER,
                NUM_OF_GLOBAL_SERVER_MACHINES);


    }

    private List<MachineEntity> createAdminServerMachinePool(){
        return createNonDesktopMachinePool(
                NUM_OF_ADMIN_SERVER_MACHINES_CLUSTERS,
                ADMIN_SERVER_MACHINES_CLUSTER_PREFIX,
                NUM_OF_ADMIN_SERVER_MACHINES_PER_CLUSTER,
                NUM_OF_ADMIN_SERVER_MACHINES);
    }

    private List<MachineEntity> createLocalServerMachinePool(){
        return createNonDesktopMachinePool(
                NUM_OF_LOCAL_SERVER_MACHINES_CLUSTERS,
                LOCAL_SERVER_MACHINES_CLUSTER_PREFIX,
                NUM_OF_LOCAL_SERVER_MACHINES_PER_CLUSTER,
                NUM_OF_LOCAL_SERVER_MACHINES);
    }


    @Test
    public void test() throws GeneratorException {
        stopWatch.start();

        Instant startInstant    = Instant.parse("2010-01-01T06:00:00.00Z");
        Instant endInstant      = Instant.parse("2010-01-01T06:01:00.00Z");

        AuthenticationPerformanceStabilityScenario scenario =
                new AuthenticationPerformanceStabilityScenario(
                        startInstant, endInstant, 1, 0.01,
                        createGlobalServerMachinePool(),
                        createLocalServerMachinePool(),
                        createAdminServerMachinePool());
        scenario.init();

        List<Event> events;
        do {
            events = scenario.generateEvents(EVENTS_GENERATION_CHUNK);

            Map<String, List<Event>> userToEvents = new HashMap<>();
            Map<String, List<Event>> srcMachineToEvents = new HashMap<>();
            Map<String, List<Event>> dstMachineToEvents = new HashMap<>();
            for(Event event: events){
                List<Event> userEvents = userToEvents.computeIfAbsent(((AuthenticationEvent)event).getUser().getUserId(), k -> new ArrayList<>());
                userEvents.add(event);
                List<Event> srcProcessEvents = srcMachineToEvents.computeIfAbsent(((AuthenticationEvent)event).getSrcMachineEntity().getMachineId(), k -> new ArrayList<>());
                srcProcessEvents.add(event);
                List<Event> dstProcessEvents = dstMachineToEvents.computeIfAbsent(((AuthenticationEvent)event).getDstMachineEntity().getMachineId(), k -> new ArrayList<>());
                dstProcessEvents.add(event);
            }

            stopWatch.split();
            System.out.println(stopWatch.toSplitString());
        } while (events.size() == EVENTS_GENERATION_CHUNK);

        stopWatch.split();
        System.out.println(stopWatch.toSplitString());
    }





}
