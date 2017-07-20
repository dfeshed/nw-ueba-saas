package presidio.data.generators.eventsGeneratorTests;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.domain.Machine;
import presidio.data.generators.machine.RemoteMachinePercentageGenerator;

public class RemoteMachinePercentageGeneratorTest {
    @Test
    public void RemoteMachineTest() throws GeneratorException {

        RemoteMachinePercentageGenerator generator = new RemoteMachinePercentageGenerator();
        Machine machine = generator.getNext();
        Assert.assertTrue(machine.isRemote());
    }
}
