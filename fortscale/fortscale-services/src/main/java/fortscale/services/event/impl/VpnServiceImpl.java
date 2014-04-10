package fortscale.services.event.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import fortscale.domain.events.VpnSession;
import fortscale.domain.events.dao.VpnSessionRepository;
import fortscale.services.event.VpnService;
import fortscale.utils.logging.Logger;

@Service("vpnService")
public class VpnServiceImpl implements VpnService{
	private static Logger logger = Logger.getLogger(VpnServiceImpl.class);
	
	public static final int VPN_GEO_HOPPING_OPEN_THRESHOLD_IN_HOURS = 48;
	public static final int VPN_GEO_HOPPING_CLOSE_THRESHOLD_IN_HOURS = 1;
	
	
	@Autowired
	private VpnSessionRepository vpnSessionRepository;
	
	private HashMap<String, GeoHoppingData> userToGeoHoppingData = new HashMap<>();
	
	
	
	
	@Override
	public void saveVpnSession(VpnSession vpnSession){
		vpnSessionRepository.save(vpnSession);
	}

	@Override
	public void createOrUpdateOpenVpnSession(VpnSession vpnSessionUpdate) {
		VpnSession vpnSession = vpnSessionRepository.findByNormalizeUsernameAndSourceIp(vpnSessionUpdate.getNormalizeUsername(), vpnSessionUpdate.getSourceIp());
		if(vpnSession == null){
			vpnSession = vpnSessionUpdate;
		} else{
			vpnSession.setCreatedAt(vpnSessionUpdate.getCreatedAt());
			vpnSession.setGeoHopping(false);
			updateVpnSessionData(vpnSession, vpnSessionUpdate);
		}
		
		vpnSession.setCreatedAtEpoch(vpnSession.getCreatedAt().getMillis());
		
		vpnSession.setModifiedAt(new DateTime());
		
		vpnSessionRepository.save(vpnSession);
	}
	
	@Override
	public void updateCloseVpnSession(VpnSession vpnSessionUpdate) {
		VpnSession vpnSession = vpnSessionRepository.findByNormalizeUsernameAndSourceIp(vpnSessionUpdate.getNormalizeUsername(), vpnSessionUpdate.getSourceIp());
		if(vpnSession == null){
			logger.warn("got close session for non existing session! username: {}, source ip: {}", vpnSessionUpdate.getNormalizeUsername(), vpnSessionUpdate.getSourceIp());
			return;
		}

		updateVpnSessionData(vpnSession, vpnSessionUpdate);
				
		vpnSession.setModifiedAt(new DateTime());
		
		vpnSessionRepository.save(vpnSession);
	}
	
	private void updateVpnSessionData(VpnSession vpnSession, VpnSession vpnSessionUpdate) {
		vpnSession.setClosedAt(vpnSessionUpdate.getClosedAt());
		if(vpnSession.getClosedAt() != null){
			vpnSession.setClosedAtEpoch(vpnSession.getClosedAt().getMillis());
		} else{
			vpnSession.setClosedAtEpoch(null);
		}
		vpnSession.setCity(vpnSessionUpdate.getCity());
		vpnSession.setCountry(vpnSessionUpdate.getCountry());
		vpnSession.setCountryIsoCode(vpnSessionUpdate.getCountryIsoCode());
		vpnSession.setDataBucket(vpnSessionUpdate.getDataBucket());
		vpnSession.setDuration(vpnSessionUpdate.getDuration());
		vpnSession.setHostname(vpnSessionUpdate.getHostname());
		vpnSession.setIsp(vpnSessionUpdate.getIsp());
		vpnSession.setIspUsage(vpnSessionUpdate.getIspUsage());
		vpnSession.setLocalIp(vpnSessionUpdate.getLocalIp());
		vpnSession.setReadBytes(vpnSessionUpdate.getReadBytes());
		vpnSession.setRegion(vpnSessionUpdate.getRegion());
		vpnSession.setTotalBytes(vpnSessionUpdate.getTotalBytes());
		vpnSession.setUsername(vpnSessionUpdate.getUsername());
		vpnSession.setWriteBytes(vpnSessionUpdate.getWriteBytes());
	}

	
	//if goe hopping exist then the given curVpnSession is updated and a list of vpn session that needed to be updated too.
	@Override
	public List<VpnSession> getGeoHoppingVpnSessions(VpnSession curVpnSession){
		if(StringUtils.isEmpty(curVpnSession.getCountry())){
			return Collections.emptyList();
		}
		GeoHoppingData geoHoppingData = getGeoHoppingData(curVpnSession);
		List<VpnSession> vpnSessions = Collections.emptyList();
		if(geoHoppingData == null){
			geoHoppingData = new GeoHoppingData();
			geoHoppingData.curCountry = curVpnSession.getCountry();
			geoHoppingData.curCountryTime = curVpnSession.getCreatedAt();
			userToGeoHoppingData.put(curVpnSession.getNormalizeUsername(), geoHoppingData);
		} else if(geoHoppingData.curCountry.equals(curVpnSession.getCountry())){
			geoHoppingData.curCountryTime = curVpnSession.getCreatedAt();
			if(geoHoppingData.otherOpenSessionCountryTime != null){
				if(curVpnSession.getCreatedAt().minusHours(VPN_GEO_HOPPING_OPEN_THRESHOLD_IN_HOURS).isAfter(geoHoppingData.otherOpenSessionCountryTime)){
					geoHoppingData.otherOpenSessionCountryTime = null;
				} else{
					curVpnSession.setGeoHopping(true);
				}
			}
			if(!curVpnSession.getGeoHopping() && geoHoppingData.otherCloseSessionCountryTime != null){
				if(curVpnSession.getCreatedAt().minusHours(VPN_GEO_HOPPING_CLOSE_THRESHOLD_IN_HOURS).isAfter(geoHoppingData.otherCloseSessionCountryTime)){
					geoHoppingData.otherCloseSessionCountryTime = null;
				} else{
					curVpnSession.setGeoHopping(true);
				}
			}
		} else{
			geoHoppingData.otherOpenSessionCountryTime = geoHoppingData.curCountryTime;
			geoHoppingData.otherCloseSessionCountryTime = null;
			if(curVpnSession.getCreatedAt().minusHours(VPN_GEO_HOPPING_OPEN_THRESHOLD_IN_HOURS).isBefore(geoHoppingData.curCountryTime)){
				vpnSessions = getGeoHoppingVpnSessions(curVpnSession, geoHoppingData.curCountry);
				if(!vpnSessions.isEmpty()){
					curVpnSession.setGeoHopping(true);
				}
				for(VpnSession vpnSession: vpnSessions){
					if(vpnSession.getClosedAt() != null){
						if(geoHoppingData.otherCloseSessionCountryTime == null || vpnSession.getClosedAt().isAfter(geoHoppingData.otherCloseSessionCountryTime)){
							geoHoppingData.otherCloseSessionCountryTime = vpnSession.getClosedAt();
						}
					}
				}
			}
			geoHoppingData.curCountry = curVpnSession.getCountry();
			geoHoppingData.curCountryTime = curVpnSession.getCreatedAt();
		}
		
		return vpnSessions;
	}
	
	private List<VpnSession> getGeoHoppingVpnSessions(VpnSession curVpnSession, String prevCountry){
		PageRequest pageRequest = new PageRequest(0, 10, Direction.DESC, VpnSession.createdAtEpochFieldName);
		List<VpnSession> vpnSessions = vpnSessionRepository.findByNormalizeUsernameAndCreatedAtEpochGreaterThan(curVpnSession.getNormalizeUsername(), curVpnSession.getCreatedAt().minusHours(VPN_GEO_HOPPING_OPEN_THRESHOLD_IN_HOURS).getMillis(), pageRequest);
		List<VpnSession> ret = new ArrayList<>();
		for(VpnSession vpnSession: vpnSessions){
			if(StringUtils.isEmpty(vpnSession.getCountry())){
				continue;
			}
			if(!vpnSession.getCountry().equals(prevCountry)){
				break;
			} else if(vpnSession.getClosedAt() == null || vpnSession.getClosedAt().plusHours(VPN_GEO_HOPPING_CLOSE_THRESHOLD_IN_HOURS).isAfter(curVpnSession.getCreatedAt())){
				ret.add(vpnSession);
			}
		}
		
		return ret;
	}
	
	
	private GeoHoppingData getGeoHoppingData(VpnSession curVpnSession){
		GeoHoppingData ret = userToGeoHoppingData.get(curVpnSession.getNormalizeUsername());
		if(ret == null){
			PageRequest pageRequest = new PageRequest(0, 100, Direction.DESC, VpnSession.createdAtEpochFieldName);
			List<VpnSession> vpnSessions = vpnSessionRepository.findByNormalizeUsernameAndCreatedAtEpochGreaterThan(curVpnSession.getNormalizeUsername(), curVpnSession.getCreatedAt().minusHours(VPN_GEO_HOPPING_OPEN_THRESHOLD_IN_HOURS).getMillis(), pageRequest);
			if(!vpnSessions.isEmpty()){
				for(VpnSession vpnSession: vpnSessions){
					if(StringUtils.isEmpty(vpnSession.getCountry())){
						continue;
					}
					if(ret == null){
						ret = new GeoHoppingData();
						ret.curCountry = vpnSession.getCountry();
						ret.curCountryTime = vpnSession.getCreatedAt();
					}else if(ret.otherOpenSessionCountryTime == null && vpnSession.getClosedAt() == null && !vpnSession.getCountry().equals(ret.curCountry)){
						ret.otherOpenSessionCountryTime = vpnSession.getCreatedAt();
					}else if(vpnSession.getClosedAt() != null && 
							(ret.otherCloseSessionCountryTime == null || ret.otherCloseSessionCountryTime.isBefore(vpnSession.getClosedAt()))){
						ret.otherCloseSessionCountryTime = vpnSession.getClosedAt();
					}
				}
			}
		}
		
		return ret;
	}
	
	
	private class GeoHoppingData{
		public String curCountry = null;
		public DateTime curCountryTime = null;
		public DateTime otherOpenSessionCountryTime = null;
		public DateTime otherCloseSessionCountryTime = null;
	}
}
