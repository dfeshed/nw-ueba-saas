package presidio.data.generators.event.process;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import presidio.data.domain.FileEntity;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.process.PROCESS_OPERATION_TYPE;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.*;
import presidio.data.generators.fileentity.ProcessFileEntityGenerator;
import presidio.data.generators.fileentity.RandomFileEntityGenerator;
import presidio.data.generators.fileentity.UserFileEntityGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.MachineGeneratorRouter;
import presidio.data.generators.machine.RandomMultiMachineEntityGenerator;
import presidio.data.generators.machine.UserDesktopGenerator;
import presidio.data.generators.processentity.ProcessCategoriesGenerator;
import presidio.data.generators.processentity.ProcessDirectoryGroupsGenerator;
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

    final Pair[] WINDOWS_PROCESS_FILES = { Pair.of("taskhostw.exe"," C:\\Windows\\System32\\"),
            Pair.of("smss.exe"," C:\\Windows\\System32\\"),
            Pair.of("lsass.exe"," C:\\Windows\\System32\\"),
            Pair.of("services.exe"," C:\\Windows\\System32\\"),
            Pair.of("lsaiso.exe"," C:\\Windows\\System32\\") };

    public static final Pair[] RECON_TOOLS = new Pair[] {
            Pair.of("arp","C:\\Windows\\System32"),
            Pair.of("arp.exe","C:\\Windows\\System32"),
            Pair.of("dsget.exe","C:\\Windows\\System32"),
            Pair.of("dsquery.exe","C:\\Windows\\System32"),
            Pair.of("forfiles.exe","C:\\Windows\\System32"),
            Pair.of("fsutil.exe","C:\\Windows\\System32"),
            Pair.of("hostname","C:\\Windows\\System32"),
            Pair.of("hostname.exe","C:\\Windows\\System32"),
            Pair.of("ipconfig.exe","C:\\Windows\\System32"),
            Pair.of("net","C:\\Windows\\System32"),
            Pair.of("net.exe","C:\\Windows\\System32"),
            Pair.of("netdom.exe","C:\\Windows\\System32"),
            Pair.of("netsh","C:\\Windows\\System32"),
            Pair.of("netsh.exe","C:\\Windows\\System32"),
            Pair.of("netstat","C:\\Windows\\System32"),
            Pair.of("netstat.exe","C:\\Windows\\System32"),
            Pair.of("nbtstat.exe","C:\\Windows\\System32"),
            Pair.of("nltest.exe","C:\\Windows\\System32"),
            Pair.of("ping","C:\\Windows\\System32"),
            Pair.of("ping.exe","C:\\Windows\\System32"),
            Pair.of("quser.exe","C:\\Windows\\System32"),
            Pair.of("qprocess.exe","C:\\Windows\\System32"),
            Pair.of("qwinsta.exe","C:\\Windows\\System32"),
            Pair.of("reg.exe","C:\\Windows\\System32"),
            Pair.of("route","C:\\Windows\\System32"),
            Pair.of("route.exe","C:\\Windows\\System32"),
            Pair.of("sc.exe","C:\\Windows\\System32"),
            Pair.of("systeminfo.exe","C:\\Windows\\System32"),
            Pair.of("tasklist.exe","C:\\Windows\\System32"),
            Pair.of("tree","C:\\Windows\\System32"),
            Pair.of("tree.exe","C:\\Windows\\System32"),
            Pair.of("whoami","C:\\Windows\\System32"),
            Pair.of("whoami.exe","C:\\Windows\\System32"),
            Pair.of("wmic.exe","C:\\Windows\\System32")
    };

    public static final Pair[] RECON_TOOLS_GROUP_A = new Pair[] {
            Pair.of("arp","C:\\Windows\\System32"),
            Pair.of("arp.exe","C:\\Windows\\System32"),
            Pair.of("hostname","C:\\Windows\\System32"),
            Pair.of("hostname.exe","C:\\Windows\\System32"),
            Pair.of("ipconfig.exe","C:\\Windows\\System32"),
            Pair.of("net","C:\\Windows\\System32"),
            Pair.of("net.exe","C:\\Windows\\System32"),
            Pair.of("netstat","C:\\Windows\\System32"),
            Pair.of("netstat.exe","C:\\Windows\\System32"),
            Pair.of("nbtstat.exe","C:\\Windows\\System32"),
            Pair.of("sc.exe","C:\\Windows\\System32"),
            Pair.of("systeminfo.exe","C:\\Windows\\System32"),
            Pair.of("wmic.exe","C:\\Windows\\System32")
    };

    final Pair[] SCRIPTING_ENGINE = {
            Pair.of("cscript.exe","D:\\Windows\\System32"),
            Pair.of("mshta.exe","C:\\Windows\\PY"),
            Pair.of("powershell.exe","D:\\Program Files (x86)\\JavaScript"),
            Pair.of("wscript.exe","D:\\Program Files")
    };

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


    //Reconnaissance tools Group A. Normal behavior + abnormal time.
    private final int RECON_TOOL_GROUP_A_NUM_OF_NORMAL_USERS = 65000;
    private final int RECON_TOOL_GROUP_A_NUM_OF_NORMAL_USERS_DAILY = 40000;
    private final double RECON_TOOL_GROUP_A_PROBABILITY_NORMAL_USER = 0.03; //~3 events per hour per user
    private final int RECON_TOOL_GROUP_A_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_NORMAL_USERS = 50000; //50 seconds. (8*3600/50)*0.03 =~17 users
    private final int RECON_TOOL_GROUP_A_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 10;
    private final int RECON_TOOL_GROUP_A_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 50;
    private final int RECON_TOOL_GROUP_A_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER = 1;
    private final int RECON_TOOL_GROUP_A_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER = 10;
    private final int RECON_TOOL_GROUP_A_NUM_OF_ADMIN_USERS = 3000;
    private final int RECON_TOOL_GROUP_A_NUM_OF_ADMIN_USERS_DAILY = 2000;
    private final double RECON_TOOL_GROUP_A_PROBABILITY_ADMIN_USER = 0.1; // 180 events per hour per user
    private final int RECON_TOOL_GROUP_A_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_ADMIN_USERS = 50000; //50 seconds. (2*3600/50)*0.1 =~15 users
    private final int RECON_TOOL_GROUP_A_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 100;
    private final int RECON_TOOL_GROUP_A_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 1000;
    private final int RECON_TOOL_GROUP_A_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER = 1;
    private final int RECON_TOOL_GROUP_A_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER = 10;
    private final int RECON_TOOL_GROUP_A_NUM_OF_SERVICE_ACCOUNT_USERS = 300;
    private final int RECON_TOOL_GROUP_A_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY = 200;
    private final double RECON_TOOL_GROUP_A_PROBABILITY_SERVICE_ACCOUNT_USER = 0.02; // ~360 events per hour per user
    private final int RECON_TOOL_GROUP_A_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_SERVICE_ACCOUNT_USERS = 1; //Not really relevant since service accounts work all day.
    private final int RECON_TOOL_GROUP_A_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 5;
    private final int RECON_TOOL_GROUP_A_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 200;
    private final int RECON_TOOL_GROUP_A_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1;
    private final int RECON_TOOL_GROUP_A_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER = 10;

    //Reconnaissance tools Group A. abnormal behavior.
    private final int ABNORMAL_RECON_TOOL_GROUP_A_NUM_OF_NORMAL_USERS_DAILY = 180;
    private final double ABNORMAL_RECON_TOOL_GROUP_A_PROBABILITY_NORMAL_USER = 0.00081; // ~16 events per hour per user
    private final int ABNORMAL_RECON_TOOL_GROUP_A_TIME_INTERVAL_FOR_OTHER_ACTIVITY_RANGE_FOR_NORMAL_USERS = 1800000; //Half an hour
    private final int ABNORMAL_RECON_TOOL_GROUP_A_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 1000;
    private final int ABNORMAL_RECON_TOOL_GROUP_A_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER = 5000;
    private final int ABNORMAL_RECON_TOOL_GROUP_A_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER = 1000;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_A_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER = 2000;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_A_NUM_OF_ADMIN_USERS_DAILY = 13;
    private final double ABNORMAL_RECON_TOOL_GROUP_A_PROBABILITY_ADMIN_USER = 0.001625; // ~450 events per hour
    private final int ABNORMAL_RECON_TOOL_GROUP_A_TIME_INTERVAL_FOR_OTHER_ACTIVITY_RANGE_FOR_ADMIN_USERS = 1800000; //Half an hour
    private final int ABNORMAL_RECON_TOOL_GROUP_A_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 100;
    private final int ABNORMAL_RECON_TOOL_GROUP_A_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER = 1000;
    private final int ABNORMAL_RECON_TOOL_GROUP_A_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER = 1000;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_A_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER = 2000;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_A_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY = 2;
    private final double ABNORMAL_RECON_TOOL_GROUP_A_PROBABILITY_SERVICE_ACCOUNT_USER = 0.0005; // ~900 events per hour per user
    private final int ABNORMAL_RECON_TOOL_GROUP_A_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_SERVICE_ACCOUNT_USERS = 1; //Not really relevant since service accounts work all day.
    private final int ABNORMAL_RECON_TOOL_GROUP_A_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 100;
    private final int ABNORMAL_RECON_TOOL_GROUP_A_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1000;
    private final int ABNORMAL_RECON_TOOL_GROUP_A_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER = 1000;//use all recon tool that you can
    private final int ABNORMAL_RECON_TOOL_GROUP_A_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER = 2000;//use all recon tool that you can



    /** USERS **/
    IUserGenerator normalUserGenerator;
    List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange;
    List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange;
    IUserGenerator adminUserGenerator;
    List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange;
    List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange;
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

    //Generator for recon tool group A Processes and normal users
    ProcessEventsGenerator reconToolGroupAForNormalUsersEventGenerator;

    //Generator for recon tool group A Processes and admin users
    ProcessEventsGenerator reconToolGroupAForAdminUsersEventGenerator;

    //Generator for recon tool group A Processes and service account users
    ProcessEventsGenerator reconToolGroupAForServiceAccountUsersEventGenerator;

    //Abnormal events Generator for recon tool group A Processes and normal users
    ProcessEventsGenerator reconToolGroupAForNormalUsersAbnormalEventGenerator;

    //Abnormal events Generator for recon tool group A Processes and admin users
    ProcessEventsGenerator reconToolGroupAForAdminUsersAbnormalEventGenerator;

    //Abnormal events Generator for recon tool group A Processes and service account users
    ProcessEventsGenerator reconToolGroupAForServiceAccountUsersAbnormalEventGenerator;


    @Before
    public void prepareTest(){
        normalUserGenerator = createNormalUserGenerator();
        normalUserActivityRange = getNormalUserActivityRange();
        normalUserAbnormalActivityRange = getNormalUserAbnormalActivityRange();
        adminUserGenerator = createAdminUserGenerator();
        adminUserActivityRange = getAdminUserActivityRange();
        adminUserAbnormalActivityRange = getAdminUserAbnormalActivityRange();
        serviceAccountUserGenerator = createServiceAccountUserGenerator();
        serviceAcountUserActivityRange = getServiceAcountUserActivityRange();

        //For Reconnaissance tools A
        ReconToolGroupAAllNormalUsers = new LimitNumOfUsersGenerator(RECON_TOOL_GROUP_A_NUM_OF_NORMAL_USERS, normalUserGenerator);
        ReconToolGroupAAllAdminUsers = new LimitNumOfUsersGenerator(RECON_TOOL_GROUP_A_NUM_OF_ADMIN_USERS, adminUserGenerator);
        ReconToolGroupAAllServiceAccountUsers = new LimitNumOfUsersGenerator(RECON_TOOL_GROUP_A_NUM_OF_SERVICE_ACCOUNT_USERS, serviceAccountUserGenerator);


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

        //+++++++++++ Recon Tool Group A ++++++++++++//

        //Generator for recon tool group A Processes and normal users
        reconToolGroupAForNormalUsersEventGenerator =
                createReconToolGroupAEventGenerator(
                        machineGenerator,
                        nonImportantProcesses,
                        RECON_TOOL_GROUP_A_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER,
                        RECON_TOOL_GROUP_A_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER,
                        RECON_TOOL_GROUP_A_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER,
                        RECON_TOOL_GROUP_A_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER,
                        "reconToolGroupAForNormalUsersEventGenerator"
                );



        //Generator for recon tool group A Processes and admin users
        reconToolGroupAForAdminUsersEventGenerator =
                createReconToolGroupAEventGenerator(
                        machineGenerator,
                        nonImportantProcesses,
                        RECON_TOOL_GROUP_A_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER,
                        RECON_TOOL_GROUP_A_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER,
                        RECON_TOOL_GROUP_A_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER,
                        RECON_TOOL_GROUP_A_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER,
                        "reconToolGroupAForAdminUsersEventGenerator"
                );

        //Generator for recon tool group A Processes and service account users
        reconToolGroupAForServiceAccountUsersEventGenerator =
                createReconToolGroupAEventGenerator(
                        machineGenerator,
                        nonImportantProcesses,
                        RECON_TOOL_GROUP_A_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER,
                        RECON_TOOL_GROUP_A_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER,
                        RECON_TOOL_GROUP_A_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER,
                        RECON_TOOL_GROUP_A_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER,
                        "reconToolGroupAForServiceAccountUsersEventGenerator"
                );


        //Abnormal events Generator for recon tool group A Processes and normal users
        reconToolGroupAForNormalUsersAbnormalEventGenerator =
                createReconToolGroupAEventGenerator(
                        machineGenerator,
                        nonImportantProcesses,
                        ABNORMAL_RECON_TOOL_GROUP_A_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER,
                        ABNORMAL_RECON_TOOL_GROUP_A_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_NORMAL_USER,
                        ABNORMAL_RECON_TOOL_GROUP_A_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER,
                        ABNORMAL_RECON_TOOL_GROUP_A_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_NORMAL_USER,
                        "reconToolGroupAForNormalUsersAbnormalEventGenerator"
                );



        //Abnormal events Generator for recon tool group A Processes and admin users
        reconToolGroupAForAdminUsersAbnormalEventGenerator =
                createReconToolGroupAEventGenerator(
                        machineGenerator,
                        nonImportantProcesses,
                        ABNORMAL_RECON_TOOL_GROUP_A_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER,
                        ABNORMAL_RECON_TOOL_GROUP_A_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_ADMIN_USER,
                        ABNORMAL_RECON_TOOL_GROUP_A_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER,
                        ABNORMAL_RECON_TOOL_GROUP_A_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_ADMIN_USER,
                        "reconToolGroupAForAdminUsersAbnormalEventGenerator"
                );

        //Abnormal events Generator for recon tool group A Processes and service account users
        reconToolGroupAForServiceAccountUsersAbnormalEventGenerator =
                createReconToolGroupAEventGenerator(
                        machineGenerator,
                        nonImportantProcesses,
                        ABNORMAL_RECON_TOOL_GROUP_A_MIN_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER,
                        ABNORMAL_RECON_TOOL_GROUP_A_MAX_NUM_OF_NON_IMPORTANT_PROCESSES_PER_SERVICE_ACCOUNT_USER,
                        ABNORMAL_RECON_TOOL_GROUP_A_MIN_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER,
                        ABNORMAL_RECON_TOOL_GROUP_A_MAX_NUM_OF_RECON_TOOL_PROCESSES_PER_SERVICE_ACCOUNT_USER,
                        "reconToolGroupAForServiceAccountUsersAbnormalEventGenerator"
                );

    }



    @Test
    public void test() throws GeneratorException {
        stopWatch.start();

        Instant startInstant    = Instant.parse("2010-01-01T22:00:00.00Z");
        Instant endInstant      = Instant.parse("2010-01-02T01:05:00.00Z");

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

            //+++++++++++ Recon Tool Group A ++++++++++++//

            eventGenerator =
                    createReconToolGroupAForNormalUsersRandomEventGenerator(
                            startDailyInstant, endDailyInstant
                    );
            eventGenerators.add(eventGenerator);
            eventGenerator =
                    createReconToolGroupAForAdminUsersEventGenerator(
                            startDailyInstant, endDailyInstant
                    );
            eventGenerators.add(eventGenerator);
            eventGenerator =
                    createReconToolGroupAForServiceAccountUsersEventGenerator(
                            startDailyInstant, endDailyInstant
                    );
            eventGenerators.add(eventGenerator);

            //Abnormal events:
            eventGenerator =
                    createReconToolGroupAForNormalUsersRandomAbnormalEventGenerator(
                            startDailyInstant, endDailyInstant
                    );
            eventGenerators.add(eventGenerator);
            eventGenerator =
                    createReconToolGroupAForAdminUsersAbnormalEventGenerator(
                            startDailyInstant, endDailyInstant
                    );
            eventGenerators.add(eventGenerator);
            eventGenerator =
                    createReconToolGroupAForServiceAccountUsersAbnormalEventGenerator(
                            startDailyInstant, endDailyInstant
                    );
            eventGenerators.add(eventGenerator);



            MultiEventGenerator multiEventGenerator = new MultiEventGenerator(eventGenerators);

            generateEvents(multiEventGenerator);

            stopWatch.split();
//            System.out.println(stopWatch.toSplitString());

            startDailyInstant = endDailyInstant;
        }
    }

    private void generateEvents(IEventGenerator<Event> eventGenerator) throws GeneratorException {

        /** Generate and send events **/

        while (eventGenerator.hasNext() != null) {
            try {
                List<Event> events = eventGenerator.generate(EVENTS_GENERATION_CHUNK);
                Map<String, List<Event>> userToEvents = new HashMap<>();
                Map<String, List<Event>> srcProcessToEvents = new HashMap<>();
                Map<String, List<Event>> dstProcessToEvents = new HashMap<>();
                for(Event event: events){
                    List<Event> userEvents = userToEvents.computeIfAbsent(((ProcessEvent)event).getUser().getUserId(), k -> new ArrayList<>());
                    userEvents.add(event);
                    List<Event> srcProcessEvents = srcProcessToEvents.computeIfAbsent(((ProcessEvent)event).getProcessOperation().getSourceProcess().getProcessFileName(), k -> new ArrayList<>());
                    srcProcessEvents.add(event);
                    List<Event> dstProcessEvents = dstProcessToEvents.computeIfAbsent(((ProcessEvent)event).getProcessOperation().getDestinationProcess().getProcessFileName(), k -> new ArrayList<>());
                    dstProcessEvents.add(event);
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

    //Non-Important processes (Not: reconnaissance, scripting engine, important windows processes)
    //general scenario where most of the load is.
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


    //Reconnaissance tools A: Destination processes: [arp, systeminfo, net, sc, netstat, nbtstat, ipconfig, wmic, hostname]
    //NOTICE: It is expected that the userGenerator will be set at each day that is generated.

    private RandomMultiEventGenerator createReconToolGroupAForNormalUsersRandomEventGenerator(Instant startInstant,
                                                                                                  Instant endInstant){

        IUserGenerator reconToolGroupANormalUsersDailyGenerator = new LimitNumOfUsersGenerator(RECON_TOOL_GROUP_A_NUM_OF_NORMAL_USERS_DAILY, ReconToolGroupAAllNormalUsers);
        reconToolGroupAForNormalUsersEventGenerator.setUserGenerator(reconToolGroupANormalUsersDailyGenerator);
        return createRandomEventGenerator(reconToolGroupAForNormalUsersEventGenerator,
                normalUserActivityRange,
                RECON_TOOL_GROUP_A_PROBABILITY_NORMAL_USER,
                RECON_TOOL_GROUP_A_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_NORMAL_USERS,
                startInstant,
                endInstant
        );
    }

    private RandomMultiEventGenerator createReconToolGroupAForNormalUsersRandomAbnormalEventGenerator(Instant startInstant,
                                                                                              Instant endInstant){

        IUserGenerator reconToolGroupANormalUsersDailyGenerator = new LimitNumOfUsersGenerator(ABNORMAL_RECON_TOOL_GROUP_A_NUM_OF_NORMAL_USERS_DAILY, ReconToolGroupAAllNormalUsers);
        reconToolGroupAForNormalUsersAbnormalEventGenerator.setUserGenerator(reconToolGroupANormalUsersDailyGenerator);
        return createRandomEventGenerator(reconToolGroupAForNormalUsersAbnormalEventGenerator,
                normalUserAbnormalActivityRange,
                ABNORMAL_RECON_TOOL_GROUP_A_PROBABILITY_NORMAL_USER,
                ABNORMAL_RECON_TOOL_GROUP_A_TIME_INTERVAL_FOR_OTHER_ACTIVITY_RANGE_FOR_NORMAL_USERS,
                startInstant,
                endInstant
        );
    }

    private RandomMultiEventGenerator createReconToolGroupAForAdminUsersEventGenerator(Instant startInstant,
                                                                                       Instant endInstant){
        IUserGenerator reconToolGroupAAdminUsersDailyGenerator = new LimitNumOfUsersGenerator(RECON_TOOL_GROUP_A_NUM_OF_ADMIN_USERS_DAILY, ReconToolGroupAAllAdminUsers);
        reconToolGroupAForAdminUsersEventGenerator.setUserGenerator(reconToolGroupAAdminUsersDailyGenerator);
        return createRandomEventGenerator(reconToolGroupAForAdminUsersEventGenerator,
                adminUserActivityRange,
                RECON_TOOL_GROUP_A_PROBABILITY_ADMIN_USER,
                RECON_TOOL_GROUP_A_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_ADMIN_USERS,
                startInstant,
                endInstant
        );
    }

    private RandomMultiEventGenerator createReconToolGroupAForAdminUsersAbnormalEventGenerator(Instant startInstant,
                                                                                       Instant endInstant){
        IUserGenerator reconToolGroupAAdminUsersDailyGenerator = new LimitNumOfUsersGenerator(ABNORMAL_RECON_TOOL_GROUP_A_NUM_OF_ADMIN_USERS_DAILY, ReconToolGroupAAllAdminUsers);
        reconToolGroupAForAdminUsersAbnormalEventGenerator.setUserGenerator(reconToolGroupAAdminUsersDailyGenerator);
        return createRandomEventGenerator(reconToolGroupAForAdminUsersAbnormalEventGenerator,
                adminUserAbnormalActivityRange,
                ABNORMAL_RECON_TOOL_GROUP_A_PROBABILITY_ADMIN_USER,
                ABNORMAL_RECON_TOOL_GROUP_A_TIME_INTERVAL_FOR_OTHER_ACTIVITY_RANGE_FOR_ADMIN_USERS,
                startInstant,
                endInstant
        );
    }

    private RandomMultiEventGenerator createReconToolGroupAForServiceAccountUsersEventGenerator(Instant startInstant,
                                                                                                Instant endInstant){
        IUserGenerator reconToolGroupAServiceAccountUsersDailyGenerator = new LimitNumOfUsersGenerator(RECON_TOOL_GROUP_A_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY, ReconToolGroupAAllServiceAccountUsers);
        reconToolGroupAForServiceAccountUsersEventGenerator.setUserGenerator(reconToolGroupAServiceAccountUsersDailyGenerator);
        return createRandomEventGenerator(reconToolGroupAForServiceAccountUsersEventGenerator,
                serviceAcountUserActivityRange,
                RECON_TOOL_GROUP_A_PROBABILITY_SERVICE_ACCOUNT_USER,
                RECON_TOOL_GROUP_A_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_SERVICE_ACCOUNT_USERS,
                startInstant,
                endInstant
        );
    }

    private RandomMultiEventGenerator createReconToolGroupAForServiceAccountUsersAbnormalEventGenerator(Instant startInstant,
                                                                                                Instant endInstant){
        IUserGenerator reconToolGroupAServiceAccountUsersDailyGenerator = new LimitNumOfUsersGenerator(ABNORMAL_RECON_TOOL_GROUP_A_NUM_OF_SERVICE_ACCOUNT_USERS_DAILY, ReconToolGroupAAllServiceAccountUsers);
        reconToolGroupAForServiceAccountUsersAbnormalEventGenerator.setUserGenerator(reconToolGroupAServiceAccountUsersDailyGenerator);
        return createRandomEventGenerator(reconToolGroupAForServiceAccountUsersAbnormalEventGenerator,
                serviceAcountUserActivityRange,
                ABNORMAL_RECON_TOOL_GROUP_A_PROBABILITY_SERVICE_ACCOUNT_USER,
                ABNORMAL_RECON_TOOL_GROUP_A_TIME_INTERVAL_FOR_ABNORMAL_TIME_FOR_SERVICE_ACCOUNT_USERS,
                startInstant,
                endInstant
        );
    }





    //==================================================================================
    // Creating Event Generators for all events for all the different scenarios
    // In the random generator which is built per day the time generator is added
    // In addition when building the random generator the set of users might be reduced.
    //==================================================================================

    //Non-Important processes (Not: reconnaissance, scripting engine, important windows processes)
    //general scenario where most of the load is.

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



    private List<FileEntity> getReconToolGroupAFileEnities(){
        return getFileEnities(RECON_TOOLS_GROUP_A);
    }


    //Reconnaissance tools A: Destination processes: [arp, systeminfo, net, sc, netstat, nbtstat, ipconfig, wmic, hostname]
    //NOTICE: It is expected that the userGenerator will be set at each day that is generated.
    private ProcessEventsGenerator createReconToolGroupAEventGenerator(IMachineGenerator machineGenerator,
                                                                       List<FileEntity> nonImportantProcesses,
                                                                       int minNumOfFilesPerUserForNonImportantProcesses,
                                                                       int maxNumOfFilesPerUserForNonImportantProcesses,
                                                                       int minNumOfFilesPerUserForReconTool,
                                                                       int maxNumOfFilesPerUserForReconTool,
                                                                       String generatorName) {
        String[] operationTypeNames = {PROCESS_OPERATION_TYPE.CREATE_PROCESS.value};
        return createReconToolEventGenerator(
                machineGenerator,
                nonImportantProcesses,
                minNumOfFilesPerUserForNonImportantProcesses,
                maxNumOfFilesPerUserForNonImportantProcesses,
                getReconToolGroupAFileEnities(),
                minNumOfFilesPerUserForReconTool,
                maxNumOfFilesPerUserForReconTool,
                operationTypeNames,
                generatorName
        );
    }

    private ProcessEventsGenerator createReconToolEventGenerator(IMachineGenerator machineGenerator,
                                                                 List<FileEntity> nonImportantProcesses,
                                                                 int minNumOfFilesPerUserForNonImportantProcesses,
                                                                 int maxNumOfFilesPerUserForNonImportantProcesses,
                                                                 List<FileEntity> reconToolProcesses,
                                                                 int minNumOfFilesPerUserForReconTool,
                                                                 int maxNumOfFilesPerUserForReconTool,
                                                                 String[] operationTypeNames,
                                                                 String generatorName) {
        UserProcessEventsGenerator processNormalUsrEventsGenerator = new UserProcessEventsGenerator();
        processNormalUsrEventsGenerator.setMachineEntityGenerator(machineGenerator);


        ProcessEntityGenerator srcProcessEntityGenerator = new ProcessEntityGenerator();
        srcProcessEntityGenerator.setProcessFileGenerator(new UserFileEntityGenerator(nonImportantProcesses,
                minNumOfFilesPerUserForNonImportantProcesses, maxNumOfFilesPerUserForNonImportantProcesses));

        ProcessEntityGenerator dstProcessEntityGenerator = new ProcessEntityGenerator();
        dstProcessEntityGenerator.setProcessFileGenerator(new UserFileEntityGenerator(reconToolProcesses,
                minNumOfFilesPerUserForReconTool, maxNumOfFilesPerUserForReconTool));

//        I need to add directory group and categories
        ProcessCategoriesGenerator categoriesGenerator = new ProcessCategoriesGenerator(new String[]{"RECONNAISSANCE_TOOL"});
        dstProcessEntityGenerator.setProcessCategoriesGenerator(categoriesGenerator);

        ProcessDirectoryGroupsGenerator dstProcessDirectoryGroupsGenerator = new ProcessDirectoryGroupsGenerator(new String[]{"WINDOWS_SYSTEM32", "WINDOWS"});
        dstProcessEntityGenerator.setProcessDirectoryGroupsGenerator(dstProcessDirectoryGroupsGenerator);


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

    private List<MultiRangeTimeGenerator.ActivityRange> getNormalUserAbnormalActivityRange(){
        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(0,0), LocalTime.of(6,0), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(22,0), LocalTime.of(23,59), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
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

    private List<MultiRangeTimeGenerator.ActivityRange> getAdminUserAbnormalActivityRange(){
        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(22,0), LocalTime.of(23,59), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
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


    private List<FileEntity> getFileEnities(Pair[] dirAndFilePair){
        List<FileEntity> ret = new ArrayList<>();
        ProcessFileEntityGenerator fileGenerator = new ProcessFileEntityGenerator(dirAndFilePair);
        for(int i = 0; i < dirAndFilePair.length; i++){
            ret.add(fileGenerator.getNext());
        }
        return ret;
    }
}
