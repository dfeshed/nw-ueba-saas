package presidio.data.generators.machine;

import presidio.data.domain.MachineEntity;
import presidio.data.generators.common.FixedIPsGenerator;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.StringCyclicValuesGenerator;

public class QuestADMachineGenerator implements IMachineGenerator {

    private static final String OS_VERSION = "Windows Server 2016 Datacenter";

    private IStringGenerator machineIdGenerator;
    private IStringGenerator machineIPGenerator;
    private IStringGenerator machineNameRegexClusterGenerator;
    private IStringGenerator machineDomainGenerator;
    private IStringGenerator osVersionGenerator;
    private IStringGenerator machineDomainDN;
    private IStringGenerator origin;
    private IStringGenerator machineDomainFQDN;

    public QuestADMachineGenerator()  {
        machineIdGenerator = new HostnameCustomListGenerator(new String[] {"host_1","host_2","host_3"});
        machineIPGenerator = new FixedIPsGenerator();
        machineNameRegexClusterGenerator = new RandomStringGenerator(10);
        machineDomainGenerator = new StringCyclicValuesGenerator("catest");
        osVersionGenerator = new StringCyclicValuesGenerator("Windows Server 2016 Datacenter");
        machineDomainDN = new StringCyclicValuesGenerator("DC=catest,DC=quest,DC=azure,DC=ca");
        origin = new StringCyclicValuesGenerator("vmMember.catest.quest.azure.ca");
        machineDomainFQDN = new StringCyclicValuesGenerator("catest.quest.azure.ca");
        osVersionGenerator = new StringCyclicValuesGenerator(OS_VERSION);
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

    public IStringGenerator getOsVersionGenerator() {
        return osVersionGenerator;
    }

    public void setOsVersionGenerator(IStringGenerator osVersionGenerator) {
        this.osVersionGenerator = osVersionGenerator;
    }

    public IStringGenerator getMachineDomainDN() {
        return machineDomainDN;
    }

    public void setMachineDomainDN(IStringGenerator machineDomainDN) {
        this.machineDomainDN = machineDomainDN;
    }

    public IStringGenerator getOrigin() {
        return origin;
    }

    public void setOrigin(IStringGenerator origin) {
        this.origin = origin;
    }

    public IStringGenerator getMachineIPGenerator() {
        return machineIPGenerator;
    }

    public void setMachineIPGenerator(IStringGenerator machineIPGenerator) {
        this.machineIPGenerator = machineIPGenerator;
    }

    public IStringGenerator getMachineDomainFQDN() { return machineDomainFQDN; }

    public void setMachineDomainFQDN(IStringGenerator machineDomainFQDN) { this.machineDomainFQDN = machineDomainFQDN; }

    public MachineEntity getNext(){
        return new MachineEntity(getMachineIdGenerator().getNext(),
                getMachineIPGenerator().getNext(),
                getMachineNameRegexClusterGenerator().getNext(),
                getMachineDomainGenerator().getNext(),
                getMachineDomainDN().getNext(),
                getMachineDomainFQDN().getNext(),
                getOsVersionGenerator().getNext());
    }
}
