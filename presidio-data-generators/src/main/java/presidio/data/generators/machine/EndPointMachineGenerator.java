package presidio.data.generators.machine;

import presidio.data.domain.MachineEntity;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.StringCyclicValuesGenerator;

public class EndPointMachineGenerator  extends SimpleMachineGenerator {
    private static final String OS_VERSION = "Windows Server 2016 Datacenter";

    private IStringGenerator machineIdGenerator;
    private IStringGenerator machineIP;
    private IStringGenerator osVersionGenerator;
    private IStringGenerator machineNameRegexClusterGenerator;
    private IStringGenerator machineDomainGenerator;
    private IStringGenerator machineDomainDN;
    private IStringGenerator machineDomainFQDN;
    private IStringGenerator ownerGenerator;

    public EndPointMachineGenerator()  {
        machineIdGenerator = super.getMachineIdGenerator();
        machineIP = super.getMachineIP();
        machineNameRegexClusterGenerator = super.getMachineNameRegexClusterGenerator();
        machineDomainGenerator = super.getMachineDomainGenerator();
        machineDomainDN = super.getMachineDomainDN();
        machineDomainFQDN = super.getMachineDomainFQDN();
        osVersionGenerator = super.getOsVersionGenerator();
        ownerGenerator = new StringCyclicValuesGenerator(new String[] {"owner1" , "owner2", "owner3"});
    }

    public IStringGenerator getOwnerGenerator() {
        return ownerGenerator;
    }

    public void setOwnerGenerator(IStringGenerator ownerGenerator) {
        this.ownerGenerator = ownerGenerator;
    }

    @Override
    public MachineEntity getNext(){
        return new MachineEntity(
                getMachineIdGenerator().getNext(),
                getMachineIP().getNext(),
                getMachineNameRegexClusterGenerator().getNext(),
                getMachineDomainGenerator().getNext(),
                getMachineDomainDN().getNext(),
                getMachineDomainFQDN().getNext(),
                getOsVersionGenerator().getNext(),
                getOwnerGenerator().getNext());

    }
}
