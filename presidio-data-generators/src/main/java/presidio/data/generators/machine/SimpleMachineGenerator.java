package presidio.data.generators.machine;

import presidio.data.generators.common.FixedIPsGenerator;
import presidio.data.generators.domain.Machine;

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

    public Machine getNext(){
        return new Machine(HG.getNext(), (String) SIPG.getNext());
    }
}
