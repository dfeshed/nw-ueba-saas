package fortscale.collection.morphlines.commands;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;

import com.fortscale.utils.geoip.GeoIPInfo;
import com.fortscale.utils.geoip.GeoIPService;
import com.typesafe.config.Config;

/**
 * @author Rois This class is a Morphline command which takes as input a column
 *         of IPv4 addresses and adds a column with the IP's country names
 */
public class GeolocationBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("Geolocation");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new Geolocation(config, parent, child, context);
	}

	private static final class Geolocation extends AbstractCommand {

		private final String recordField;
		private final String outputFieldName;
		private GeoIPService geoIpService;

		public Geolocation(Config config, Command parent, Command child, MorphlineContext context) {
			super(config, parent, child, context);
			// Get the field which holds the IP addresses
			this.recordField = getConfigs().getString(config, "input_record_name");
			// This is the field name we'll use to hold the country name
			this.outputFieldName = getConfigs().getString(config, "output_record_name");

			// Try to instantiate the GeoIP service
			try {
				String DB = getClass().getResource("/GeoLite2-City.mmdb").getFile();
				File dbFile = new File(DB);
				if (dbFile.exists()) {
					this.geoIpService = new GeoIPService(dbFile);
				}
			} catch (IOException e) {
				this.geoIpService = null;
			}
			validateArguments();
		}

		@Override
		protected boolean doProcess(Record inputRecord) {

			List tmp = inputRecord.get(this.recordField);
			if (tmp != null && tmp.size() > 0) {
				// Get the IP Address
				String ipAddress = (String) tmp.get(0);
				// If the geo ip service is available
				if (this.geoIpService != null) {
					try {
						GeoIPInfo geoIPInfo = this.geoIpService.getGeoIPInfo(ipAddress);
						String countryName = geoIPInfo.getCountryName();
						// Write the country name
						inputRecord.put(this.outputFieldName, countryName);
					} catch (IOException e) {
						// TODO: Insert logging here
					}
					// If an error occurs, we're not adding / changing anything
				}
			}
			return super.doProcess(inputRecord);

		}
	}
}