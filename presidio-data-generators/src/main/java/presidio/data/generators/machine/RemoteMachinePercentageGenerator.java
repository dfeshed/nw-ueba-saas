package presidio.data.generators.machine;

import presidio.data.domain.MachineEntity;
import presidio.data.generators.common.AbstractCyclicValuesGenerator;
import presidio.data.generators.common.FixedIPsGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.precentage.BooleanPercentageGenerator;

public class RemoteMachinePercentageGenerator implements IMachineGenerator {

    private HostnameCustomListGenerator hostGenerator;
    private FixedIPsGenerator simpleIPGenerator;
    private AbstractCyclicValuesGenerator<Boolean> remoteMachineGenerator;

    public RemoteMachinePercentageGenerator() throws GeneratorException {
        hostGenerator = new HostnameCustomListGenerator(new String[] {"host_1", "host_2", "host_3"});
        simpleIPGenerator = new FixedIPsGenerator();
        remoteMachineGenerator = new BooleanPercentageGenerator(2);
    }

    public RemoteMachinePercentageGenerator(HostnameCustomListGenerator hg, FixedIPsGenerator sipg, BooleanPercentageGenerator rmg)  {
        hostGenerator = hg;
        simpleIPGenerator = sipg;
        remoteMachineGenerator = rmg;
    }

    public MachineEntity getNext(){
        return new MachineEntity((String) hostGenerator.getNext(), (String) simpleIPGenerator.getNext(), remoteMachineGenerator.getNext());
    }

    public HostnameCustomListGenerator getHostGenerator() {
        return hostGenerator;
    }

    public void setHostGenerator(HostnameCustomListGenerator hostGenerator) {
        this.hostGenerator = hostGenerator;
    }

    public FixedIPsGenerator getSimpleIPGenerator() {
        return simpleIPGenerator;
    }

    public void setSimpleIPGenerator(FixedIPsGenerator simpleIPGenerator) {
        this.simpleIPGenerator = simpleIPGenerator;
    }

    public AbstractCyclicValuesGenerator<Boolean> getRemoteMachineGenerator() {
        return remoteMachineGenerator;
    }

    public void setRemoteMachineGenerator(AbstractCyclicValuesGenerator<Boolean> remoteMachineGenerator) {
        this.remoteMachineGenerator = remoteMachineGenerator;
    }
}
