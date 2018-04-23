package presidio.data.generators.machine;

import org.testng.Assert;
import org.testng.annotations.Test;
import presidio.data.generators.common.GeneratorException;

import java.util.Arrays;

public class RandomMultiMachineGeneratorTest {

    @Test
    public void multiMachineTest() throws GeneratorException {

        RandomMultiMachineEntityGenerator generator = new RandomMultiMachineEntityGenerator(
                Arrays.asList("D1", "D2"),
                10, "C",
                100, "M");

        String actualMachineId = generator.getNext().getMachineId();
        System.out.println(actualMachineId);
        Assert.assertTrue(actualMachineId.equals("M60_CnIekKmgNlhOmUzLXjgyd_D1")||actualMachineId.equals("M60_CnIekKmgNlhOmUzLXjgyd_D2"));

        for (int i = 0; i< 100; i++) {
            System.out.println(generator.getNext().getMachineId());
        }
    }
}
