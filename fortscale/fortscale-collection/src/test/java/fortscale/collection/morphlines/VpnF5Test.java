package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;

import java.util.ArrayList;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;

@RunWith(JUnitParamsRunner.class)
public class VpnF5Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readVPN_F5.conf";

	
	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.vpn.table.morphline.fields");
		List<String> vpnOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		morphlineTester.init(new String[] {confFile}, vpnOutputFields);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
	}
	
	@Test
	@Parameters
	public void test(String testCase, Object[] lines, Object[] outputs) {
		
		List<String> events = new ArrayList<String>(lines.length);
		for (Object line : lines)
			events.add((String)line);
		
		List<String> expected = new ArrayList<String>(outputs.length);
		for (Object output : outputs)
			expected.add((String)output);
		
		morphlineTester.testMultipleLines(testCase, events , expected);
	}
	
	
	@SuppressWarnings("unused")
	private Object[] parametersForTest() {
        return	$(
    		$(
	    		"Regular (BS) Successful VPN Authentication",
	    		$(
	    			"Jan  2 19:08:28 server.bs.dom Jan  2 19:09:56 server notice tmm2[20226]: 01490500:5: 49dc878f: New session from client IP 75.26.245.200 (ST=Illinois/CC=US/C=NA) at VIP 172.10.10.10 Listener /DETAILS/details_https-va (Reputation=Unknown)",
	    			"Jan  2 19:08:35 server.bs.dom Jan  2 19:10:03 server info apd[18544]: 01490017:6: 49dc878f: AD agent: Auth (logon attempt:0): authenticate with 'chavier' successful"
				),
	    		$(
    				(String)null,
	    			"2014-01-02 19:08:35,1388707715,chavier,75.26.245.200,172.10.10.10,SUCCESS,United States,,Morton Grove,,,"
	    		)
    		),
    		
    		$(
    	    	"Regular (BS) Failed VPN Authentication",
    	    	$(
    	    		"Jan  2 19:06:14 server.bs.dom Jan  2 19:07:42 server notice tmm2[20226]: 01490500:5: 8a38fa12: New session from client IP 69.141.27.100 (ST=New Jersey/CC=US/C=NA) at VIP 172.10.11.12 Listener /DETAILS/details (Reputation=Unknown)",
    	    		"Jan  2 19:06:26 server.bs.dom Jan  2 19:07:54 server info apd[18544]: 01490017:6: 8a38fa12: AD agent: Auth (logon attempt:0): authenticate with 'bartra' failed"
    			),
    	    	$(
        			(String)null,
    	    		"2014-01-02 19:06:26,1388707586,bartra,69.141.27.100,172.10.11.12,FAIL,United States,,Jersey City,,,"
    	    	)
        	),
        	
    		$(
	    		"Only First Event of Regular (BS) Successful VPN Authentication",
	    		$(
	    			"Jan  2 19:08:28 server.bs.dom Jan  2 19:09:56 server notice tmm2[20226]: 01490500:5: 49dc878f: New session from client IP 75.26.245.200 (ST=Illinois/CC=US/C=NA) at VIP 172.10.10.10 Listener /DETAILS/details_https-va (Reputation=Unknown)"
				),
	    		$(
    				(String)null
	    		)
    		),
    		
    		$(
	    		"Only Second Event of Regular (BS) Successful VPN Authentication",
	    		$(
	    			"Jan  2 19:08:35 server.bs.dom Jan  2 19:10:03 server info apd[18544]: 01490017:6: 49dc878f: AD agent: Auth (logon attempt:0): authenticate with 'chavier' successful"
				),
	    		$(
    				(String)null
	    		)
    		),
    		
    		$(
	    		"Regular (BS) Successful VPN Authentication From Last Year",
	    		$(
	    			"Dec  2 19:08:28 server.bs.dom Jan  2 19:09:56 server notice tmm2[20226]: 01490500:5: 49dc878f: New session from client IP 75.26.245.200 (ST=Illinois/CC=US/C=NA) at VIP 172.10.10.10 Listener /DETAILS/details_https-va (Reputation=Unknown)",
	    			"Dec  2 19:08:35 server.bs.dom Jan  2 19:10:03 server info apd[18544]: 01490017:6: 49dc878f: AD agent: Auth (logon attempt:0): authenticate with 'chavier' successful"
				),
	    		$(
    				(String)null,
	    			"2013-12-02 19:08:35,1386029315,chavier,75.26.245.200,172.10.10.10,SUCCESS,United States,,Morton Grove,,,"
	    		)
    		)
		);
    }

}