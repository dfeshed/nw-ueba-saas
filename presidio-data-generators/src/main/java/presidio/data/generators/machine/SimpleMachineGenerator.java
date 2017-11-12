package presidio.data.generators.machine;

import presidio.data.domain.MachineEntity;
import presidio.data.generators.common.FixedIPsGenerator;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.StringCyclicValuesGenerator;

public class SimpleMachineGenerator implements IMachineGenerator {

    private IStringGenerator machineIdGenerator;
    private IStringGenerator machineIP;
    private IStringGenerator machineNameRegexClusterGenerator;
    private IStringGenerator machineDomainGenerator;
    private IStringGenerator machineDomainDN;
    private IStringGenerator machineDomainFQDN;

    public SimpleMachineGenerator()  {
        machineIdGenerator = new HostnameCustomListGenerator(new String[] {"host_1","host_2","host_3"});
        machineIP = new FixedIPsGenerator();
        machineNameRegexClusterGenerator = new RandomStringGenerator(10);
        machineDomainGenerator = new RandomStringGenerator(10);
        machineDomainDN = new StringCyclicValuesGenerator("DC=catest,DC=quest,DC=azure,DC=ca");
        machineDomainFQDN = new StringCyclicValuesGenerator("catest.quest.azure.ca");
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

    public IStringGenerator getMachineDomainDN() {
        return machineDomainDN;
    }

    public void setMachineDomainDN(IStringGenerator machineDomainDN) {
        this.machineDomainDN = machineDomainDN;
    }

    public IStringGenerator getMachineIP() {
        return machineIP;
    }

    public void setMachineIP(IStringGenerator machineIP) {
        this.machineIP = machineIP;
    }

    public IStringGenerator getMachineDomainFQDN() { return machineDomainFQDN; }

    public void setMachineDomainFQDN(IStringGenerator machineDomainFQDN) { this.machineDomainFQDN = machineDomainFQDN; }

    public MachineEntity getNext(){
        return new MachineEntity(
                getMachineIdGenerator().getNext(),
                getMachineIP().getNext(),
                getMachineNameRegexClusterGenerator().getNext(),
                getMachineDomainGenerator().getNext(),
                getMachineDomainDN().getNext(),
                getMachineDomainFQDN().getNext());
    }
}
