package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;

import fortscale.geoip.GeoIPInfo;

/**
 * @author Rois This class is a Morphline command which takes as input a column
 *         of IPv4 addresses and adds a column with the IP's country names
 */
public class GeolocationBuilder implements CommandBuilder {

	private static Logger logger = LoggerFactory.getLogger(GeolocationBuilder.class);
	
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("Geolocation");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new Geolocation(this, config, parent, child, context);
	}

	private static final class Geolocation extends AbstractCommand {

		private final String ipField;
		private final String countryFieldName;
		private final String regionFieldName;
		private final String cityFieldName;
		private final String ispFieldName;
		private final String usageTypeFieldName;
		private CollectionGoeIpService geoIpService = null;
		

		public Geolocation(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			// Get the field which holds the IP addresses
			this.ipField = getConfigs().getString(config, "ip_field");
			// This is the field name we'll use to hold the country name
			this.countryFieldName = getConfigs().getString(config, "country_field");
			// This is the field name we'll use to hold the country name
			this.regionFieldName = getConfigs().getString(config, "region_field");
			// This is the field name we'll use to hold the city name
			this.cityFieldName = getConfigs().getString(config, "city_field");
			// This is the field name we'll use to hold the isp name
			this.ispFieldName = getConfigs().getString(config, "isp_field");
			// This is the field name we'll use to hold the usage type name
			this.usageTypeFieldName = getConfigs().getString(config, "usage_type_field");
			
			this.geoIpService = new CollectionGoeIpService();

			validateArguments();
		}

		@Override
		protected boolean doProcess(Record inputRecord) {

			List<?> tmp = inputRecord.get(this.ipField);
			if (tmp != null && tmp.size() > 0) {
				// Get the IP Address
				String ipAddress = (String) tmp.get(0);
				// If the geo ip service is available
				try {
					GeoIPInfo geoIPInfo = geoIpService.getGeoIPInfo(ipAddress);

					// Write the ip info:  country, city, isp, usageType
					inputRecord.put(this.countryFieldName, geoIPInfo.getCountryName());
					inputRecord.put(this.regionFieldName, geoIPInfo.getRegionName());
					inputRecord.put(this.cityFieldName, geoIPInfo.getCityName());
					inputRecord.put(this.ispFieldName, geoIPInfo.getISP());
					inputRecord.put(this.usageTypeFieldName, geoIPInfo.getUsageType() != null ? geoIPInfo.getUsageType().getId() : "");
				} catch (Exception e) {
					logger.warn("error resolving geo2ip for {}, exception: {}", ipAddress, e.toString());
				}
			}
			return super.doProcess(inputRecord);

		}
	}
}