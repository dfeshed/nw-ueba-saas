package presidio.data.generators.eventsGeneratorTests;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.domain.MachineEntity;
import presidio.data.generators.machine.SimpleMachineGenerator;

/**
 * Created by cloudera on 6/6/17.
 */
public class MachineEntityGeneratorTest {

    /***
     * Custom SimpleMachineGenerator must get username as a parameter.
     */
    @Test
    public void MachineGeneratorCustomUserTest() {
        SimpleMachineGenerator generator = new SimpleMachineGenerator();

        MachineEntity sm = generator.getNext();
        Assert.assertEquals(sm.getMachineId(), "host_1");
        Assert.assertEquals(sm.getMachineNameRegexCluster().length(), 10);
        Assert.assertEquals(sm.getMachineDomain().length(), 10);
    }
}
