package presidio.data.generators.event.process;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Before;
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
import presidio.data.generators.user.LimitNumOfUsersGenerator;
import presidio.data.generators.user.NumberedUserRandomUniformallyGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class UserProcessEventsGeneratorTest {

    final int ACTIVE_TIME_INTERVAL = 1000000; // nanos
    final int EVENTS_GENERATION_CHUNK = 50000;
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
    private final int TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_NORMAL_USERS = 50000; //50 seconds. (8*3600/50)*0.49 =~280 users
    private final int MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 100;
    private final int MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 200;
    private final int NUM_OF_ADMIN_USERS = 5000;
    private final double PROBABILITY_ADMIN_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT = 0.3189;
    private final int TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_ADMIN_USERS = 50000; //50 seconds. (2*3600/50)*0.3189 =~45 users
    private final int MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 100;
    private final int MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 1000;
    private final int NUM_OF_SERVICE_ACCOUNT_USERS = 500;
    private final double PROBABILITY_SERVICE_ACCOUNT_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT = 0.06944;
    private final int TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_SERVICE_ACCOUNT_USERS = 1; //Not really relevant since service accounts work all day.
    private final int MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 5;
    private final int MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 200;


    //Reconnaissance tools Group A
    private final int RECON_TOOL_GROUP_A_NUM_OF_NORMAL_USERS = 65000;
    private final int RECON_TOOL_GROUP_A_NUM_OF_NORMAL_USERS_DAILY = 40000;
    private final double RECON_TOOL_GROUP_A_PROBABILITY_NORMAL_USER = 0.4921875;
    private final int RECON_TOOL_GROUP_A_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_NORMAL_USERS = 50000; //50 seconds. (8*3600/50)*0.49 =~280 users
    private final int RECON_TOOL_GROUP_A_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 10;
    private final int RECON_TOOL_GROUP_A_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 50;
    private final int RECON_TOOL_GROUP_A_NUM_OF_ADMIN_USERS = 3000;
    private final int RECON_TOOL_GROUP_A_NUM_OF_ADMIN_USERS_DAILY = 2000;
    private final double RECON_TOOL_GROUP_A_PROBABILITY_ADMIN_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT = 0.3189;
    private final int RECON_TOOL_GROUP_A_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_ADMIN_USERS = 50000; //50 seconds. (2*3600/50)*0.3189 =~45 users
    private final int RECON_TOOL_GROUP_A_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 100;
    private final int RECON_TOOL_GROUP_A_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 1000;
    private final int RECON_TOOL_GROUP_A_NUM_OF_SERVICE_ACCOUNT_USERS = 300;
    private final int RECON_TOOL_GROUP_A_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY = 200;
    private final double RECON_TOOL_GROUP_A_PROBABILITY_SERVICE_ACCOUNT_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT = 0.06944;
    private final int RECON_TOOL_GROUP_A_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_SERVICE_ACCOUNT_USERS = 1; //Not really relevant since service accounts work all day.
    private final int RECON_TOOL_GROUP_A_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 5;
    private final int RECON_TOOL_GROUP_A_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 200;



    /** USERS **/
    IUserGenerator normalUserGenerator;
    List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange;
    IUserGenerator adminUserGenerator;
    List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange;
    IUserGenerator serviceAccountUserGenerator;
    List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange;

    //For Reconnaissance tools A
    IUserGenerator ReconToolGroupAAllNormalUsers;
    IUserGenerator ReconToolGroupAAllAdminUsers;
    IUserGenerator ReconToolGroupAAllServiceAccountUsers;


    /** MACHINES **/
    IMachineGenerator machineGenerator;

    /** Processes **/
    List<FileEntity> nonImportantProcesses;


    /** GENERATORS: PROCESS **/

    //Generator for non Important Processes and normal users
    ProcessEventsGenerator nonImportantProcessForNormalUsersEventGenerator;

    //Generator for non Important Processes and admin users
    ProcessEventsGenerator nonImportantProcessForAdminUsersEventGenerator;

    //Generator for non Important Processes and service account users
    ProcessEventsGenerator nonImportantProcessForServiceAccountUsersEventGenerator;


    @Before
    public void prepareTest(){
        normalUserGenerator = createNormalUserGenerator();
        normalUserActivityRange = getNormalUserActivityRange();
        adminUserGenerator = createAdminUserGenerator();
        adminUserActivityRange = getAdminUserActivityRange();
        serviceAccountUserGenerator = createServiceAccountUserGenerator();
        serviceAcountUserActivityRange = getServiceAcountUserActivityRange();

        //For Reconnaissance tools A
        ReconToolGroupAAllNormalUsers = new LimitNumOfUsersGenerator(RECON_TOOL_GROUP_A_NUM_OF_NORMAL_USERS, normalUserGenerator);
        ReconToolGroupAAllAdminUsers = new LimitNumOfUsersGenerator(RECON_TOOL_GROUP_A_NUM_OF_ADMIN_USERS, normalUserGenerator);
        ReconToolGroupAAllServiceAccountUsers = new LimitNumOfUsersGenerator(RECON_TOOL_GROUP_A_NUM_OF_SERVICE_ACCOUNT_USERS, normalUserGenerator);


        /** MACHINES **/
        machineGenerator = createMachineGenerator();

        /** Processes **/
        nonImportantProcesses = generateFileEntities();



        /** GENERATORS: PROCESS **/

        //Generator for non Important Processes and normal users
        nonImportantProcessForNormalUsersEventGenerator =
                createNonImportantProcessEventGenerator(
                        machineGenerator,
                        normalUserGenerator,
                        nonImportantProcesses,
                        MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER,
                        MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER,
                        "nonImportantProcessForNormalUsersEventGenerator"
                );



        //Generator for non Important Processes and admin users
        nonImportantProcessForAdminUsersEventGenerator =
                createNonImportantProcessEventGenerator(
                        machineGenerator,
                        adminUserGenerator,
                        nonImportantProcesses,
                        MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER,
                        MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER,
                        "nonImportantProcessForAdminUsersEventGenerator"
                );

        //Generator for non Important Processes and service account users
        nonImportantProcessForServiceAccountUsersEventGenerator =
                createNonImportantProcessEventGenerator(
                        machineGenerator,
                        serviceAccountUserGenerator,
                        nonImportantProcesses,
                        MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER,
                        MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER,
                        "nonImportantProcessForServiceAccountUsersEventGenerator"
                );


    }



    @Test
    public void test() throws GeneratorException {
        stopWatch.start();

        Instant startInstant    = Instant.parse("2010-01-01T06:00:00.00Z");
        Instant endInstant      = Instant.parse("2010-01-01T06:05:00.00Z");

        // daily loop
        Instant startDailyInstant = startInstant;
        while(startDailyInstant.isBefore(endInstant)) {
            Instant endDailyInstant = startDailyInstant.truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS);
            if(endDailyInstant.isAfter(endInstant)){
                endDailyInstant = endInstant;
            }

            //get list of event generators
            List<AbstractEventGenerator<Event>> eventGenerators = new ArrayList<>();
            RandomMultiEventGenerator eventGenerator =
                    createNonImportantProcessForNormalUsersRandomEventGenerator(
                            startDailyInstant, endDailyInstant);
            eventGenerators.add(eventGenerator);
            eventGenerator =
                    createNonImportantProcessForAdminUsersEventGenerator(
                            startDailyInstant,endDailyInstant
                    );
            eventGenerators.add(eventGenerator);
            eventGenerator =
                    createNonImportantProcessForServiceAccountUsersEventGenerator(
                            startDailyInstant, endDailyInstant
                    );
            eventGenerators.add(eventGenerator);



            MultiEventGenerator multiEventGenerator = new MultiEventGenerator(eventGenerators);

            generateEvents(multiEventGenerator);

            stopWatch.split();
            System.out.println(stopWatch.toSplitString());

            startDailyInstant = endDailyInstant;
        }
    }

    private void generateEvents(IEventGenerator<Event> eventGenerator) throws GeneratorException {

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

    //==================================================================================
    // Creating Random Event Generators for daily events for all the different scenarios
    //==================================================================================

    private RandomMultiEventGenerator createNonImportantProcessForNormalUsersRandomEventGenerator(Instant startInstant,
                                                                                                  Instant endInstant){
        return createRandomEventGenerator(nonImportantProcessForNormalUsersEventGenerator,
                normalUserActivityRange,
                PROBABILITY_NORMAL_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT,
                TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_NORMAL_USERS,
                startInstant,
                endInstant
        );
    }

    private RandomMultiEventGenerator createNonImportantProcessForAdminUsersEventGenerator(Instant startInstant,
                                                                                           Instant endInstant){
        return createRandomEventGenerator(nonImportantProcessForAdminUsersEventGenerator,
                adminUserActivityRange,
                PROBABILITY_ADMIN_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT,
                TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_ADMIN_USERS,
                startInstant,
                endInstant
        );
    }

    private RandomMultiEventGenerator createNonImportantProcessForServiceAccountUsersEventGenerator(Instant startInstant,
                                                                                                    Instant endInstant){
        return createRandomEventGenerator(nonImportantProcessForServiceAccountUsersEventGenerator,
                serviceAcountUserActivityRange,
                PROBABILITY_SERVICE_ACCOUNT_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT,
                TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_SERVICE_ACCOUNT_USERS,
                startInstant,
                endInstant
        );
    }





    private RandomMultiEventGenerator createRandomEventGenerator(ProcessEventsGenerator processEventsGenerator,
                                                                 List<MultiRangeTimeGenerator.ActivityRange> rangesList,
                                                                 double eventProbability,
                                                                 int timeIntervalForAbnormalTime,
                                                                 Instant startInstant,
                                                                 Instant endInstant) {
        List< RandomMultiEventGenerator.EventGeneratorProbability > listOfProbabilities = new ArrayList<>();
        RandomMultiEventGenerator.EventGeneratorProbability eventsProbabilityForNormalUsers =
                new RandomMultiEventGenerator.EventGeneratorProbability(processEventsGenerator, eventProbability);
        listOfProbabilities.add(eventsProbabilityForNormalUsers);



        RandomMultiEventGenerator randomEventsGenerator = new RandomMultiEventGenerator(listOfProbabilities,
                startInstant, endInstant, rangesList, Duration.ofMillis((int) (timeIntervalForAbnormalTime) ));
        return randomEventsGenerator;
    }




    //==================================================================================
    // Creating Event Generators for all events for all the different scenarios
    // In the random generator which is built per day the time generator is added
    // In addition when building the random generator the set of users might be reduced.
    //==================================================================================

    private ProcessEventsGenerator createNonImportantProcessEventGenerator(IMachineGenerator machineGenerator,
                                                                              IUserGenerator userGenerator,
                                                                              List<FileEntity> processes,
                                                                              int minNumOfFilesPerUser,
                                                                              int maxNumOfFilesPerUser,
                                                                              String generatorName) {
        UserProcessEventsGenerator processNormalUsrEventsGenerator = new UserProcessEventsGenerator();
        processNormalUsrEventsGenerator.setUserGenerator(userGenerator);
        processNormalUsrEventsGenerator.setMachineEntityGenerator(machineGenerator);


        ProcessEntityGenerator srcProcessEntityGenerator = new ProcessEntityGenerator();
        srcProcessEntityGenerator.setProcessFileGenerator(new UserFileEntityGenerator(processes,
                minNumOfFilesPerUser, maxNumOfFilesPerUser));
        ProcessEntityGenerator dstProcessEntityGenerator = new ProcessEntityGenerator();
        dstProcessEntityGenerator.setProcessFileGenerator(new UserFileEntityGenerator(processes,
                minNumOfFilesPerUser, maxNumOfFilesPerUser));
        String[] operationTypeNames = {PROCESS_OPERATION_TYPE.OPEN_PROCESS.value, PROCESS_OPERATION_TYPE.CREATE_PROCESS.value, PROCESS_OPERATION_TYPE.CREATE_REMOTE_THREAD.value};
        fillProcessEventsGeneratorWithDefaultGenerators(processNormalUsrEventsGenerator, srcProcessEntityGenerator,
                dstProcessEntityGenerator, operationTypeNames, generatorName);

        return processNormalUsrEventsGenerator;
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
