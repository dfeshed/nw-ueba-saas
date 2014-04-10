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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.typesafe.config.Config;

import fortscale.geoip.GeoIPInfo;
import fortscale.geoip.IpToLocationGeoIPService;

/**
 * @author Rois This class is a Morphline command which takes as input a column
 *         of IPv4 addresses and adds a column with the IP's country names
 */
@Configurable(preConstruction=true)
public class GeolocationBuilder implements CommandBuilder {
	private static Logger logger = LoggerFactory.getLogger(GeolocationBuilder.class);
	
	@Autowired
	private IpToLocationGeoIPService ipToLocationGeoIPService;
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("Geolocation");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new Geolocation(this, config, parent, child, context);
	}

	
	private class Geolocation extends AbstractCommand {

		private final String ipField;
		private final String countryFieldName;
		private final String countryIsoCodeFieldName;
		private final String regionFieldName;
		private final String cityFieldName;
		private final String ispFieldName;
		private final String usageTypeFieldName;
		private final String longtitudeFieldName;
		private final String latitudeFieldName;
		
		

		public Geolocation(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.ipField = getConfigs().getString(config, "ip_field");
			this.countryFieldName = getConfigs().getString(config, "country_field");
			this.countryIsoCodeFieldName = getConfigs().getString(config, "country_code_field");
			this.regionFieldName = getConfigs().getString(config, "region_field");
			this.cityFieldName = getConfigs().getString(config, "city_field");
			this.ispFieldName = getConfigs().getString(config, "isp_field");
			this.usageTypeFieldName = getConfigs().getString(config, "usage_type_field");
			this.longtitudeFieldName = getConfigs().getString(config, "longtitude_field");
			this.latitudeFieldName = getConfigs().getString(config, "latitude_field");
			
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
					GeoIPInfo geoIPInfo = ipToLocationGeoIPService.getGeoIPInfo(ipAddress);

					// Write the ip info:  country, city, isp, usageType
					inputRecord.put(this.countryFieldName, geoIPInfo.getCountryName());
					inputRecord.put(this.countryIsoCodeFieldName, geoIPInfo.getCountryISOCode());
					inputRecord.put(this.regionFieldName, geoIPInfo.getRegionName());
					inputRecord.put(this.cityFieldName, geoIPInfo.getCityName());
					inputRecord.put(this.ispFieldName, geoIPInfo.getISP());
					inputRecord.put(this.usageTypeFieldName, geoIPInfo.getUsageType() != null ? geoIPInfo.getUsageType().getId() : "");
					inputRecord.put(this.longtitudeFieldName, geoIPInfo.getLongitude());
					inputRecord.put(this.latitudeFieldName, geoIPInfo.getLatitude());
				} catch (Exception e) {
					logger.warn("error resolving geo2ip for {}, exception: {}", ipAddress, e.toString());
				}
			}
			return super.doProcess(inputRecord);

		}
	}
}