package presidio.data.generators.machine;

import presidio.data.domain.MachineEntity;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.RandomStringGenerator;

public class SimpleMachineGenerator implements IMachineGenerator {

    private IStringGenerator machineIdGenerator;
    private IStringGenerator machineNameRegexClusterGenerator;
    private IStringGenerator machineDomainGenerator;

    public SimpleMachineGenerator()  {
        machineIdGenerator = new HostnameCustomListGenerator(new String[] {"host_1","host_2","host_3"});
        machineNameRegexClusterGenerator = new RandomStringGenerator(10);
        machineDomainGenerator = new RandomStringGenerator(10);
    }

    public IStringGenerator getMachineIdGenerator() {
        return machineIdGenerator;
    }

    public void setMachineIdGenerator(IStringGenerator machineIdGenerator) {
        this.machineIdGenerator = machineIdGenerator;
    }

    public IStringGenerator getMachineNameRegexClusterGenerator() {
        return machineNameRegexClusterGenerator;
    }

    public void setMachineNameRegexClusterGenerator(IStringGenerator machineNameRegexClusterGenerator) {
        this.machineNameRegexClusterGenerator = machineNameRegexClusterGenerator;
    }

    public IStringGenerator getMachineDomainGenerator() {
        return machineDomainGenerator;
    }

    public void setMachineDomainGenerator(IStringGenerator machineDomainGenerator) {
        this.machineDomainGenerator = machineDomainGenerator;
    }

    public MachineEntity getNext(){
        return new MachineEntity((String) getMachineIdGenerator().getNext(),
                (String) getMachineNameRegexClusterGenerator().getNext(),
                (String) getMachineDomainGenerator().getNext());
    }
}
