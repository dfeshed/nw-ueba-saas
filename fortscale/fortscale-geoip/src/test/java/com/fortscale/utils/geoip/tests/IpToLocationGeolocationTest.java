package com.fortscale.utils.geoip.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.maxmind.geoip2.exception.GeoIp2Exception;

import fortscale.geoip.GeoIPInfo;
import fortscale.geoip.IpToLocationGeoIPService;

public class IpToLocationGeolocationTest {

	private static IpToLocationGeoIPService geoLocService = null;

	@BeforeClass
	public static void ontTimeSetUp() throws Exception {
		geoLocService = new IpToLocationGeoIPService("src/test/resources/data/iptolocationdata.bin");
	}

	@Test
	public void sanityTest() throws UnknownHostException{
		String IP_Fortscale = "72.193.146.27";

		geoLocService.getGeoIPInfo(IP_Fortscale);
	}
	
	@Test
	public void sanityTestIPv6Success() throws UnknownHostException{
		String ip1 = "2001:4860:4860::8888";
		
		geoLocService.getGeoIPInfo(ip1);
	}
	
	@Test
	public void sanityTestIPNotfound() throws IOException, GeoIp2Exception {
		GeoIPInfo first = new GeoIPInfo("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
		GeoIPInfo second = new GeoIPInfo("2001:db8:85a3:0:0:8a2e:370:7334");

		GeoIPInfo res = geoLocService.getGeoIPInfo(first.getIp());
		assertEquals(first, res);

		res = geoLocService.getGeoIPInfo(second.getIp());
		assertEquals(second, res);
	}

	@Test
	public void testReservedIPRange() throws IOException, GeoIp2Exception {
		String IPAddress = "10.0.0.1";
		GeoIPInfo geoIPInfo = geoLocService.getGeoIPInfo(IPAddress);
		assertEquals(GeoIPInfo.RESERVED_RANGE, geoIPInfo.getCountryName());
		assertEquals("", geoIPInfo.getCountryISOCode().toLowerCase());
	}

	@Test
	public void testNoIP() throws UnknownHostException {
		String IPAddress = "";

		GeoIPInfo expected = new GeoIPInfo();
		GeoIPInfo result = geoLocService.getGeoIPInfo(IPAddress);
		assertEquals(expected, result);
	}

	@Test(expected = UnknownHostException.class)
	public void testInvalidIP() throws IOException, GeoIp2Exception {
		String IPAddress = "xxxx";
		geoLocService.getGeoIPInfo(IPAddress);
	}
}
