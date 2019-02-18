package presidio.data.generators.event.process;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.FileEntity;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.process.PROCESS_OPERATION_TYPE;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.RandomMultiEventGenerator;
import presidio.data.generators.fileentity.UserFileEntityGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.processentity.ProcessCategoriesGenerator;
import presidio.data.generators.processentity.ProcessDirectoryGroupsGenerator;
import presidio.data.generators.processentity.ProcessEntityGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.LimitNumOfUsersGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


//Reconnaissance tools A: Destination processes: [arp, systeminfo, net, sc, netstat, nbtstat, ipconfig, wmic, hostname]
//NOTICE: It expect once a day with a day period in order to replace the userGenerator each day.
public class ReconToolGroupAEventGeneratorsBuilder extends ProcessEventGeneratorsBuilder{
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




    //For Reconnaissance tools A
    IUserGenerator ReconToolGroupAAllNormalUsers;
    IUserGenerator ReconToolGroupAAllAdminUsers;
    IUserGenerator ReconToolGroupAAllServiceAccountUsers;


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


    public ReconToolGroupAEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
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
        //For Reconnaissance tools A
        ReconToolGroupAAllNormalUsers = new LimitNumOfUsersGenerator(RECON_TOOL_GROUP_A_NUM_OF_NORMAL_USERS, normalUserGenerator);
        ReconToolGroupAAllAdminUsers = new LimitNumOfUsersGenerator(RECON_TOOL_GROUP_A_NUM_OF_ADMIN_USERS, adminUserGenerator);
        ReconToolGroupAAllServiceAccountUsers = new LimitNumOfUsersGenerator(RECON_TOOL_GROUP_A_NUM_OF_SERVICE_ACCOUNT_USERS, serviceAccountUserGenerator);

        /** GENERATORS: PROCESS **/

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



    public List<AbstractEventGenerator<Event>> buildGenerators(Instant startInstant, Instant endInstant) throws GeneratorException{
        List<AbstractEventGenerator<Event>> eventGenerators = new ArrayList<>();
        RandomMultiEventGenerator eventGenerator =
                createReconToolGroupAForNormalUsersRandomEventGenerator(
                        startInstant, endInstant
                );
        eventGenerators.add(eventGenerator);
        eventGenerator =
                createReconToolGroupAForAdminUsersEventGenerator(
                        startInstant, endInstant
                );
        eventGenerators.add(eventGenerator);
        eventGenerator =
                createReconToolGroupAForServiceAccountUsersEventGenerator(
                        startInstant, endInstant
                );
        eventGenerators.add(eventGenerator);

        //Abnormal events:
        eventGenerator =
                createReconToolGroupAForNormalUsersRandomAbnormalEventGenerator(
                        startInstant, endInstant
                );
        eventGenerators.add(eventGenerator);
        eventGenerator =
                createReconToolGroupAForAdminUsersAbnormalEventGenerator(
                        startInstant, endInstant
                );
        eventGenerators.add(eventGenerator);
        eventGenerator =
                createReconToolGroupAForServiceAccountUsersAbnormalEventGenerator(
                        startInstant, endInstant
                );
        eventGenerators.add(eventGenerator);

        return eventGenerators;
    }

    //==================================================================================
    // Creating Random Event Generators for all kind of users
    //==================================================================================



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
    // Creating Event Generators for all events for all type of users (normal, admin, service account)
    // In the random generator which is built per day the time generator is added
    // In addition when building the random generator the set of users is randomly reduced.
    //==================================================================================
    private List<FileEntity> getReconToolGroupAFileEnities(){
        return getFileEnities(RECON_TOOLS_GROUP_A);
    }



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

        ProcessCategoriesGenerator categoriesGenerator = new ProcessCategoriesGenerator(new String[]{"RECONNAISSANCE_TOOL"});
        dstProcessEntityGenerator.setProcessCategoriesGenerator(categoriesGenerator);

        ProcessDirectoryGroupsGenerator dstProcessDirectoryGroupsGenerator = new ProcessDirectoryGroupsGenerator(new String[]{"WINDOWS_SYSTEM32", "WINDOWS"});
        dstProcessEntityGenerator.setProcessDirectoryGroupsGenerator(dstProcessDirectoryGroupsGenerator);


        fillProcessEventsGeneratorWithDefaultGenerators(processNormalUsrEventsGenerator, srcProcessEntityGenerator,
                dstProcessEntityGenerator, operationTypeNames, generatorName);

        return processNormalUsrEventsGenerator;
    }
}
