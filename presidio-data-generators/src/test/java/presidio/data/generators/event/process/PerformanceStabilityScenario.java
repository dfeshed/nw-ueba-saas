package presidio.data.generators.event.process;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Before;
import presidio.data.domain.FileEntity;
import presidio.data.domain.event.Event;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.MultiEventGenerator;
import presidio.data.generators.fileentity.RandomFileEntityGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.MachineGeneratorRouter;
import presidio.data.generators.machine.RandomMultiMachineEntityGenerator;
import presidio.data.generators.machine.UserDesktopGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.NumberedUserRandomUniformallyGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PerformanceStabilityScenario {

    private Instant startInstant;
    private Instant endInstant;
    private double probabilityMultiplier;
    private double usersMultiplier;

    private final int ACTIVE_TIME_INTERVAL = 1000000; // nanos

    private final int NUM_OF_NORMAL_USERS = 94500;
    private final int NUM_OF_ADMIN_USERS = 5000;
    private final int NUM_OF_SERVICE_ACCOUNT_USERS = 500;

    MultiEventGenerator curDailyEventGenerator;
    Instant curStartDailyInstant;
    Instant curEndDailyInstant;

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

    List<ProcessEventGeneratorsBuilder> processEventGeneratorsBuilders = new ArrayList<>();


    public PerformanceStabilityScenario(Instant startInstant, Instant endInstant, double probabilityMultiplier, double usersMultiplier) {
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.probabilityMultiplier = probabilityMultiplier;
        this.usersMultiplier = usersMultiplier;

        init();
    }

    public void init(){
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

        initBuilders();

        this.curStartDailyInstant = startInstant;
        try {
            nextDailyEventGenerator();
        } catch (GeneratorException e) {
            throw new RuntimeException(e);
        }
    }

    private void initBuilders(){
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
        processEventGeneratorsBuilders.add(nonImportantProcessEventGeneratorsBuilder);
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
        processEventGeneratorsBuilders.add(reconToolGroupAEventGeneratorsBuilder);

        ReconToolGroupBEventGeneratorsBuilder reconToolGroupBEventGeneratorsBuilder =
                new ReconToolGroupBEventGeneratorsBuilder(
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
        processEventGeneratorsBuilders.add(reconToolGroupBEventGeneratorsBuilder);

        ReconToolGroupCEventGeneratorsBuilder reconToolGroupCEventGeneratorsBuilder =
                new ReconToolGroupCEventGeneratorsBuilder(
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
        processEventGeneratorsBuilders.add(reconToolGroupCEventGeneratorsBuilder);

        ReconToolGroupDEventGeneratorsBuilder reconToolGroupDEventGeneratorsBuilder =
                new ReconToolGroupDEventGeneratorsBuilder(
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
        processEventGeneratorsBuilders.add(reconToolGroupDEventGeneratorsBuilder);

        ScriptingEngineExecutedEventGeneratorsBuilder scriptingEngineExecutedEventGeneratorsBuilder =
                new ScriptingEngineExecutedEventGeneratorsBuilder(
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
        processEventGeneratorsBuilders.add(scriptingEngineExecutedEventGeneratorsBuilder);

        ScriptingEngineExecuteAndOpenEventGeneratorsBuilder scriptingEngineExecuteAndOpenEventGeneratorsBuilder =
                new ScriptingEngineExecuteAndOpenEventGeneratorsBuilder(
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
        processEventGeneratorsBuilders.add(scriptingEngineExecuteAndOpenEventGeneratorsBuilder);

        LsassEventGeneratorsBuilder lsassEventGeneratorsBuilder =
                new LsassEventGeneratorsBuilder(
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
        processEventGeneratorsBuilders.add(lsassEventGeneratorsBuilder);

        WindowsProcessesEventGeneratorsBuilder windowsProcessesEventGeneratorsBuilder =
                new WindowsProcessesEventGeneratorsBuilder(
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
        processEventGeneratorsBuilders.add(windowsProcessesEventGeneratorsBuilder);
        processEventGeneratorsBuilders.forEach(processEventGeneratorsBuilder -> processEventGeneratorsBuilder.setProbabilityMultiplier(probabilityMultiplier));
        processEventGeneratorsBuilders.forEach(processEventGeneratorsBuilder -> processEventGeneratorsBuilder.setUsersMultiplier(usersMultiplier));
    }

    private IUserGenerator createNormalUserGenerator(){
        IUserGenerator userGenerator = new NumberedUserRandomUniformallyGenerator((int)(NUM_OF_NORMAL_USERS* usersMultiplier), 1, "normal_user_", "UID", false, false);
        return userGenerator;
    }

    private List<MultiRangeTimeGenerator.ActivityRange> getNormalUserActivityRange(){
        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(8,0), LocalTime.of(16,0), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        return rangesList;
    }

    private List<MultiRangeTimeGenerator.ActivityRange> getNormalUserAbnormalActivityRange(){
        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(0,0), LocalTime.of(8,0), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(16,0), LocalTime.of(23,59), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        return rangesList;
    }

    private IUserGenerator createAdminUserGenerator(){
        IUserGenerator userGenerator = new NumberedUserRandomUniformallyGenerator((int)(NUM_OF_ADMIN_USERS*usersMultiplier), 1, "admin_user_", "UID", false, false);
        return userGenerator;
    }

    private List<MultiRangeTimeGenerator.ActivityRange> getAdminUserActivityRange(){
        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(6,0), LocalTime.of(22,0), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        return rangesList;
    }

    private List<MultiRangeTimeGenerator.ActivityRange> getAdminUserAbnormalActivityRange(){
        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(0,0), LocalTime.of(6,0), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(22,0), LocalTime.of(23,59), Duration.ofNanos(ACTIVE_TIME_INTERVAL)));
        return rangesList;
    }

    private IUserGenerator createServiceAccountUserGenerator(){
        IUserGenerator userGenerator = new NumberedUserRandomUniformallyGenerator((int)(NUM_OF_SERVICE_ACCOUNT_USERS*usersMultiplier), 1, "sa_user_", "UID", false, false);
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

    private void nextDailyEventGenerator() throws GeneratorException {
        if(curStartDailyInstant == null){
            curDailyEventGenerator = null;
        } else {
            curEndDailyInstant = curStartDailyInstant.truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS);
            if (curEndDailyInstant.isAfter(endInstant)) {
                curEndDailyInstant = endInstant;
            }

            //get list of event generators
            List<AbstractEventGenerator<Event>> eventGenerators = new ArrayList<>();
            for (ProcessEventGeneratorsBuilder builder : processEventGeneratorsBuilders) {
                eventGenerators.addAll(builder.buildGenerators(curStartDailyInstant, curEndDailyInstant));
            }

            curDailyEventGenerator = new MultiEventGenerator(eventGenerators);
            if (curEndDailyInstant.equals(endInstant)) {
                curStartDailyInstant = null;
            } else {
                curStartDailyInstant = curEndDailyInstant;
            }
        }
    }

    public List<Event> generateEvents(int numOfEventsToGenerate) throws GeneratorException {

        List<Event> events = new ArrayList<>();
        // daily loop
        while(curDailyEventGenerator != null && events.size() < numOfEventsToGenerate){
            events.addAll(curDailyEventGenerator.generate(numOfEventsToGenerate - events.size()));
            if(events.size() < numOfEventsToGenerate){
                nextDailyEventGenerator();
            }
        }

        return events;
    }

}
