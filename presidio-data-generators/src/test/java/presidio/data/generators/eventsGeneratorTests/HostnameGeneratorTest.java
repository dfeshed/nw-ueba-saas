package presidio.data.generators.eventsGeneratorTests;

import org.junit.Assert;
import org.testng.annotations.Test;
import presidio.data.generators.machine.HostnameCustomListGenerator;
import presidio.data.generators.machine.HostnameFromUsernameGenerator;

/**
 * Created by cloudera on 6/1/17.
 */
public class HostnameGeneratorTest {
    @Test
    public void HostnameGeneratorTest() {
        // one hostname constructed from username with addition of "_src"
        HostnameFromUsernameGenerator generator = new HostnameFromUsernameGenerator("dlpuser");
        Assert.assertEquals("dlpuser_src", generator.getNext());
    }


    @Test
    public void HostnameGenerator1Test() {
        // Hostname constructed from username with addition of "_<A>_src"
        // where <A> stays for character from a to z.
        // Max number of hostnames - 702

        final String expectedHostname = "dlpuser_s_src";
        String actualHostname;

        HostnameFromUsernameGenerator generator = new HostnameFromUsernameGenerator("dlpuser", 703);
        actualHostname = generator.getNext();
        Assert.assertEquals(expectedHostname, actualHostname );
        for (int i = 2; i < 703; i++) { generator.getNext(); }
        Assert.assertEquals("dlpuser_ton_src", generator.getNext() );// last host in the list (max - 702)
        Assert.assertEquals(expectedHostname, generator.getNext() );  // starting the list over (cyclic)
    }

    @Test
    public void HostnameCustomListGenerator2Test() {
        // Cyclic list of hostname provided provided in the generators
        String[] hostsArray = new String[] {"host_1", "host_2", "host_3"};
        HostnameCustomListGenerator generator = new HostnameCustomListGenerator(hostsArray);
        Assert.assertEquals("host_1", generator.getNext() );
    }

}
