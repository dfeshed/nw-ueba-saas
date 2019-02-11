package presidio.data.generators.event.process;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Test;
import presidio.data.domain.FileEntity;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.process.PROCESS_OPERATION_TYPE;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.*;
import presidio.data.generators.fileentity.RandomFileEntityGenerator;
import presidio.data.generators.fileentity.UserFileEntityGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.MachineGeneratorRouter;
import presidio.data.generators.machine.RandomMultiMachineEntityGenerator;
import presidio.data.generators.machine.UserDesktopGenerator;
import presidio.data.generators.processentity.ProcessEntityGenerator;
import presidio.data.generators.processop.ProcessOperationGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.NumberedUserRandomUniformallyGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;

public class UserProcessEventsGeneratorTest {

    final int ACTIVE_TIME_INTERVAL = 1000000; // nanos
    final int IDLE_TIME_INTERVAL = 500; // millis (event per 500 millis)
    final int EVENTS_GENERATION_CHUNK = 10000;
    final int EVENTS_INSERT_CHUNK = 4000;

    private StopWatch stopWatch = new StopWatch();



    /** Probabilities of events for different user groups
     * Calculated as: events_per_day / milliseconds_per_activity_period_in_a_day
     * Examples:
     * 1. 94500 users make 300 events per day in 16 active hours (6:00 - 22:00)
     * 94500*300         = 28350000    events per day
     * 16*60*60*1000    = 57,600,000  millisecond per day in users activity interval
     * 28350000/57600000  = 0.4921875
     *
     * 2. 5k users make 100 events per hour at 22 active hours
     * 5k * 100 * 22  = 11M   events per day
     * 22*60*60*1000 = 79.2M
     * 11M/79.2M = 0.3189
     *
     *
     * 3. 500 users make 500 events per hour at 24 active hours
     * 6M events per day
     * 6M / 86.4M = 0.06944
     *
     * !!! scenario with 10 users making 50K events per hour causes issue with garbage collector in hourly_output_processor !!!
     * ####10 * 50000 * 10 / 36000000 = 0.1389
     * **/

    private final int NUM_OF_NORMAL_USERS = 94500;
    private final double PROBABILITY_NORMAL_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT = 0.4921875;
    private final int MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 100;
    private final int MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 200;
    private final int NUM_OF_ADMIN_USERS = 5000;
    private final double PROBABILITY_ADMIN_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT = 0.3189;
    private final int NUM_OF_SERVICE_ACCOUNT_USERS = 500;
    private final double PROBABILITY_SERVICE_ACCOUNT_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT = 0.06944;





    @Test
    public void test() throws GeneratorException {
        stopWatch.start();



        Instant startInstant    = Instant.parse("2010-01-01T06:00:00.00Z");
        Instant endInstant      = Instant.parse("2010-01-01T06:05:00.00Z");








        /** USERS **/
        IUserGenerator normalUserGenerator = createNormalUserGenerator();
        List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange = getNormalUserActivityRange();


        /** MACHINES **/
        IMachineGenerator machineGenerator = createMachineGenerator();

        /** Processes **/
        List<FileEntity> nonImportantProcesses = generateFileEntities();



        /** GENERATORS: PROCESS **/
        RandomMultiEventGenerator nonImportantProcessNormalBehaviorForNormalUsersEventGenerator =
                createNonImportantProcessNormalBehaviorEventGenerator(
                        machineGenerator,
                        normalUserGenerator,
                        normalUserActivityRange,
                        PROBABILITY_NORMAL_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT,
                        nonImportantProcesses,
                        startInstant,
                        endInstant,
                        "nonImportantProcessNormalBehaviorForNormalUsersEventGenerator");

        List<AbstractEventGenerator<Event>> eventGenerators = new ArrayList<>();
        eventGenerators.add(nonImportantProcessNormalBehaviorForNormalUsersEventGenerator);
        MultiEventGenerator multiEventGenerator = new MultiEventGenerator(eventGenerators);

        generateDaysOfEvents(multiEventGenerator);

        stopWatch.split();
        System.out.println(stopWatch.toSplitString());
    }

    private void generateDaysOfEvents(IEventGenerator<Event> eventGenerator) throws GeneratorException {

        /** Generate and send events **/
        int iterations = 0;

        while (eventGenerator.hasNext() != null) {
            try {
                List<Event> events = eventGenerator.generate(EVENTS_GENERATION_CHUNK);

                Map<String, List<Event>> userToEvents = new HashMap<>();
                Map<String, List<Event>> srcProcessToEvents = new HashMap<>();
                for(Event event: events){
                    List<Event> userEvents = userToEvents.computeIfAbsent(((ProcessEvent)event).getUser().getUserId(), k -> new ArrayList<>());
                    userEvents.add(event);
                    List<Event> srcProcessEvents = srcProcessToEvents.computeIfAbsent(((ProcessEvent)event).getProcessOperation().getSourceProcess().getProcessFileName(), k -> new ArrayList<>());
                    srcProcessEvents.add(event);
                }
                stopWatch.split();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private IUserGenerator createNormalUserGenerator(){
        IUserGenerator userGenerator = new NumberedUserRandomUniformallyGenerator(NUM_OF_NORMAL_USERS, 1, "normal_user_", "UID", false, false);
        return userGenerator;
    }

    private List<MultiRangeTimeGenerator.ActivityRange> getNormalUserActivityRange(){
        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(6,0), LocalTime.of(22,0), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        return rangesList;
    }

    private IUserGenerator createAdminUserGenerator(){
        IUserGenerator userGenerator = new NumberedUserRandomUniformallyGenerator(NUM_OF_ADMIN_USERS, 1, "admin_user_", "UID", false, false);
        return userGenerator;
    }

    private List<MultiRangeTimeGenerator.ActivityRange> getAdminUserActivityRange(){
        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(0,0), LocalTime.of(22,0), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        return rangesList;
    }

    private IUserGenerator createServiceAccountUserGenerator(){
        IUserGenerator userGenerator = new NumberedUserRandomUniformallyGenerator(NUM_OF_SERVICE_ACCOUNT_USERS, 1, "sa_user_", "UID", false, false);
        return userGenerator;
    }

    private List<MultiRangeTimeGenerator.ActivityRange> getServiceAcountUserActivityRange(){
        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(0,0), LocalTime.of(23,59), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        return rangesList;
    }

    private IMachineGenerator createMachineGenerator(){
        IMachineGenerator src100MachinesGenerator = createNonDesktopMachineGenerator();

        List<MachineGeneratorRouter.MachineGeneratorWeight> machineGeneratorWeights = new ArrayList<>();
        machineGeneratorWeights.add(new MachineGeneratorRouter.MachineGeneratorWeight(5, src100MachinesGenerator));
        machineGeneratorWeights.add(new MachineGeneratorRouter.MachineGeneratorWeight(95, new UserDesktopGenerator()));
        MachineGeneratorRouter machineGeneratorRouter = new MachineGeneratorRouter(machineGeneratorWeights);

        return machineGeneratorRouter;
    }

    private IMachineGenerator createNonDesktopMachineGenerator(){
        return new RandomMultiMachineEntityGenerator(
                Arrays.asList("100m_domain1", "100m_domain2", "100m_domain3", "100m_domain4", "100m_domain5",
                        "100m_domain6", "100m_domain7", "100m_domain8", "100m_domain9", "100m_domain10"),
                10, "5machines_",
                10, "src");
    }

    private RandomMultiEventGenerator createNonImportantProcessNormalBehaviorEventGenerator(IMachineGenerator machineGenerator,
                                                                                            IUserGenerator normalUserGenerator,
                                                                                            List<MultiRangeTimeGenerator.ActivityRange> rangesList,
                                                                                            double eventProbability,
                                                                                            List<FileEntity> processes,
                                                                                            Instant startInstant,
                                                                                            Instant endInstant,
                                                                                            String generatorName) {
        UserProcessEventsGenerator processNormalUsrEventsGenerator = new UserProcessEventsGenerator();
        processNormalUsrEventsGenerator.setUserGenerator(normalUserGenerator);
        processNormalUsrEventsGenerator.setMachineEntityGenerator(machineGenerator);


        ProcessEntityGenerator srcProcessEntityGenerator = new ProcessEntityGenerator();
        srcProcessEntityGenerator.setProcessFileGenerator(new UserFileEntityGenerator(processes,
                MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER, MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER));
        ProcessEntityGenerator dstProcessEntityGenerator = new ProcessEntityGenerator();
        dstProcessEntityGenerator.setProcessFileGenerator(new UserFileEntityGenerator(processes,
                MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER, MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER));
        String[] operationTypeNames = {PROCESS_OPERATION_TYPE.OPEN_PROCESS.value, PROCESS_OPERATION_TYPE.CREATE_PROCESS.value, PROCESS_OPERATION_TYPE.CREATE_REMOTE_THREAD.value};
        fillProcessEventsGeneratorWithDefaultGenerators(processNormalUsrEventsGenerator, srcProcessEntityGenerator,
                dstProcessEntityGenerator, operationTypeNames, generatorName);

        List< RandomMultiEventGenerator.EventGeneratorProbability > listOfProbabilities = new ArrayList<>();
        RandomMultiEventGenerator.EventGeneratorProbability eventsProbabilityForNormalUsers =
                new RandomMultiEventGenerator.EventGeneratorProbability(processNormalUsrEventsGenerator, eventProbability);
        listOfProbabilities.add(eventsProbabilityForNormalUsers);


        RandomMultiEventGenerator randomEventsGenerator = new RandomMultiEventGenerator(listOfProbabilities,
                startInstant, endInstant, rangesList, Duration.ofMillis((int) (IDLE_TIME_INTERVAL) ));
        return randomEventsGenerator;
    }

    private void fillProcessEventsGeneratorWithDefaultGenerators(ProcessEventsGenerator processEventsGenerator,
                                                                 ProcessEntityGenerator srcProcessEntityGenerator,
                                                                 ProcessEntityGenerator dstProcessEntityGenerator,
                                                                 String[] operationTypeNames,
                                                                 String testCase){
        //operator generator
        ProcessOperationGenerator opGenerator = new ProcessOperationGenerator();

        CyclicOperationTypeGenerator opTypeGenerator = new CyclicOperationTypeGenerator(operationTypeNames);
        opGenerator.setOperationTypeGenerator(opTypeGenerator);
        opGenerator.setSourceProcessEntityGenerator(srcProcessEntityGenerator);
        opGenerator.setDestProcessEntityGenerator(dstProcessEntityGenerator);
        processEventsGenerator.setProcessOperationGenerator(opGenerator);
        //event id generator
        EntityEventIDFixedPrefixGenerator eventIdGen = new EntityEventIDFixedPrefixGenerator(testCase);
        processEventsGenerator.setEventIdGenerator(eventIdGen);
    }

    private List<FileEntity> generateFileEntities(){
        RandomFileEntityGenerator randomFileEntityGenerator =
                new RandomFileEntityGenerator(1000, "dir", "",
                        10000, "proc", ".exe");
        Set<FileEntity> fileEntitySet = new HashSet<>();
        while(fileEntitySet.size() < 10000){
            fileEntitySet.add(randomFileEntityGenerator.getNext());
        }
        return new ArrayList<>(fileEntitySet);
    }



}
