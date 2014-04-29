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
	public IpToLocationGeoIPService(String fullDbPath, String fullLicenseKeyPath){
			if (loc == null) {
				loc = new IP2Location();
				IP2Location.IPDatabasePath = fullDbPath;
				IP2Location.IPDatabasePathIPv6 = fullDbPath;
				if(StringUtils.isNotEmpty(fullLicenseKeyPath)){
					IP2Location.IPLicensePath = fullLicenseKeyPath;
				}
			}
	}
	
	/**
	 * Constructor. Takes a path to to a GeoIP DB
	 * 
	 * @param dbFileName
	 *            - path to a GeoIP DB
	 * @throws IOException
	 */
	public IpToLocationGeoIPService(Resource geoIpDbResource,Resource geoIpV6DbResource, Resource fullLicenseKeyResource) throws IOException{
			if (loc == null) {
				loc = new IP2Location();
				if(geoIpDbResource != null && geoIpDbResource.exists()){
					IP2Location.IPDatabasePath = geoIpDbResource.getFile().getAbsolutePath();
				} else{
					if(geoIpDbResource == null){
						logger.error("ipv4 db full path was not recieved.");
					} else{
						logger.error("ipv4 db full path {} does not exist", geoIpDbResource.getFile().getAbsolutePath());
					}
				}
				
				if(geoIpV6DbResource != null && geoIpV6DbResource.exists()){
					IP2Location.IPDatabasePathIPv6 = geoIpV6DbResource.getFile().getAbsolutePath();
				} else{
					if(geoIpV6DbResource == null){
						logger.error("ipv6 db full path was not recieved.");
					} else{
						logger.error("ipv6 db full path {} does not exist", geoIpV6DbResource.getFile().getAbsolutePath());
					}
				}
				
				if(fullLicenseKeyResource != null && fullLicenseKeyResource.exists()){
					IP2Location.IPLicensePath = fullLicenseKeyResource.getFile().getAbsolutePath();
				}
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
					geoInfo.setCityName(rec.getCity().replace(",", ""));
				}

				if (!StringUtils.isEmpty(rec.getCountryShort())) {
					geoInfo.setCountryISOCode(rec.getCountryShort());
				}

				if (!StringUtils.isEmpty(rec.getCountryLong())) {
					geoInfo.setCountryName(rec.getCountryLong().replace(",", ""));
				}
				if (!StringUtils.isEmpty(rec.getISP())) {
					geoInfo.setISP(rec.getDomain().replace(",", ""));
				}
				if (!StringUtils.isEmpty(rec.getRegion())) {
					geoInfo.setRegionName(rec.getRegion().replace(",", ""));
				}
				if (!StringUtils.isEmpty(rec.getUsageType())) {
					if(rec.getUsageType().contains(MOBILE_USAGE_TYPE)){
						geoInfo.setUsageType(IpUsageTypeEnum.mob);
					} else{
						geoInfo.setUsageType(IpUsageTypeEnum.isp);
					}
				}
				geoInfo.setLatitude((double) rec.getLatitude());
				geoInfo.setLongitude((double) rec.getLongitude());
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