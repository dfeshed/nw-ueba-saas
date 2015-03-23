package fortscale.services.geoIp;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import fortscale.domain.geo.dao.FortscaleGeoIpInfoRepository;
import fortscale.geoip.GeoIPService;
import fortscale.geoip.IGeoIPInfo;

public class FortscaleGeoIpService implements GeoIPService, InitializingBean {
	
	@Value("${fortscale.geolocation.load.all.to.memory:true}")
	private boolean isLoadAllToMemory;
	@Value("${fortscale.geolocation.in.memory.refresh.period.in.millis:0}")
	private long inMemoryRefreshPeriodInMillis;
	
	private Map<String, IGeoIPInfo> ipToGeoMap = null;
	private long lastRefreshTime = 0;

	@Autowired
	private FortscaleGeoIpInfoRepository fortscaleGeoIpInfoRepository;
	
	@Override
	public IGeoIPInfo getGeoIPInfo(String IPAddress) throws UnknownHostException {
		if(ipToGeoMap != null){
			if(inMemoryRefreshPeriodInMillis > 0 && System.currentTimeMillis() > (lastRefreshTime+inMemoryRefreshPeriodInMillis) ){
				refreshIpToGeoMap();
			}
			return ipToGeoMap.get(IPAddress);
		} else{
			return fortscaleGeoIpInfoRepository.findByIp(IPAddress);
		}
	}
	
	private void refreshIpToGeoMap(){
		ipToGeoMap = new HashMap<>();
		for(IGeoIPInfo geoIPInfo: fortscaleGeoIpInfoRepository.findAll()){
			ipToGeoMap.put(geoIPInfo.getIp(), geoIPInfo);
		}
		lastRefreshTime = System.currentTimeMillis();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(!isLoadAllToMemory){
			refreshIpToGeoMap();
		}
		
	}

}
