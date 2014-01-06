package fortscale.utils.geoip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

public class GeoIPService {
	private static final String ERR_DB_FILE_NOT_FOUND = "DB File not found!";
	private static final String ERR_WRITING_DB_FS = "Error writing GeoIP DB to FileSystem!";

	private static final String MM_DB_FILENAME = "GeoLite2-City.mmdb";

	private static DatabaseReader reader = null;

	/**
	 * Default constructor - uses "GeoLite2-City.mmdb"
	 * 
	 * @throws IOException
	 */
	public GeoIPService() throws IOException {
		this(getDbResourceFileName());
	}

	private static File getDbResourceFileName() throws IOException {
		URL resource = GeoIPService.class.getResource("/" + MM_DB_FILENAME);
		String dbFileName = resource.getFile();
		String dbFileNameAlt = resource.getPath() + MM_DB_FILENAME;

		File dbFile = new File(dbFileName);
		File dbFileAlt = new File(dbFileNameAlt);
		if (!dbFile.exists() && !dbFileAlt.exists()) {
			InputStream resourceAsStream = GeoIPService.class.getResourceAsStream("/GeoLite2-City.mmdb");
			try {
				writeISToFile(resourceAsStream, dbFileAlt);
			} catch (IOException e) {
				System.err.println(String.format("ERROR: %s Message: %s", ERR_WRITING_DB_FS, e.getMessage()));
				throw e;
			}
		}
		return dbFile;
	}

	private static void writeISToFile(InputStream is, File db) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(db);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
		} finally {
			if (out != null) {
				out.close();
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
	public GeoIPService(File dbFile) throws IOException {
		try {
			if (reader == null) {
				reader = new DatabaseReader.Builder(dbFile).build();
			}
		} catch (IOException e) {
			System.err.println(String.format("ERROR: %s Message: %s", ERR_DB_FILE_NOT_FOUND, e.getMessage()));
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
	public GeoIPInfo getGeoIPInfo(String IPAddress) throws UnknownHostException {
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
					geoInfo.setCityName(cityName);
				}

				String countryIsoCode = response.getCountry().getIsoCode();
				if (countryIsoCode != null && countryIsoCode.length() > 0) {
					geoInfo.setCountryISOCode(countryIsoCode);
				}

				String countryName = response.getCountry().getName();
				if (countryName != null && countryName.length() > 0) {
					geoInfo.setCountryName(countryName);
				}
			}
		} catch (Exception e) {
			// TODO: Add some error reporting
		}
		return geoInfo;
	}
}
