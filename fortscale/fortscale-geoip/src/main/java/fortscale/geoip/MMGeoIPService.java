package fortscale.geoip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

import fortscale.utils.logging.Logger;

public class MMGeoIPService implements GeoIPService {
	private static Logger logger = Logger.getLogger(MMGeoIPService.class);

	private static final String MM_DB_FILENAME = "GeoLite2-City.mmdb";

	private static DatabaseReader reader = null;

	/**
	 * Default constructor - uses "GeoLite2-City.mmdb"
	 * 
	 * @throws IOException
	 */
	public MMGeoIPService() throws IOException {
		InputStream stream = MMGeoIPService.class.getResourceAsStream(String.format("/%s",MM_DB_FILENAME));
		reader = new DatabaseReader.Builder(stream).build();
	}

	/**
	 * Constructor. Takes a path to to a GeoIP DB
	 * 
	 * @param dbFileName
	 *            - path to a GeoIP DB
	 * @throws IOException
	 */
	public MMGeoIPService(File dbFile) throws IOException {
		try {
			if (reader == null) {
				reader = new DatabaseReader.Builder(dbFile).build();
			}
		} catch (IOException e) {
			logger.error("Got an exception while try to read the db file.", e);
			throw e;
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
	@Override
	public IGeoIPInfo getGeoIPInfo(String IPAddress) throws UnknownHostException {
		// Convert to IP address
		InetAddress byName = InetAddress.getByName(IPAddress);

		GeoIPInfo geoInfo = new GeoIPInfo(IPAddress);
		try {
			if (byName.isSiteLocalAddress()) {
				geoInfo.setCountryName(GeoIPInfo.RESERVED_RANGE);
			} else {
				// Get the city
				CityResponse response = reader.city(byName);

				// Populate out Geo IP info class
				String cityName = response.getCity().getName();
				if (cityName != null && cityName.length() > 0) {
					geoInfo.setCityName(cityName.replace(",", ""));
				}

				String countryIsoCode = response.getCountry().getIsoCode();
				if (countryIsoCode != null && countryIsoCode.length() > 0) {
					geoInfo.setCountryISOCode(countryIsoCode);
				}

				String countryName = response.getCountry().getName();
				if (countryName != null && countryName.length() > 0) {
					geoInfo.setCountryName(countryName.replace(",", ""));
				}
			}
		} catch (Exception e) {
			logger.warn(String.format("Failed to perform GeoIP lookup for IP %s", IPAddress), e);
		}
		return geoInfo;
	}
}