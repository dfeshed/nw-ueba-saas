package presidio.data.generators.machine;

import presidio.data.domain.MachineEntity;
import presidio.data.generators.common.CustomStringGenerator;
import presidio.data.generators.common.IStringGenerator;

public class FixedMachineGenerator implements IMachineGenerator {
    private MachineEntity machineEntity;


    public FixedMachineGenerator(String machineName)  {
        String machineNameRegexCluster = machineName.replaceAll("\\d","");
        machineEntity = new MachineEntity(machineName, machineNameRegexCluster, "DC=catest,DC=quest,DC=azure,DC=ca");
    }

    public MachineEntity getNext(){
        return machineEntity;
    }
}
