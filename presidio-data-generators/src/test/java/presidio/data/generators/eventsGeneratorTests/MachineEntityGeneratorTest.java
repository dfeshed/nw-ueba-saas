package presidio.data.generators.eventsGeneratorTests;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import presidio.data.domain.MachineEntity;
import presidio.data.generators.common.FixedIPsGenerator;
import presidio.data.generators.machine.HostnameFromUsernameGenerator;
import presidio.data.generators.machine.SimpleMachineGenerator;
import presidio.data.generators.machine.StaticIPMachineGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cloudera on 6/6/17.
 */
public class MachineEntityGeneratorTest {

    /***
     * Custom SimpleMachineGenerator must get username as a parameter.
     */
    @Test
    public void MachineGeneratorCustomUserTest() {
        SimpleMachineGenerator generator = new SimpleMachineGenerator("mycustomuser");

        MachineEntity sm = generator.getNext();
        Assert.assertEquals(sm.getName(), "mycustomuser_src");
        Assert.assertEquals(sm.getIp_address(), "192.168.0.1"); //default IP
    }

    /***
     * Setting custom Hostname and IP generators that are part of MachineEntity generator
     * Creating events where same user connects to 3 different source machines
     */
    @Test
    public void SourceMachineGeneratorMultipleHostsTest() {
        HostnameFromUsernameGenerator HG = new HostnameFromUsernameGenerator("username1", 50);

        String[] ips = {"192.168.0.1", "192.168.0.2", "192.168.0.3"};
        FixedIPsGenerator SIPG = new FixedIPsGenerator(ips);

        SimpleMachineGenerator generator = new SimpleMachineGenerator(HG, SIPG);
        MachineEntity m;

        m = generator.getNext();
        Assert.assertEquals(m.getName(), "username1_s_src");
        Assert.assertEquals(m.getIp_address(), "192.168.0.1");

        m = generator.getNext();
        Assert.assertEquals(m.getName(), "username1_sx_src");
        Assert.assertEquals(m.getIp_address(), "192.168.0.2");

        m = generator.getNext();
        Assert.assertEquals(m.getName(), "username1_v_src");
        Assert.assertEquals(m.getIp_address(), "192.168.0.3");

        m = generator.getNext();
        Assert.assertEquals(m.getName(), "username1_n_src");
        Assert.assertEquals(m.getIp_address(), "192.168.0.1");

    }

    /***
     * Custom SimpleMachineGenerator must get username as a parameter.
     */
    @Test
    public void MachineGeneratorFromUsernameTest() {
        MachineEntity sm;

        StaticIPMachineGenerator generator = new StaticIPMachineGenerator("testuser", 4);

        sm = generator.getNext();
        Assert.assertEquals(sm.getName(), "testuser_s_");
        Assert.assertEquals(sm.getIp_address(), "192.168.0.1"); //default IP

        sm = generator.getNext();
        Assert.assertEquals(sm.getName(), "testuser_sx_");
        Assert.assertEquals(sm.getIp_address(), "192.168.0.2"); //default IP

        sm = generator.getNext();
        Assert.assertEquals(sm.getName(), "testuser_v_");
        Assert.assertEquals(sm.getIp_address(), "192.168.0.3"); //default IP

        sm = generator.getNext();
        Assert.assertEquals(sm.getName(), "testuser_n_");
        Assert.assertEquals(sm.getIp_address(), "192.168.0.4"); //default IP

        sm = generator.getNext();
        Assert.assertEquals(sm.getName(), "testuser_s_");
        Assert.assertEquals(sm.getIp_address(), "192.168.0.1"); //default IP

    }

    /***
     * Custom SimpleMachineGenerator must get username as a parameter.
     */
    @Test
    public void MachineGeneratorPairsTest() {
        List<Pair<String,String>> pairs = new ArrayList<>();

        pairs.add(Pair.of("host1", "203.104.0.35"));
        pairs.add(Pair.of("host2", "251.104.0.35"));

        StaticIPMachineGenerator generator = new StaticIPMachineGenerator(pairs);

        MachineEntity sm;
        sm = generator.getNext();
        Assert.assertEquals(sm.getName(), "host1");
        Assert.assertEquals(sm.getIp_address(), "203.104.0.35"); //default IP

        sm = generator.getNext();
        Assert.assertEquals(sm.getName(), "host2");
        Assert.assertEquals(sm.getIp_address(), "251.104.0.35"); //default IP

    }

    }
