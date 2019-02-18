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
    private final int NUM_OF_ADMIN_USERS = 5000;
    private final int NUM_OF_SERVICE_ACCOUNT_USERS = 500;

    /** USERS **/
    IUserGenerator normalUserGenerator;
    List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange;
    List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange;
    IUserGenerator adminUserGenerator;
    List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange;
    List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange;
    IUserGenerator serviceAccountUserGenerator;
    List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange;

    /** MACHINES **/
    IMachineGenerator machineGenerator;

    /** Processes **/
    List<FileEntity> nonImportantProcesses;



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

        /** MACHINES **/
        machineGenerator = createMachineGenerator();

        /** Processes **/
        nonImportantProcesses = generateFileEntities();
    }



    @Test
    public void test() throws GeneratorException {
        stopWatch.start();

        Instant startInstant    = Instant.parse("2010-01-01T22:00:00.00Z");
        Instant endInstant      = Instant.parse("2010-01-02T01:05:00.00Z");

        NonImportantProcessEventGeneratorsBuilder nonImportantProcessEventGeneratorsBuilder =
                new NonImportantProcessEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange,
                        machineGenerator,
                        nonImportantProcesses
                );
        ReconToolGroupAEventGeneratorsBuilder reconToolGroupAEventGeneratorsBuilder =
                new ReconToolGroupAEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange,
                        machineGenerator,
                        nonImportantProcesses
                );

        // daily loop
        Instant startDailyInstant = startInstant;
        while(startDailyInstant.isBefore(endInstant)) {
            Instant endDailyInstant = startDailyInstant.truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS);
            if(endDailyInstant.isAfter(endInstant)){
                endDailyInstant = endInstant;
            }

            //get list of event generators
            List<AbstractEventGenerator<Event>> eventGenerators = new ArrayList<>();
            eventGenerators.addAll(nonImportantProcessEventGeneratorsBuilder.buildGenerators(startDailyInstant, endDailyInstant));
            eventGenerators.addAll(reconToolGroupAEventGeneratorsBuilder.buildGenerators(startDailyInstant, endDailyInstant));

            MultiEventGenerator multiEventGenerator = new MultiEventGenerator(eventGenerators);

            generateEvents(multiEventGenerator);

            stopWatch.split();
            System.out.println(stopWatch.toSplitString());

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
