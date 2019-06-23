package presidio.data.generators.event.performance.scenario;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.performance.registry.GeneralRegistryUseCaseEventGeneratorsBuilder;
import presidio.data.generators.event.performance.scenario.EndPointPerformanceStabilityScenario;

import java.time.Instant;
import java.util.*;


public class RegistryPerformanceStabilityScenario extends EndPointPerformanceStabilityScenario {
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



    /** registry keys **/
    private Map<String, List<String>> registryKeyGroupToRegistryKey;
    private Map<String, List<String>> registryKeyToValuesMap;


    public RegistryPerformanceStabilityScenario(Instant startInstant, Instant endInstant, double probabilityMultiplier,
                                                double usersMultiplier) {
        super(startInstant, endInstant, probabilityMultiplier,usersMultiplier);
    }

    public void init() throws GeneratorException {
        /**Registry keys **/
        initRegistryKeys();

        super.init();
    }

    protected void initBuilders(){
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

        eventGeneratorsBuilders.add(generalRegistryUseCaseEventGeneratorsBuilder);
        eventGeneratorsBuilders.forEach(registryEventGeneratorsBuilder -> registryEventGeneratorsBuilder.setProbabilityMultiplier(probabilityMultiplier));
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



}
