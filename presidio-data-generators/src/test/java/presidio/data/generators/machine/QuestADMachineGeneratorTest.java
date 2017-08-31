package presidio.data.generators.machine;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.domain.MachineEntity;

/**
 * Created by YaronDL on 8/8/2017.
 */
public class QuestADMachineGeneratorTest {
    /***
     * Custom QuestADMachineGenerator must get username as a parameter.
     */
    @Test
    public void MachineGeneratorCustomUserTest() {
        QuestADMachineGenerator generator = new QuestADMachineGenerator();

        MachineEntity sm = generator.getNext();
        Assert.assertEquals(sm.getMachineId(), "host_1");
        Assert.assertEquals(sm.getMachineIp(), "192.168.0.1");
        Assert.assertEquals(sm.getMachineNameRegexCluster().length(), 10);
        Assert.assertEquals(sm.getMachineDomain().length(), 6);
    }

}
