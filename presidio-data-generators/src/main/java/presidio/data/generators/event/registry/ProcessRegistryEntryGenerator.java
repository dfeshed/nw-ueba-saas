package presidio.data.generators.event.registry;

import presidio.data.domain.ProcessEntity;
import presidio.data.domain.RegistryEntry;
import presidio.data.generators.registryentry.IRegistryEntryGenerator;

import java.util.*;

public class ProcessRegistryEntryGenerator implements IRegistryEntryGenerator {
    public static ProcessEntity curProcess = null;
    private List<String> registryGroups;
    private Map<String, List<String>> registryKeyGroupToRegistryKey;
    private Map<String, List<String>> registryKeyToValueNamesMap;
    private Map<String, List<String>> processToRegistryGroups;
    private int minRegistryGroupsToProcess;
    private int maxRegestryGroupsToProcess;
    Random random;

    public ProcessRegistryEntryGenerator(
            Map<String, List<String>> registryKeyGroupToRegistryKey,
            Map<String, List<String>> registryKeyToValueNamesMap,
            int minRegistryGroupsToProcess,
            int maxRegestryGroupsToProcess
    ){
        this.registryKeyGroupToRegistryKey = registryKeyGroupToRegistryKey;
        this.registryKeyToValueNamesMap = registryKeyToValueNamesMap;
        registryGroups = new ArrayList<>(registryKeyGroupToRegistryKey.keySet());
        this.maxRegestryGroupsToProcess = Math.min(maxRegestryGroupsToProcess, registryGroups.size());
        this.minRegistryGroupsToProcess = Math.min(minRegistryGroupsToProcess, maxRegestryGroupsToProcess);

        processToRegistryGroups = new HashMap<>();
        random = new Random();
    }


    @Override
    public RegistryEntry getNext() {
        String processFullPath = curProcess.getProcessDirectory() + curProcess.getProcessFileName();
        List<String> groups = processToRegistryGroups.computeIfAbsent(processFullPath, k -> generateRandomGroupList());
        int groupIndex = random.nextInt(groups.size());
        String group = groups.get(groupIndex);
        List<String> registryKeys = registryKeyGroupToRegistryKey.get(group);
        int keyIndex = random.nextInt(registryKeys.size());
        String key = registryKeys.get(keyIndex);
        List<String> valueNames = registryKeyToValueNamesMap.get(key);
        int valueIndex = random.nextInt(valueNames.size());
        String valueName = valueNames.get(valueIndex);
        return new RegistryEntry(key, group, valueName);
    }

    private List<String> generateRandomGroupList(){
        int numOfGroups = random.nextInt(maxRegestryGroupsToProcess - minRegistryGroupsToProcess) + minRegistryGroupsToProcess;
        if(numOfGroups == maxRegestryGroupsToProcess){
            return new ArrayList<>(registryGroups);
        }

        Set<String> groupSet = new HashSet<>();
        while(groupSet.size() < numOfGroups){
            int i = random.nextInt(registryGroups.size());
            groupSet.add(registryGroups.get(i));
        }

        return new ArrayList<>(groupSet);
    }
}
