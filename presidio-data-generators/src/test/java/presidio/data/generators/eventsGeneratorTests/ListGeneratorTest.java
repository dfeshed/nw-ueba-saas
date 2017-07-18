package presidio.data.generators.eventsGeneratorTests;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.common.FixedIPsGenerator;
import presidio.data.generators.event.dlpfile.SimpleMalwareScanResultGenerator;

public class ListGeneratorTest {
    @Test
    public void MalwareScanResultCyclicGeneratorTest() {
        SimpleMalwareScanResultGenerator MSRG = new SimpleMalwareScanResultGenerator();
        Assert.assertEquals(MSRG.getNext(), "Virus Total: 0 / 56 scans positive");
     }

    @Test
    public void MalwareScanResultCyclicGenerator1Test() {
        String[] evTypes = {"Virus Total: 10 / 56 scans positive", "Virus Total: 5 / 56 scans positive"};
        SimpleMalwareScanResultGenerator MSRG = new SimpleMalwareScanResultGenerator(evTypes);

        Assert.assertEquals(MSRG.getNext(), "Virus Total: 10 / 56 scans positive");
        Assert.assertEquals(MSRG.getNext(), "Virus Total: 5 / 56 scans positive");
    }

    @Test
    public void IPCyclicGenerator1Test() {
        String[] evTypes = {"192.168.0.1", "192.168.0.2", "192.168.0.3"};
        FixedIPsGenerator generator = new FixedIPsGenerator(evTypes);

        Assert.assertEquals(generator.getNext(), "192.168.0.1");
        Assert.assertEquals(generator.getNext(), "192.168.0.2");
        Assert.assertEquals(generator.getNext(), "192.168.0.3");
        Assert.assertEquals(generator.getNext(), "192.168.0.1"); // cyclic

   }
}
