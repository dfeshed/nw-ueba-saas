package fortscale.services.geoIp;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import fortscale.geoip.CachedGeoIPService;
import fortscale.geoip.GeoIPInfo;
import fortscale.geoip.GeoIPService;
import fortscale.geoip.IGeoIPInfo;
import fortscale.utils.logging.Logger;



public class MultiProviderGeoIpService extends CachedGeoIPService implements ApplicationContextAware, InitializingBean {
	private static Logger logger = Logger.getLogger(MultiProviderGeoIpService.class);
	
	private ApplicationContext applicationContext;
	
	@Value("${geo.ip.providers.service.bean:}")
	private String geoIpProvidersServiceBeanNames;
	
	private List<GeoIPService> geoIpServices = new ArrayList<>();

	@Override
	protected IGeoIPInfo doGetGeoIPInfo(String IPAddress) throws UnknownHostException {
		IGeoIPInfo geoIPInfo = null;
		for(GeoIPService geoIPService: geoIpServices){
			try{
				geoIPInfo = geoIPService.getGeoIPInfo(IPAddress);
				if(geoIPInfo != null && StringUtils.isNotEmpty(geoIPInfo.getCountryName())){
					break;
				}
			} catch (Exception e) {
				logger.debug("error resolving geo2ip for {}, exception: {}", IPAddress, e.toString());
			}
		}
		if(geoIPInfo == null){
			geoIPInfo = new GeoIPInfo(IPAddress);
		}
		return geoIPInfo;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(StringUtils.isNotBlank(geoIpProvidersServiceBeanNames)){
			for(String providerBean: geoIpProvidersServiceBeanNames.split(",")){
				GeoIPService geoIPService = applicationContext.getBean(providerBean, GeoIPService.class);
				geoIpServices.add(geoIPService);
			}
		}
	}
}
