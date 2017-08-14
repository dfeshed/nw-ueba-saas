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
    private IStringGenerator osVersionGenerator;
    private IStringGenerator machineDomainDN;
    private IStringGenerator origin;

    public SimpleMachineGenerator()  {
        machineIdGenerator = new HostnameCustomListGenerator(new String[] {"host_1","host_2","host_3"});
        machineIP = new FixedIPsGenerator();
        machineNameRegexClusterGenerator = new RandomStringGenerator(10);
        machineDomainGenerator = new RandomStringGenerator(10);
        osVersionGenerator = new StringCyclicValuesGenerator(new String [] {"Windows Server 2016 Datacenter"});
        machineDomainDN = new StringCyclicValuesGenerator(new String[] {"DC=catest,DC=quest,DC=azure,DC=ca"});
        origin = new StringCyclicValuesGenerator(new String[] {"vmMember.catest.quest.azure.ca"});
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

    public IStringGenerator getMachineIP() {
        return machineIP;
    }

    public void setMachineIP(IStringGenerator machineIP) {
        this.machineIP = machineIP;
    }

    public MachineEntity getNext(){
        return new MachineEntity(getMachineIdGenerator().getNext(),
                getMachineIP().getNext(),
                getMachineNameRegexClusterGenerator().getNext(),
                getMachineDomainGenerator().getNext(),
                getMachineDomainDN().getNext(),
                getOsVersionGenerator().getNext(),
                getOrigin().getNext());
    }
}
