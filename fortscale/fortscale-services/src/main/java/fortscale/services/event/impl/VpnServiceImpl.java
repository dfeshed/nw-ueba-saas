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
import fortscale.geoip.GeoIPInfo;
import fortscale.services.event.VpnService;
import fortscale.utils.logging.Logger;

@Service("vpnService")
public class VpnServiceImpl implements VpnService{
	private static Logger logger = Logger.getLogger(VpnServiceImpl.class);
	
	public static final int VPN_GEO_HOPPING_OPEN_THRESHOLD_IN_HOURS = 6;
	public static final int VPN_GEO_HOPPING_CLOSE_THRESHOLD_IN_HOURS = 1;
	
	
	@Autowired
	private VpnSessionRepository vpnSessionRepository;
	
	private HashMap<String, GeoHoppingData> userToGeoHoppingData = new HashMap<>();
	
	
	
	
	@Override
	public VpnSession findBySessionId(String sessionId){
		return vpnSessionRepository.findBySessionId(sessionId);
	}
	
	@Override
	public VpnSession findByNormalizeUsernameAndSourceIp(String normalizeUsername, String sourceIp){
		return vpnSessionRepository.findByNormalizeUsernameAndSourceIp(normalizeUsername, sourceIp);
	}
	
	@Override
	public void saveVpnSession(VpnSession vpnSession){
		vpnSessionRepository.save(vpnSession);
	}

	@Override
	public void createOrUpdateOpenVpnSession(VpnSession vpnSessionUpdate) {
		VpnSession vpnSession = findVpnSession(vpnSessionUpdate);
		if(vpnSession == null){
			vpnSession = vpnSessionUpdate;
		} else{
			vpnSession.setSourceIp(vpnSessionUpdate.getSourceIp());
			vpnSession.setNormalizeUsername(vpnSessionUpdate.getNormalizeUsername());
			vpnSession.setCreatedAt(vpnSessionUpdate.getCreatedAt());
			vpnSession.setClosedAtEpoch(null);
			vpnSession.setClosedAt(null);
			vpnSession.setGeoHopping(false);
			vpnSession.setCity(vpnSessionUpdate.getCity());
			vpnSession.setCountry(vpnSessionUpdate.getCountry());
			vpnSession.setCountryIsoCode(vpnSessionUpdate.getCountryIsoCode());
			vpnSession.setDataBucket(null);
			vpnSession.setDuration(null);
			vpnSession.setHostname(vpnSessionUpdate.getHostname());
			vpnSession.setIsp(vpnSessionUpdate.getIsp());
			vpnSession.setIspUsage(vpnSessionUpdate.getIspUsage());
			vpnSession.setLocalIp(vpnSessionUpdate.getLocalIp());
			vpnSession.setReadBytes(null);
			vpnSession.setRegion(vpnSessionUpdate.getRegion());
			vpnSession.setTotalBytes(null);
			vpnSession.setUsername(vpnSessionUpdate.getUsername());
			vpnSession.setWriteBytes(null);
		}
		
		vpnSession.setCreatedAtEpoch(vpnSession.getCreatedAt().getMillis());
		
		vpnSession.setModifiedAt(new DateTime());
		
		vpnSessionRepository.save(vpnSession);
	}
	private VpnSession findVpnSession(VpnSession vpnSessionUpdate){
		VpnSession ret = null;
		if(StringUtils.isNotEmpty(vpnSessionUpdate.getSessionId())){
			ret = vpnSessionRepository.findBySessionId(vpnSessionUpdate.getSessionId());
		} else if(StringUtils.isNotEmpty(vpnSessionUpdate.getNormalizeUsername()) && StringUtils.isNotEmpty(vpnSessionUpdate.getSourceIp())){
			ret = vpnSessionRepository.findByNormalizeUsernameAndSourceIp(vpnSessionUpdate.getNormalizeUsername(), vpnSessionUpdate.getSourceIp());
		}
		
		return ret;
	}
	
	@Override
	public void updateCloseVpnSession(VpnSession vpnSessionUpdate) {
		VpnSession vpnSession = findVpnSession(vpnSessionUpdate);
		if(vpnSession == null){
			logger.warn("got close session for non existing session! username: {}, source ip: {}", vpnSessionUpdate.getNormalizeUsername(), vpnSessionUpdate.getSourceIp());
			return;
		}

		vpnSession.setClosedAt(vpnSessionUpdate.getClosedAt());
		vpnSession.setClosedAtEpoch(vpnSession.getClosedAt().getMillis());
		vpnSession.setDataBucket(vpnSessionUpdate.getDataBucket());
		vpnSession.setDuration(vpnSessionUpdate.getDuration());
		vpnSession.setReadBytes(vpnSessionUpdate.getReadBytes());
		vpnSession.setWriteBytes(vpnSessionUpdate.getWriteBytes());
		vpnSession.setTotalBytes(vpnSessionUpdate.getTotalBytes());
				
		vpnSession.setModifiedAt(new DateTime());
		
		vpnSessionRepository.save(vpnSession);
	}
	
	//if goe hopping exist then the given curVpnSession is updated and a list of vpn session that needed to be updated too.
	@Override
	public List<VpnSession> getGeoHoppingVpnSessions(VpnSession curVpnSession){
		return getGeoHoppingVpnSessions(curVpnSession, VPN_GEO_HOPPING_CLOSE_THRESHOLD_IN_HOURS, VPN_GEO_HOPPING_OPEN_THRESHOLD_IN_HOURS);
	}
	
	@Override
	public List<VpnSession> getGeoHoppingVpnSessions(VpnSession curVpnSession, int vpnGeoHoppingCloseSessionThresholdInHours, int vpnGeoHoppingOpenSessionThresholdInHours){
		if(!isCountryValid(curVpnSession)){
			return Collections.emptyList();
		}

		GeoHoppingData geoHoppingData = getGeoHoppingData(curVpnSession, vpnGeoHoppingOpenSessionThresholdInHours);
		List<VpnSession> vpnSessions = Collections.emptyList();
		if(geoHoppingData == null){
			addNewGeoHoppingData(curVpnSession);
		} else if(geoHoppingData.curCountry.equals(curVpnSession.getCountry())){
			geoHoppingData.curCountryTime = curVpnSession.getCreatedAt();
			if(geoHoppingData.otherOpenSessionCountryTime != null){
				if(curVpnSession.getCreatedAt().minusHours(vpnGeoHoppingOpenSessionThresholdInHours).isAfter(geoHoppingData.otherOpenSessionCountryTime)){
					geoHoppingData.otherOpenSessionCountryTime = null;
				} else{
					curVpnSession.setGeoHopping(true);
				}
			}
			if(!curVpnSession.getGeoHopping() && geoHoppingData.otherCloseSessionCountryTime != null){
				if(curVpnSession.getCreatedAt().minusHours(vpnGeoHoppingCloseSessionThresholdInHours).isAfter(geoHoppingData.otherCloseSessionCountryTime)){
					geoHoppingData.otherCloseSessionCountryTime = null;
				} else{
					curVpnSession.setGeoHopping(true);
				}
			}
		} else{
			geoHoppingData.otherOpenSessionCountryTime = geoHoppingData.curCountryTime;
			geoHoppingData.otherCloseSessionCountryTime = null;
			if(curVpnSession.getCreatedAt().minusHours(vpnGeoHoppingOpenSessionThresholdInHours).isBefore(geoHoppingData.curCountryTime)){
				vpnSessions = getGeoHoppingVpnSessions(curVpnSession, geoHoppingData.curCountry, vpnGeoHoppingCloseSessionThresholdInHours, vpnGeoHoppingOpenSessionThresholdInHours);
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
	
	private List<VpnSession> getGeoHoppingVpnSessions(VpnSession curVpnSession, String prevCountry, int vpnGeoHoppingCloseSessionThresholdInHours, int vpnGeoHoppingOpenSessionThresholdInHours){
		PageRequest pageRequest = new PageRequest(0, 10, Direction.DESC, VpnSession.createdAtEpochFieldName);
		List<VpnSession> vpnSessions = vpnSessionRepository.findByNormalizeUsernameAndCreatedAtEpochGreaterThan(curVpnSession.getNormalizeUsername(), curVpnSession.getCreatedAt().minusHours(vpnGeoHoppingOpenSessionThresholdInHours).getMillis(), pageRequest);
		List<VpnSession> ret = new ArrayList<>();
		for(VpnSession vpnSession: vpnSessions){
			if(StringUtils.isEmpty(vpnSession.getCountry())){
				continue;
			}
			if(!vpnSession.getCountry().equals(prevCountry)){
				break;
			} else if(vpnSession.getClosedAt() == null || vpnSession.getClosedAt().plusHours(vpnGeoHoppingCloseSessionThresholdInHours).isAfter(curVpnSession.getCreatedAt())){
				ret.add(vpnSession);
			}
		}
		
		return ret;
	}
	
	
	private GeoHoppingData getGeoHoppingData(VpnSession curVpnSession, int vpnGeoHoppingOpenSessionThresholdInHours){
		GeoHoppingData ret = userToGeoHoppingData.get(curVpnSession.getNormalizeUsername());
		if(ret == null){
			PageRequest pageRequest = new PageRequest(0, 100, Direction.DESC, VpnSession.createdAtEpochFieldName);
			List<VpnSession> vpnSessions = vpnSessionRepository.findByNormalizeUsernameAndCreatedAtEpochGreaterThan(curVpnSession.getNormalizeUsername(), curVpnSession.getCreatedAt().minusHours(vpnGeoHoppingOpenSessionThresholdInHours).getMillis(), pageRequest);
			if(!vpnSessions.isEmpty()){
				for(VpnSession vpnSession: vpnSessions){
					if(!isCountryValid(vpnSession)){
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
				if(ret != null){
					userToGeoHoppingData.put(curVpnSession.getNormalizeUsername(), ret);
				}
			}
		}
		
		return ret;
	}
	
	private void addNewGeoHoppingData(VpnSession curVpnSession){
		GeoHoppingData geoHoppingData = new GeoHoppingData();
		geoHoppingData.curCountry = curVpnSession.getCountry();
		geoHoppingData.curCountryTime = curVpnSession.getCreatedAt();
		userToGeoHoppingData.put(curVpnSession.getNormalizeUsername(), geoHoppingData);
	}
	
	private boolean isCountryValid(VpnSession vpnSession){
		return StringUtils.isNotEmpty(vpnSession.getCountry()) && !GeoIPInfo.RESERVED_RANGE.equalsIgnoreCase(vpnSession.getCountry());
	}
	
	private class GeoHoppingData{
		public String curCountry = null;
		public DateTime curCountryTime = null;
		public DateTime otherOpenSessionCountryTime = null;
		public DateTime otherCloseSessionCountryTime = null;
	}
}
