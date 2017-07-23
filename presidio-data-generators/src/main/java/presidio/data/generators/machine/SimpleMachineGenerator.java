package presidio.data.generators.machine;

import presidio.data.generators.common.FixedIPsGenerator;
import presidio.data.domain.MachineEntity;

public class SimpleMachineGenerator implements IMachineGenerator {

    private HostnameFromUsernameGenerator HG;
    private FixedIPsGenerator SIPG;

    public SimpleMachineGenerator(String username)  {
        HG = new HostnameFromUsernameGenerator(username);
        SIPG = new FixedIPsGenerator();
    }

    public SimpleMachineGenerator(HostnameFromUsernameGenerator hg, FixedIPsGenerator sipg)  {
        HG = hg;
        SIPG = sipg;
    }

    public MachineEntity getNext(){
        return new MachineEntity(HG.getNext(), (String) SIPG.getNext());
    }
}
