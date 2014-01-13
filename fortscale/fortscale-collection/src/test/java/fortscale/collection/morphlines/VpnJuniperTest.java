package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class VpnJuniperTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "src/main/non-packaged-resources/conf-files/readVPN_juniper.conf";
	private String[] vpnOutputFields = new String[] {"date_time","date_time_unixTime","username","source_ip","local_ip","status","message","country_name","host_name"};

	
	@Before
	public void setUp() throws Exception {
		morphlineTester.init(confFile, vpnOutputFields);
	}

//	@Test
	@Parameters
	public void test(String testCase, String inputLine, String expectedOutput) {
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}
	
	
	
	@SuppressWarnings("unused")
	private Object[] parametersForTest() {
        return	$(
        		$ (
        		"Successful VPN Authentication with Empty username",
				"Nov  7 14:41:28 11.155.45.2 Juniper: 2013-11-07 14:41:28 - ive - [208.91.35.4] - VPN Tunneling: Session started for user with IP 11.155.46.100, hostname victory.local",
				null
				),
        		$ (
        		"Regular Successful VPN Authentication",
				"Nov  7 14:41:28 11.155.45.2 Juniper: 2013-11-07 14:41:28 - ive - [208.91.35.4] sergio-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.100, hostname victory.local",
				"2013-11-07 14:41:28,1383864088,sergio-contractor,208.91.35.4,11.155.46.100,SUCCESS,Nov  7 14:41:28 11.155.45.2 Juniper: 2013-11-07 14:41:28 - ive - [208.91.35.4] sergio-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.100, hostname victory.local,United States,victory.local"
				),
				$ (
				"Regular Successful VPN Authentication",
				"Nov  7 14:37:04 11.155.45.2 Juniper: 2013-11-07 14:37:05 - ive - [72.193.146.27] bvaldes(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.113, hostname LAPTOP-20005507",
				"2013-11-07 14:37:05,1383863825,bvaldes,72.193.146.27,11.155.46.113,SUCCESS,Nov  7 14:37:04 11.155.45.2 Juniper: 2013-11-07 14:37:05 - ive - [72.193.146.27] bvaldes(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.113, hostname LAPTOP-20005507,United States,LAPTOP-20005507"
				),
				$ (
		        "Regular Successful VPN Authentication",
				"Nov  7 14:34:16 11.155.45.2 Juniper: 2013-11-07 14:34:16 - ive - [208.91.35.4] cbartra-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.177, hostname localhost-2.local",
				"2013-11-07 14:34:16,1383863656,cbartra-contractor,208.91.35.4,11.155.46.177,SUCCESS,Nov  7 14:34:16 11.155.45.2 Juniper: 2013-11-07 14:34:16 - ive - [208.91.35.4] cbartra-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.177, hostname localhost-2.local,United States,localhost-2.local"
				),
				$ (
		        "Regular Successful VPN Authentication",
				"Nov  7 14:33:06 11.155.45.2 Juniper: 2013-11-07 14:33:06 - ive - [72.193.210.215] avaliente(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.110, hostname LAPTOP-10021555",
				"2013-11-07 14:33:06,1383863586,avaliente,72.193.210.215,11.155.46.110,SUCCESS,Nov  7 14:33:06 11.155.45.2 Juniper: 2013-11-07 14:33:06 - ive - [72.193.210.215] avaliente(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.110, hostname LAPTOP-10021555,United States,LAPTOP-10021555"
				),
				$ (
		        "Regular Successful VPN Authentication",
				"Nov  7 14:30:33 11.155.45.2 Juniper: 2013-11-07 14:30:33 - ive - [208.91.35.4] sergio-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.100, hostname victory.local",
				"2013-11-07 14:30:33,1383863433,sergio-contractor,208.91.35.4,11.155.46.100,SUCCESS,Nov  7 14:30:33 11.155.45.2 Juniper: 2013-11-07 14:30:33 - ive - [208.91.35.4] sergio-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.100, hostname victory.local,United States,victory.local"
				),
				$ (
		        "Regular Successful VPN Authentication",
				"Nov  7 14:28:26 11.155.45.2 Juniper: 2013-11-07 14:28:26 - ive - [68.108.130.138] jhayun(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.109, hostname localhost",
				"2013-11-07 14:28:26,1383863306,jhayun,68.108.130.138,11.155.46.109,SUCCESS,Nov  7 14:28:26 11.155.45.2 Juniper: 2013-11-07 14:28:26 - ive - [68.108.130.138] jhayun(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.109, hostname localhost,United States,localhost"
				),
				$ (
		        "Regular Successful VPN Authentication",
				"Nov  7 14:23:22 11.155.45.2 Juniper: 2013-11-07 14:23:22 - ive - [208.91.35.4] cbartra-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.177, hostname localhost-2.local",
				"2013-11-07 14:23:22,1383863002,cbartra-contractor,208.91.35.4,11.155.46.177,SUCCESS,Nov  7 14:23:22 11.155.45.2 Juniper: 2013-11-07 14:23:22 - ive - [208.91.35.4] cbartra-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.177, hostname localhost-2.local,United States,localhost-2.local"
				),
				$ (
		        "Regular Successful VPN Authentication",
				"Nov  7 14:21:30 11.155.45.2 Juniper: 2013-11-07 14:21:30 - ive - [181.29.110.190] nadriano-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.126, hostname Heart",
				"2013-11-07 14:21:30,1383862890,nadriano-contractor,181.29.110.190,11.155.46.126,SUCCESS,Nov  7 14:21:30 11.155.45.2 Juniper: 2013-11-07 14:21:30 - ive - [181.29.110.190] nadriano-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.126, hostname Heart,Argentina,Heart"
				),
				$ (
		        "Regular Successful VPN Authentication",
				"Nov  7 14:20:50 11.155.45.2 Juniper: 2013-11-07 14:20:50 - ive - [68.108.130.138] jhayun(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.109, hostname localhost",
				"2013-11-07 14:20:50,1383862850,jhayun,68.108.130.138,11.155.46.109,SUCCESS,Nov  7 14:20:50 11.155.45.2 Juniper: 2013-11-07 14:20:50 - ive - [68.108.130.138] jhayun(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.109, hostname localhost,United States,localhost"
				),
				$ (
		        "Regular Successful VPN Authentication",
				"Nov  7 14:19:36 11.155.45.2 Juniper: 2013-11-07 14:19:36 - ive - [208.91.35.4] sergio-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.100, hostname victory.local",
				"2013-11-07 14:19:36,1383862776,sergio-contractor,208.91.35.4,11.155.46.100,SUCCESS,Nov  7 14:19:36 11.155.45.2 Juniper: 2013-11-07 14:19:36 - ive - [208.91.35.4] sergio-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.100, hostname victory.local,United States,victory.local"
				),
				$ (
		        "Regular Successful VPN Authentication",
				"Nov  7 14:17:13 11.155.45.2 Juniper: 2013-11-07 14:17:13 - ive - [68.96.250.211] apedrito-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.114, hostname apedrito-2013",
				"2013-11-07 14:17:13,1383862633,apedrito-contractor,68.96.250.211,11.155.46.114,SUCCESS,Nov  7 14:17:13 11.155.45.2 Juniper: 2013-11-07 14:17:13 - ive - [68.96.250.211] apedrito-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.114, hostname apedrito-2013,United States,apedrito-2013"
				),
				$ (
		        "Regular Successful VPN Authentication",
				"Nov  7 14:16:40 11.155.45.2 Juniper: 2013-11-07 14:16:40 - ive - [68.96.250.211] apedrito-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.114, hostname apedrito-2013",
				"2013-11-07 14:16:40,1383862600,apedrito-contractor,68.96.250.211,11.155.46.114,SUCCESS,Nov  7 14:16:40 11.155.45.2 Juniper: 2013-11-07 14:16:40 - ive - [68.96.250.211] apedrito-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.114, hostname apedrito-2013,United States,apedrito-2013"
				),
				$ (
		        "Regular Successful VPN Authentication",
				"Nov  7 14:16:06 11.155.45.2 Juniper: 2013-11-07 14:16:06 - ive - [68.96.250.211] apedrito-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.114, hostname apedrito-2013",
				"2013-11-07 14:16:06,1383862566,apedrito-contractor,68.96.250.211,11.155.46.114,SUCCESS,Nov  7 14:16:06 11.155.45.2 Juniper: 2013-11-07 14:16:06 - ive - [68.96.250.211] apedrito-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.114, hostname apedrito-2013,United States,apedrito-2013"
				),
				$ (
		        "Regular Failed VPN Authentication",
				"Nov  7 14:36:10 11.155.45.2 Juniper: 2013-11-07 14:36:10 - ive - [72.193.146.27] bvaldes(Users)[] - Login failed using auth server server.mypozza.com (ACE Server).  Reason: Failed",
				"2013-11-07 14:36:10,1383863770,bvaldes,72.193.146.27,,FAIL,Nov  7 14:36:10 11.155.45.2 Juniper: 2013-11-07 14:36:10 - ive - [72.193.146.27] bvaldes(Users)[] - Login failed using auth server server.mypozza.com (ACE Server).  Reason: Failed,United States,"
				),
				$ (
		        "Regular Failed VPN Authentication",
				"Nov  7 14:35:54 11.155.45.2 Juniper: 2013-11-07 14:35:54 - ive - [72.193.146.27] bvaldes(Users)[] - Login failed using auth server server.mypozza.com (ACE Server).  Reason: Failed",
				"2013-11-07 14:35:54,1383863754,bvaldes,72.193.146.27,,FAIL,Nov  7 14:35:54 11.155.45.2 Juniper: 2013-11-07 14:35:54 - ive - [72.193.146.27] bvaldes(Users)[] - Login failed using auth server server.mypozza.com (ACE Server).  Reason: Failed,United States,"
				)        		
        		);
    }

}