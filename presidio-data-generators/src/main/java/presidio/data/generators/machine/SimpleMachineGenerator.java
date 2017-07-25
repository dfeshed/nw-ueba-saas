package presidio.data.generators.machine;

import presidio.data.generators.common.FixedIPsGenerator;
import presidio.data.domain.MachineEntity;
import presidio.data.generators.common.IStringGenerator;

public class SimpleMachineGenerator implements IMachineGenerator {

    private IStringGenerator HG;
    private FixedIPsGenerator SIPG;

    public SimpleMachineGenerator(String username)  {
        HG = new HostnameFromUsernameGenerator(username);
        SIPG = new FixedIPsGenerator();
    }

    public SimpleMachineGenerator(IStringGenerator hg, FixedIPsGenerator sipg)  {
        HG = hg;
        SIPG = sipg;
    }

    public MachineEntity getNext(){
        return new MachineEntity((String) HG.getNext(), (String) SIPG.getNext());
    }
}
