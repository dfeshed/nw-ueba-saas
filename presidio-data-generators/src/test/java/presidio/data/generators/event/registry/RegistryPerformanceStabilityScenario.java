package presidio.data.generators.event.registry;

import presidio.data.domain.FileEntity;
import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.MultiEventGenerator;
import presidio.data.generators.event.process.NonImportantProcessEventGeneratorsBuilder;
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


public class RegistryPerformanceStabilityScenario {
    private static final String[] REGISTRY_KEY_GROUPS = new String[] {
            "RUN_KEY",
            "SERVICES_IMAGE_PATH",
            "FIREWALL_POLICY",
            "LUA_SETTING_REGISTRY_EDITOR_SETTING",
            "SECURITY_CENTER_CONFIGURATION",
            "TASK_MANAGER_SETTING",
            "WINDOWS_SYSTEM_POLICY",
            "INTERNET_ZONE_SETTINGS",
            "BAD_CERTIFICATE_WARNING_SETTING"};

    private Instant startInstant;
    private Instant endInstant;
    private double probabilityMultiplier;

    private final int ACTIVE_TIME_INTERVAL = 1000000; // nanos

    private final int NUM_OF_NORMAL_USERS = 94500;
    private final int NUM_OF_ADMIN_USERS = 5000;
    private final int NUM_OF_SERVICE_ACCOUNT_USERS = 500;

    private MultiEventGenerator curDailyEventGenerator;
    private Instant curStartDailyInstant;
    private Instant curEndDailyInstant;

    /** USERS **/
    private IUserGenerator normalUserGenerator;
    private List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange;
    private List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange;
    private IUserGenerator adminUserGenerator;
    private List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange;
    private List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange;
    private IUserGenerator serviceAccountUserGenerator;
    private List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange;

    /** MACHINES **/
    private IMachineGenerator machineGenerator;

    /** Processes **/
    private List<FileEntity> nonImportantProcesses;

    /** registry keys **/
    private Map<String, List<String>> registryKeyGroupToRegistryKey;
    private Map<String, List<String>> registryKeyToValuesMap;



    List<RegistryEventGeneratorsBuilder> registryEventGeneratorsBuilders = new ArrayList<>();


    public RegistryPerformanceStabilityScenario(Instant startInstant, Instant endInstant, double probabilityMultiplier) {
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.probabilityMultiplier = probabilityMultiplier;

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

        /**Registry keys **/
        initRegistryKeys();

        initBuilders();

        this.curStartDailyInstant = startInstant;
        try {
            nextDailyEventGenerator();
        } catch (GeneratorException e) {
            throw new RuntimeException(e);
        }
    }

    private void initBuilders(){
        GeneralRegistryUseCaseEventGeneratorsBuilder generalRegistryUseCaseEventGeneratorsBuilder =
                new GeneralRegistryUseCaseEventGeneratorsBuilder(
                        normalUserGenerator,
                        normalUserActivityRange,
                        normalUserAbnormalActivityRange,
                        adminUserGenerator,
                        adminUserActivityRange,
                        adminUserAbnormalActivityRange,
                        serviceAccountUserGenerator,
                        serviceAcountUserActivityRange,
                        machineGenerator,
                        nonImportantProcesses,
                        registryKeyGroupToRegistryKey,
                        registryKeyToValuesMap
                );

        registryEventGeneratorsBuilders.add(generalRegistryUseCaseEventGeneratorsBuilder);
        registryEventGeneratorsBuilders.forEach(registryEventGeneratorsBuilder -> registryEventGeneratorsBuilder.setProbabilityMultiplier(probabilityMultiplier));
    }

    private IUserGenerator createNormalUserGenerator(){
        IUserGenerator userGenerator = new NumberedUserRandomUniformallyGenerator(NUM_OF_NORMAL_USERS, 1, "normal_user_", "UID", false, false);
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
        IUserGenerator userGenerator = new NumberedUserRandomUniformallyGenerator(NUM_OF_ADMIN_USERS, 1, "admin_user_", "UID", false, false);
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



    private void initRegistryKeys(){
        registryKeyGroupToRegistryKey = new HashMap<>();
        registryKeyToValuesMap = new HashMap<>();
        Random random = new Random();
        for(int i = 0; i <= 100000; i++){
            int groupIndex = random.nextInt(REGISTRY_KEY_GROUPS.length);
            String group = REGISTRY_KEY_GROUPS[groupIndex];
            List<String> registryKeys = registryKeyGroupToRegistryKey.computeIfAbsent(group, k -> new ArrayList<>());
            String registryKeyName = "registry_key_" + i;
            registryKeys.add(registryKeyName);
            List<String> registryValues = createRegistryKeyValues(random, i);
            registryKeyToValuesMap.put(registryKeyName, registryValues);
        }
    }

    private List<String> createRegistryKeyValues(Random random, int prefixNumber){
        int numOfValues = random.nextInt(100) + 1;
        List<String> ret = new ArrayList<>();
        for(int i=0; i<numOfValues; i++){
            ret.add("key_value_" + prefixNumber + "_" + i);
        }
        return ret;
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
            for (RegistryEventGeneratorsBuilder builder : registryEventGeneratorsBuilders) {
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
