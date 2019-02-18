package presidio.data.generators.event.process;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.FileEntity;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.process.PROCESS_OPERATION_TYPE;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.RandomMultiEventGenerator;
import presidio.data.generators.fileentity.ProcessFileEntityGenerator;
import presidio.data.generators.fileentity.RandomFileEntityGenerator;
import presidio.data.generators.fileentity.UserFileEntityGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.processentity.ProcessEntityGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


//Non-Important processes (Not: reconnaissance, scripting engine, important windows processes)
//general scenario where most of the load is.
public class NonImportantProcessEventGeneratorsBuilder extends ProcessEventGeneratorsBuilder{
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

    private final double PROBABILITY_NORMAL_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT = 0.4921875;
    private final int TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_NORMAL_USERS = 50000; //50 seconds. (8*3600/50)*0.49 =~280 users
    private final int MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 100;
    private final int MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 200;

    private final double PROBABILITY_ADMIN_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT = 0.3189;
    private final int TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_ADMIN_USERS = 50000; //50 seconds. (2*3600/50)*0.3189 =~45 users
    private final int MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 100;
    private final int MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 1000;

    private final double PROBABILITY_SERVICE_ACCOUNT_USER_WITH_NON_IMPORTANT_PROCESSES_PROCESS_EVENT = 0.06944;
    private final int TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_SERVICE_ACCOUNT_USERS = 1; //Not really relevant since service accounts work all day.
    private final int MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 5;
    private final int MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 200;


    /** GENERATORS: PROCESS **/

    //Generator for non Important Processes and normal users
    ProcessEventsGenerator nonImportantProcessForNormalUsersEventGenerator;

    //Generator for non Important Processes and admin users
    ProcessEventsGenerator nonImportantProcessForAdminUsersEventGenerator;

    //Generator for non Important Processes and service account users
    ProcessEventsGenerator nonImportantProcessForServiceAccountUsersEventGenerator;

    public NonImportantProcessEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
                                                     List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange,
                                                     List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange,
                                                     IUserGenerator adminUserGenerator,
                                                     List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange,
                                                     List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange,
                                                     IUserGenerator serviceAccountUserGenerator,
                                                     List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange,
                                                     IMachineGenerator machineGenerator,
                                                     List<FileEntity> nonImportantProcesses){

        super(normalUserGenerator, normalUserActivityRange, normalUserAbnormalActivityRange,
                adminUserGenerator, adminUserActivityRange, adminUserAbnormalActivityRange,
                serviceAccountUserGenerator, serviceAcountUserActivityRange,
                machineGenerator, nonImportantProcesses);
        createGenerators();
    }


    private void createGenerators(){

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



    public List<AbstractEventGenerator<Event>> buildGenerators(Instant startInstant, Instant endInstant) throws GeneratorException {
        List<AbstractEventGenerator<Event>> eventGenerators = new ArrayList<>();
        RandomMultiEventGenerator eventGenerator =
                createNonImportantProcessForNormalUsersRandomEventGenerator(
                        startInstant, endInstant);
        eventGenerators.add(eventGenerator);
        eventGenerator =
                createNonImportantProcessForAdminUsersEventGenerator(
                        startInstant,endInstant
                );
        eventGenerators.add(eventGenerator);
        eventGenerator =
                createNonImportantProcessForServiceAccountUsersEventGenerator(
                        startInstant, endInstant
                );
        eventGenerators.add(eventGenerator);

        return eventGenerators;
    }

    //==================================================================================
    // Creating Random Event Generators for all kind of users
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








    //==================================================================================
    // Creating Event Generators for all events for all the different kind of users
    // In the random generator which is built above the time generator is added
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
}
