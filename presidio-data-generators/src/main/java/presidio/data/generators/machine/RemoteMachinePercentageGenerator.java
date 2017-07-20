package presidio.data.generators.machine;

import presidio.data.generators.common.FixedIPsGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.RandomStringGenerator;
import presidio.data.generators.common.precentage.BooleanPercentageGenerator;
import presidio.data.generators.domain.Machine;

public class RemoteMachinePercentageGenerator implements IMachineGenerator {

    private HostnameCustomListGenerator hostGenerator;
    private FixedIPsGenerator simpleIPGenerator;
    private BooleanPercentageGenerator remoteMachineGenerator;

    public RemoteMachinePercentageGenerator() throws GeneratorException {
        hostGenerator = new HostnameCustomListGenerator(new String[] {"host_1", "host_2", "host_3"});
        simpleIPGenerator = new FixedIPsGenerator();
        remoteMachineGenerator = new BooleanPercentageGenerator();
    }

    public RemoteMachinePercentageGenerator(HostnameCustomListGenerator hg, FixedIPsGenerator sipg, BooleanPercentageGenerator rmg)  {
        hostGenerator = hg;
        simpleIPGenerator = sipg;
        remoteMachineGenerator = rmg;
    }

    public Machine getNext(){
        return new Machine((String) hostGenerator.getNext(), (String) simpleIPGenerator.getNext(), remoteMachineGenerator.getNext());
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

    public BooleanPercentageGenerator getRemoteMachineGenerator() {
        return remoteMachineGenerator;
    }

    public void setRemoteMachineGenerator(BooleanPercentageGenerator remoteMachineGenerator) {
        this.remoteMachineGenerator = remoteMachineGenerator;
    }
}
