package com.fortscale.utils.geoip.tests;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import fortscale.geoip.GeoIPInfo;
import fortscale.geoip.IGeoIPInfo;
import fortscale.geoip.MMGeoIPService;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MMGeolocationTest {

	private static final String US = "US";
	private static final String UNITED_STATES = "United States";
	private static MMGeoIPService geoLocService = null;



	/*
	@BeforeClass
	public static void ontTimeSetUp() throws Exception {
		geoLocService = new MMGeoIPService(new File("src/main/resources/GeoLite2-City.mmdb"));
	}*/

	@Ignore
	@Test
	public void sanityTest() throws IOException, GeoIp2Exception {
		String IP_Fortscale = "79.176.104.190";
		String IP_Google = "8.8.8.8";

		IGeoIPInfo geoIPFs = geoLocService.getGeoIPInfo(IP_Fortscale);
		assertEquals("israel", geoIPFs.getCountryName().toLowerCase());
		assertEquals("il", geoIPFs.getCountryISOCode().toLowerCase());

		IGeoIPInfo geoIPGoogle = geoLocService.getGeoIPInfo(IP_Google);
		assertEquals("united states", geoIPGoogle.getCountryName().toLowerCase());
		assertEquals("us", geoIPGoogle.getCountryISOCode().toLowerCase());
	}

	@Ignore
	@Test
	public void sanityTestIPv6Success() throws IOException, GeoIp2Exception {
		GeoIPInfo res1 = new GeoIPInfo("2001:4860:4860::8888");
		res1.setCountryName(UNITED_STATES);
		res1.setCountryISOCode(US);

		GeoIPInfo res2 = new GeoIPInfo("2607:f0d0:1002:51::4");
		res2.setCountryName(UNITED_STATES);
		res2.setCountryISOCode(US);

		GeoIPInfo res3 = new GeoIPInfo("2620:101:4003:743:78ca:d7b2:fb41:6271");
		res3.setCountryName(UNITED_STATES);
		res3.setCountryISOCode(US);

		GeoIPInfo res4 = new GeoIPInfo("2001:b48:10:3::11");
		res4.setCountryName("Sweden");
		res4.setCountryISOCode("SE");

		ArrayList<GeoIPInfo> expected = new ArrayList<GeoIPInfo>();
		expected.add(res1);
		expected.add(res2);
		expected.add(res3);
		expected.add(res4);

		for (GeoIPInfo ex : expected) {
			IGeoIPInfo res = geoLocService.getGeoIPInfo(ex.getIp());
			assertEquals(ex, res);
		}

	}

	@Ignore
	@Test
	public void sanityTestIPNotfound() throws IOException, GeoIp2Exception {
		GeoIPInfo first = new GeoIPInfo("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
		GeoIPInfo second = new GeoIPInfo("2001:db8:85a3:0:0:8a2e:370:7334");

		IGeoIPInfo res = geoLocService.getGeoIPInfo(first.getIp());
		assertEquals(first, res);

		res = geoLocService.getGeoIPInfo(second.getIp());
		assertEquals(second, res);
	}

	@Ignore
	@Test
	public void testReservedIPRange() throws IOException, GeoIp2Exception {
		String IPAddress = "10.0.0.1";
		IGeoIPInfo geoIPInfo = geoLocService.getGeoIPInfo(IPAddress);
		assertEquals(GeoIPInfo.RESERVED_RANGE, geoIPInfo.getCountryName());
		assertEquals("", geoIPInfo.getCountryISOCode().toLowerCase());
	}

	@Ignore
	@Test
	public void testNoIP() throws UnknownHostException {
		String IPAddress = "";

		GeoIPInfo expected = new GeoIPInfo();
		IGeoIPInfo result = geoLocService.getGeoIPInfo(IPAddress);
		assertEquals(expected, result);
	}

	@Ignore
	@Test(expected = UnknownHostException.class)
	public void testInvalidIP() throws IOException, GeoIp2Exception {
		String IPAddress = "xxxx";
		geoLocService.getGeoIPInfo(IPAddress);
	}

}
