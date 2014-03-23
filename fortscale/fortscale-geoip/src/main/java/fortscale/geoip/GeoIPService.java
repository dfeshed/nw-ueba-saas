package fortscale.geoip;

import java.net.UnknownHostException;

public interface GeoIPService {

	/**
	 * This method takes an IPv4 address and returns a GeoIPInfo object. Returns
	 * an empty GeoIPInfo object if an error occurs.
	 * 
	 * @param IPAddress
	 *            - takes an IPv4 address
	 * @return GeoIPInfo
	 * @throws UnknownHostException
	 */
	public GeoIPInfo getGeoIPInfo(String IPAddress) throws UnknownHostException;
}
