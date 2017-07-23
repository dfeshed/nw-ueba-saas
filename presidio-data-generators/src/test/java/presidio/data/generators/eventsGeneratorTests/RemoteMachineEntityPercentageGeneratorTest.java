package presidio.data.generators.eventsGeneratorTests;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.common.GeneratorException;
import presidio.data.domain.MachineEntity;
import presidio.data.generators.machine.RemoteMachinePercentageGenerator;

public class RemoteMachineEntityPercentageGeneratorTest {
    @Test
    public void RemoteMachineTest() throws GeneratorException {

        RemoteMachinePercentageGenerator generator = new RemoteMachinePercentageGenerator();
        MachineEntity machineEntity = generator.getNext();
        Assert.assertTrue(machineEntity.isRemote());
    }
}
