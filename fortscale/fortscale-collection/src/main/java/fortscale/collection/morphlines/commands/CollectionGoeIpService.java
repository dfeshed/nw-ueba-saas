package fortscale.collection.morphlines.commands;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import fortscale.geoip.GeoIPInfo;
import fortscale.geoip.GeoIPService;
import fortscale.geoip.IpToLocationGeoIPService;
import fortscale.geoip.MMGeoIPService;



@Configurable(preConstruction=true)
public class CollectionGoeIpService implements GeoIPService{
	private static Logger logger = LoggerFactory.getLogger(CollectionGoeIpService.class);

	@Autowired
	private IpToLocationGeoIPService ipToLocationGeoIPService;
	
	private List<GeoIPService> geoIpServices;
	
	public CollectionGoeIpService(){
		// Try to instantiate the GeoIP service
		geoIpServices = new ArrayList<>();
		try {
			if(ipToLocationGeoIPService != null){
				geoIpServices.add(ipToLocationGeoIPService);
			}
		} catch (Exception e) {
			logger.error("failed to load ip2location service", e);
		}
		
		try {
			GeoIPService geoIpService = new MMGeoIPService();
			geoIpServices.add(geoIpService);
		} catch (Exception e) {
			logger.error("failed to load max mind geo service", e);
		}
	}

	@Override
	public GeoIPInfo getGeoIPInfo(String ipAddress) throws UnknownHostException {
		GeoIPInfo geoIPInfo = null;
		for(GeoIPService geoIpService: geoIpServices){
			try {
				geoIPInfo = geoIpService.getGeoIPInfo(ipAddress);
				if(!StringUtils.isEmpty(geoIPInfo.getCountryName())){
					break;
				}
			} catch (IOException e) {
				logger.warn("error resolving geo2ip for {}, exception: {}", ipAddress, e.toString());
			}
			// If an error occurs, we're not adding / changing anything
		}
		
		return geoIPInfo;
	}
}
