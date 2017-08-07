package presidio.data.generators.eventsGeneratorTests;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import presidio.data.domain.MachineEntity;
import presidio.data.generators.machine.SimpleMachineGenerator;
import presidio.data.generators.machine.StaticIPMachineGenerator;

import java.util.ArrayList;
import java.util.List;

public class MachineEntityGeneratorTest {

    /***
     * Custom SimpleMachineGenerator must get username as a parameter.
     */
    @Test
    public void MachineGeneratorCustomUserTest() {
        SimpleMachineGenerator generator = new SimpleMachineGenerator();

        MachineEntity sm = generator.getNext();
        Assert.assertEquals(sm.getMachineId(), "host_1");
        Assert.assertEquals(sm.getMachineIp(), "0.0.0.1");
        Assert.assertEquals(sm.getMachineNameRegexCluster().length(), 10);
        Assert.assertEquals(sm.getMachineDomain().length(), 10);
    }

    /***
     * Custom StaticIpMachineGenerator - builds machine entities from provided name/ip pairs.
     */
    @Test
    public void MachineGeneratorPairsTest() {
        List<Pair<String,String>> pairs = new ArrayList<>();

        pairs.add(Pair.of("host1", "203.104.0.35"));
        pairs.add(Pair.of("host2", "251.104.0.35"));

        StaticIPMachineGenerator generator = new StaticIPMachineGenerator(pairs);

        MachineEntity sm;
        sm = generator.getNext();
        Assert.assertEquals(sm.getMachineId(), "host1");
        Assert.assertEquals(sm.getMachineIp(), "203.104.0.35"); //default IP

        sm = generator.getNext();
        Assert.assertEquals(sm.getMachineId(), "host2");
        Assert.assertEquals(sm.getMachineIp(), "251.104.0.35"); //default IP
    }

    /***
     * Setting custom Hostname and IP generators that are part of MachineEntity generator
     * Creating events where same user connects to 3 different source machines
     */
    @Test
    public void SourceMachineGeneratorMultipleHostsTest() {


        StaticIPMachineGenerator generator = new StaticIPMachineGenerator("username1", 3);
        MachineEntity m;

        m = generator.getNext();
        Assert.assertEquals(m.getMachineId(), "username1_s_");
        Assert.assertEquals(m.getMachineIp(), "192.168.0.1");

        m = generator.getNext();
        Assert.assertEquals(m.getMachineId(), "username1_sx_");
        Assert.assertEquals(m.getMachineIp(), "192.168.0.2");

        m = generator.getNext();
        Assert.assertEquals(m.getMachineId(), "username1_v_");
        Assert.assertEquals(m.getMachineIp(), "192.168.0.3");

        m = generator.getNext();
        Assert.assertEquals(m.getMachineId(), "username1_s_");
        Assert.assertEquals(m.getMachineIp(), "192.168.0.1");

    }

}
