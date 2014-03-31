package fortscale.geoip;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;

import fortscale.utils.logging.Logger;


public class IpToLocationGeoIPService implements GeoIPService{
	private static Logger logger = Logger.getLogger(IpToLocationGeoIPService.class);

	private static final String MOBILE_USAGE_TYPE = "MOB";

	private IP2Location loc = null;

	/**
	 * Constructor. Takes a path to to a GeoIP DB
	 * 
	 * @param dbFileName
	 *            - path to a GeoIP DB
	 * @throws IOException
	 */
	public IpToLocationGeoIPService(String fullPath){
			if (loc == null) {
				loc = new IP2Location();
				IP2Location.IPDatabasePath = fullPath;
				IP2Location.IPDatabasePathIPv6 = fullPath;
			}
	}
	
	/**
	 * Constructor. Takes a path to to a GeoIP DB
	 * 
	 * @param dbFileName
	 *            - path to a GeoIP DB
	 * @throws IOException
	 */
	public IpToLocationGeoIPService(Resource geoIpDbResource,Resource geoIpV6DbResource) throws IOException{
			if (loc == null) {
				loc = new IP2Location();
				IP2Location.IPDatabasePath = geoIpDbResource.getFile().getAbsolutePath();
				IP2Location.IPDatabasePathIPv6 = geoIpV6DbResource.getFile().getAbsolutePath();
			}
	}

	/**
	 * This method takes an IPv4 address and returns a GeoIPInfo object. Returns
	 * an empty GeoIPInfo object if an error occurs.
	 * 
	 * @param IPAddress
	 *            - takes an IPv4 address
	 * @return GeoIPInfo
	 * @throws UnknownHostException
	 */
	public GeoIPInfo getGeoIPInfo(String IPAddress) throws UnknownHostException {
		// Convert to IP address
		InetAddress byName = InetAddress.getByName(IPAddress);

		GeoIPInfo geoInfo = new GeoIPInfo(IPAddress);
		try {
			if (byName.isSiteLocalAddress()) {
				geoInfo.setCountryName(GeoIPInfo.RESERVED_RANGE);
			} else {
				IPResult rec = loc.IPQuery(IPAddress);
				if(StringUtils.isEmpty(rec.getCountryShort()) || rec.getCountryShort().equals("??") || rec.getCountryShort().equals("-")){
					return geoInfo;
				}

				// Populate out Geo IP info class
				if (!StringUtils.isEmpty(rec.getCity())) {
					geoInfo.setCityName(rec.getCity());
				}

				if (!StringUtils.isEmpty(rec.getCountryShort())) {
					geoInfo.setCountryISOCode(rec.getCountryShort());
				}

				if (!StringUtils.isEmpty(rec.getCountryLong())) {
					geoInfo.setCountryName(rec.getCountryLong());
				}
				if (!StringUtils.isEmpty(rec.getISP())) {
					geoInfo.setISP(rec.getISP());
				}
				if (!StringUtils.isEmpty(rec.getRegion())) {
					geoInfo.setRegionName(rec.getRegion());
				}
				if (!StringUtils.isEmpty(rec.getUsageType())) {
					if(rec.getUsageType().contains(MOBILE_USAGE_TYPE)){
						geoInfo.setUsageType(IpUsageTypeEnum.mob);
					} else{
						geoInfo.setUsageType(IpUsageTypeEnum.isp);
					}
				}
				
			}
		} catch (Exception e) {
			logger.warn(String.format("Failed to perform GeoIP lookup for IP %s", IPAddress), e);
		}
		return geoInfo;
	}

//	public static void main(String[] args) throws IOException {
//
//		IpToLocationGeoIPService gis = new IpToLocationGeoIPService();
//		GeoIPInfo info = gis.getGeoIPInfo("128.101.101.101");
//		System.out.println(info.getCountryName());
//	}
}