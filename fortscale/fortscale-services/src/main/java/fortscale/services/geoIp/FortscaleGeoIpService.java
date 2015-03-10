package fortscale.services.geoIp;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;

import fortscale.domain.geo.dao.FortscaleGeoIpInfoRepository;
import fortscale.geoip.GeoIPService;
import fortscale.geoip.IGeoIPInfo;

public class FortscaleGeoIpService implements GeoIPService {

	@Autowired
	private FortscaleGeoIpInfoRepository fortscaleGeoIpInfoRepository;
	
	@Override
	public IGeoIPInfo getGeoIPInfo(String IPAddress) throws UnknownHostException {
		return fortscaleGeoIpInfoRepository.findByIp(IPAddress);
	}

}
