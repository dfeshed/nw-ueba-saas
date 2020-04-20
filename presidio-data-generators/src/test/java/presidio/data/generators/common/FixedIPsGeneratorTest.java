package presidio.data.generators.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by YaronDL on 8/8/2017.
 */
public class FixedIPsGeneratorTest {
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
