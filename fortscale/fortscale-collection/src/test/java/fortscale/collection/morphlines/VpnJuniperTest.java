package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fortscale.utils.impala.ImpalaParser;

@RunWith(JUnitParamsRunner.class)
public class VpnJuniperTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readVPN_juniper.conf";

	
	@Before
	public void setUp() throws Exception {
		Properties properties = new Properties();
		InputStream is = getClass().getResourceAsStream( "/META-INF/fortscale-config.properties" );

		properties.load(is);
		String impalaTableFields = properties.getProperty("impala.data.vpn.table.fields");
		List<String> vpnOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		morphlineTester.init(confFile, vpnOutputFields);
	}

	@Test
	@Parameters
	public void test(String testCase, String inputLine, String expectedOutput) {
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}
	
	
	
	@SuppressWarnings("unused")
	private Object[] parametersForTest() {
        return	$(
//        		TODO: Verify whether this is a valid event, and how to parse the username
//				$ (
//				"Regular (Internet) Successful VPN Authentication",
//				"Nov 6 13:38:34 11.155.45.2 Juniper: 2012-11-06 13:38:34 - ive - [188.245.200.84] Root::thrghazas(Com2-Trusted-Linux-IKEv2)[Com2-Trusted-XXX-Linux-IKEv2] - VPN Tunneling: Session started for user with IP 10.84.255.160, hostname",
//				"2012-11-06 13:38:34,1352209114,Root::thrghazas,188.245.200.84,10.84.255.160,SUCCESS,Nov 6 13:38:34 11.155.45.2 Juniper: 2012-11-06 13:38:34 - ive - [188.245.200.84] Root::thrghazas(Com2-Trusted-Linux-IKEv2)[Com2-Trusted-XXX-Linux-IKEv2] - VPN Tunneling: Session started for user with IP 10.84.255.160, hostname,Iran,"
//				),
				$ (
		        "Successful Empty Username VPN Authentication (Should be dropped)",
		        "Nov 7 14:41:28 11.155.45.2 Juniper: 2013-11-07 14:41:28 - ive - [208.91.35.4] ser:gio-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.100, hostname victory.local",
				null
				),
				$ (
				"Regular (PT) Successful VPN Authentication",
				"info - [62.219.118.133] - baxishk(Company Users)[Group a,Group b] - 2011/06/10 09:57:18 - VPN Tunneling: Session started for user with IPv4 address 10.122.65.1, hostname SWAN",
				"2011-06-10 09:57:18,1307689038,baxishk,62.219.118.133,10.122.65.1,SUCCESS,Israel,SWAN"
				),
				$ (
        		"Regular (Poza) Successful VPN Authentication",
				"Nov 7 14:41:28 11.155.45.2 Juniper: 2013-11-07 14:41:28 - ive - [208.91.35.4] sergio-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.100, hostname victory.local",
				"2013-11-07 14:41:28,1383828088,sergio-contractor,208.91.35.4,11.155.46.100,SUCCESS,United States,victory.local"
				),
				$ (
				"Regular (Poza) Successful VPN Authentication",
				"Nov  7 14:37:04 11.155.45.2 Juniper: 2013-11-07 14:37:05 - ive - [72.193.146.27] bvaldes(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.113, hostname LAPTOP-20005507",
				"2013-11-07 14:37:05,1383827825,bvaldes,72.193.146.27,11.155.46.113,SUCCESS,United States,LAPTOP-20005507"
				),
				$ (
		        "Regular (Poza) Successful VPN Authentication",
				"Nov  7 14:34:16 11.155.45.2 Juniper: 2013-11-07 14:34:16 - ive - [208.91.35.4] cbartra-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.177, hostname localhost-2.local",
				"2013-11-07 14:34:16,1383827656,cbartra-contractor,208.91.35.4,11.155.46.177,SUCCESS,United States,localhost-2.local"
				),
				$ (
		        "Regular (Poza) Successful VPN Authentication",
				"Nov  7 14:33:06 11.155.45.2 Juniper: 2013-11-07 14:33:06 - ive - [72.193.210.215] avaliente(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.110, hostname LAPTOP-10021555",
				"2013-11-07 14:33:06,1383827586,avaliente,72.193.210.215,11.155.46.110,SUCCESS,United States,LAPTOP-10021555"
				),
				$ (
		        "Regular (Poza) Successful VPN Authentication",
				"Nov  7 14:30:33 11.155.45.2 Juniper: 2013-11-07 14:30:33 - ive - [208.91.35.4] sergio-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.100, hostname victory.local",
				"2013-11-07 14:30:33,1383827433,sergio-contractor,208.91.35.4,11.155.46.100,SUCCESS,United States,victory.local"
				),
				$ (
		        "Regular (Poza) Successful VPN Authentication",
				"Nov  7 14:28:26 11.155.45.2 Juniper: 2013-11-07 14:28:26 - ive - [68.108.130.138] jhayun(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.109, hostname localhost",
				"2013-11-07 14:28:26,1383827306,jhayun,68.108.130.138,11.155.46.109,SUCCESS,United States,localhost"
				),
				$ (
		        "Regular (Poza) Successful VPN Authentication",
				"Nov  7 14:23:22 11.155.45.2 Juniper: 2013-11-07 14:23:22 - ive - [208.91.35.4] cbartra-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.177, hostname localhost-2.local",
				"2013-11-07 14:23:22,1383827002,cbartra-contractor,208.91.35.4,11.155.46.177,SUCCESS,United States,localhost-2.local"
				),
				$ (
		        "Regular (Poza) Successful VPN Authentication",
				"Nov  7 14:21:30 11.155.45.2 Juniper: 2013-11-07 14:21:30 - ive - [181.29.110.190] nadriano-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.126, hostname Heart",
				"2013-11-07 14:21:30,1383826890,nadriano-contractor,181.29.110.190,11.155.46.126,SUCCESS,Argentina,Heart"
				),
				$ (
		        "Regular (Poza) Successful VPN Authentication",
				"Nov  7 14:20:50 11.155.45.2 Juniper: 2013-11-07 14:20:50 - ive - [68.108.130.138] jhayun(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.109, hostname localhost",
				"2013-11-07 14:20:50,1383826850,jhayun,68.108.130.138,11.155.46.109,SUCCESS,United States,localhost"
				),
				$ (
		        "Regular (Poza) Successful VPN Authentication",
				"Nov  7 14:19:36 11.155.45.2 Juniper: 2013-11-07 14:19:36 - ive - [208.91.35.4] sergio-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.100, hostname victory.local",
				"2013-11-07 14:19:36,1383826776,sergio-contractor,208.91.35.4,11.155.46.100,SUCCESS,United States,victory.local"
				),
				$ (
		        "Regular (Poza) Successful VPN Authentication",
				"Nov  7 14:17:13 11.155.45.2 Juniper: 2013-11-07 14:17:13 - ive - [68.96.250.211] apedrito-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.114, hostname apedrito-2013",
				"2013-11-07 14:17:13,1383826633,apedrito-contractor,68.96.250.211,11.155.46.114,SUCCESS,United States,apedrito-2013"
				),
				$ (
		        "Regular (Poza) Successful VPN Authentication",
				"Nov  7 14:16:40 11.155.45.2 Juniper: 2013-11-07 14:16:40 - ive - [68.96.250.211] apedrito-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.114, hostname apedrito-2013",
				"2013-11-07 14:16:40,1383826600,apedrito-contractor,68.96.250.211,11.155.46.114,SUCCESS,United States,apedrito-2013"
				),
				$ (
		        "Regular (Poza) Successful VPN Authentication",
				"Nov  7 14:16:06 11.155.45.2 Juniper: 2013-11-07 14:16:06 - ive - [68.96.250.211] apedrito-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.114, hostname apedrito-2013",
				"2013-11-07 14:16:06,1383826566,apedrito-contractor,68.96.250.211,11.155.46.114,SUCCESS,United States,apedrito-2013"
				),
				$ (
		        "Regular (Poza) Failed VPN Authentication",
				"Nov  7 14:36:10 11.155.45.2 Juniper: 2013-11-07 14:36:10 - ive - [72.193.146.27] bvaldes(Users)[] - Login failed using auth server server.mypozza.com (ACE Server).  Reason: Failed",
				null
//				TODO: When VPN failures are being processed, replace the expected null with the commented-out line
//				"2013-11-07 14:36:10,1383834970,bvaldes,72.193.146.27,,FAIL,Nov  7 14:36:10 11.155.45.2 Juniper: 2013-11-07 14:36:10 - ive - [72.193.146.27] bvaldes(Users)[] - Login failed using auth server server.mypozza.com (ACE Server).  Reason: Failed,United States,"
				),
				$ (
		        "Regular (Poza) Failed VPN Authentication",
				"Nov  7 14:35:54 11.155.45.2 Juniper: 2013-11-07 14:35:54 - ive - [72.193.146.27] bvaldes(Users)[] - Login failed using auth server server.mypozza.com (ACE Server).  Reason: Failed",
				null
//				TODO: When VPN failures are being processed, replace the expected null with the commented-out line 
//				"2013-11-07 14:35:54,1383834954,bvaldes,72.193.146.27,,FAIL,Nov  7 14:35:54 11.155.45.2 Juniper: 2013-11-07 14:35:54 - ive - [72.193.146.27] bvaldes(Users)[] - Login failed using auth server server.mypozza.com (ACE Server).  Reason: Failed,United States,"
				)
        		);
    }

}
