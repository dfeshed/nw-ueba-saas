package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import fortscale.collection.morphlines.RecordSinkCommand;

import fortscale.services.ipresolving.ComputerLoginResolver;
import fortscale.services.ipresolving.DhcpResolver;
import fortscale.services.ipresolving.DnsResolver;
import org.glassfish.grizzly.utils.ArraySet;
import org.junit.Before;
import org.junit.Test;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import  fortscale.collection.morphlines.commands.IpToHostnameBuilder.IpToHostname;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IpToHostnameBuilderTest {

    private RecordSinkCommand sink = new RecordSinkCommand();
    private Config config;
    private DhcpResolver dhcpResolver;
    private DnsResolver dnsResolver;
    private ComputerLoginResolver computerLoginResolver;
    private ConfigObject configObject;

    @Before
    public void setUp() throws Exception {

        // mock morphline command parameters configuration
        config = mock(Config.class);
        configObject = mock(ConfigObject.class);
        dhcpResolver = mock(DhcpResolver.class);
        dnsResolver = mock(DnsResolver.class);
        computerLoginResolver = mock(ComputerLoginResolver.class);



        when(config.getString("ipAddress")).thenReturn("ipAddress");
        when(config.getString("resolvers")).thenReturn("resolvers");
        when(config.getBoolean("remove_last_dot")).thenReturn(true);
        when(config.getBoolean("short_name")).thenReturn(true);
        when(config.getString("timeStamp")).thenReturn("timeStamp");
        when(config.getString("outputRecordName")).thenReturn("hostName");

        Set<String> keySet = new HashSet<String>();
        keySet.add("ipAddress");
        keySet.add("remove_last_dot");
        keySet.add("short_name");
        keySet.add("timeStamp");
        keySet.add("outputRecordName");

        when(config.root()).thenReturn(configObject);
        when(configObject.keySet()).thenReturn(keySet);


        when(dhcpResolver.getHostname("192.168.0.55", 1l)).thenReturn("test-hostName");
        when(dnsResolver.getHostname("192.168.0.55")).thenReturn("test-hostName");
        when(computerLoginResolver.getHostname("192.168.0.55",1l)).thenReturn("test-hostName");

    }

    private Record getRecord(String ip,List<String> resolvers,long ts) {
        Record record = new Record();
        record.put("ipAddress", ip);
        record.put("resolvers",resolvers);
        record.put("timeStamp",ts);
        return record;
    }

    private IpToHostname getCommand() {

        IpToHostnameBuilder builder = new IpToHostnameBuilder();
        MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
        IpToHostname ipToHostname =  new IpToHostname(builder,config,sink,sink,morphlineContext);

        ipToHostname.setComputerLoginResolver(computerLoginResolver);
        ipToHostname.setDhcpResolver(dhcpResolver);
        ipToHostname.setDnsResolver(dnsResolver);
        return ipToHostname;

    }


    @Test
    public void testGettingIP_RetriveFromComputerLoginResolver() throws Exception {

        List<String> resolvers = new ArrayList<>();
        resolvers.add("logins");
        Record record = getRecord("192.168.0.55",resolvers,1l);

        IpToHostname command = getCommand();

        boolean result = command.doProcess(record);
        Record output = sink.popRecord();

        assertTrue(result);
        assertNotNull(output);
        assertEquals("test-hostName", output.getFirstValue("hostName"));

    }

    @Test
    public void testGettingIP_RetriveFromDns() throws Exception {

        List<String> resolvers = new ArrayList<>();
        resolvers.add("dns");
        Record record = getRecord("192.168.0.55",resolvers,1l);

        IpToHostname command = getCommand();

        boolean result = command.doProcess(record);
        Record output = sink.popRecord();

        assertTrue(result);
        assertNotNull(output);
        assertEquals("test-hostName", output.getFirstValue("hostName"));

    }

    @Test
    public void testGettingIP_RetriveFromDHCP() throws Exception {

        List<String> resolvers = new ArrayList<>();
        resolvers.add("dhcp");
        Record record = getRecord("192.168.0.55",resolvers,1l);

        IpToHostname command = getCommand();

        boolean result = command.doProcess(record);
        Record output = sink.popRecord();

        assertTrue(result);
        assertNotNull(output);
        assertEquals("test-hostName", output.getFirstValue("hostName"));

    }

    @Test
    public void testGettingIP_RetriveFromAllResolvers() throws Exception {

        List<String> resolvers = new ArrayList<>();

        Record record = getRecord("192.168.0.55",resolvers,1l);

        IpToHostname command = getCommand();

        boolean result = command.doProcess(record);
        Record output = sink.popRecord();

        assertTrue(result);
        assertNotNull(output);
        assertEquals("test-hostName", output.getFirstValue("hostName"));

    }

}