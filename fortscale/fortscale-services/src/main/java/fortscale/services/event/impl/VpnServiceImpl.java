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
	
	
	// Return vpn sessions that are geo hopping events with the the current vpn session.
	// If the current vpn session is a geo hopping event then it (curVpnSession) is marked.
	//
	// A vpn session is a geo-hopping event if there is a vpn session from a different country which was opened with in the given boundary
	//   and if it was closed then it is also with in the given boundary.
	//
	// Assumption: 1. the events are received by time order. 2. Hopping from one country to another doesn't occur a lot.
	// userToGeoHoppingData is using assumption #2 to reduce the db access. For each user it saves:
	//                           - the country and the creation time of the last vpn session (in order to use assumption #2)
	//                           - the latest creation and close time of the vpn session (might be 2 different vpn sessions) from different country than the last vpn session.
	@Override
	public List<VpnSession> getGeoHoppingVpnSessions(VpnSession curVpnSession, int vpnGeoHoppingCloseSessionThresholdInHours, int vpnGeoHoppingOpenSessionThresholdInHours){
		if(!isCountryValid(curVpnSession)){
			return Collections.emptyList();
		}

		//Getting the user's vpn session state which help decide if there is a geo event with out querying the DB.
		GeoHoppingData geoHoppingData = getGeoHoppingData(curVpnSession, vpnGeoHoppingOpenSessionThresholdInHours);
		List<VpnSession> vpnSessions = Collections.emptyList();
		if(geoHoppingData == null){
			//This is the first vpn session ever for this user.
			addNewGeoHoppingData(curVpnSession);
		} else if(geoHoppingData.curCountry.equals(curVpnSession.getCountry())){
			//In this case the current vpn session is from the country as the previous received vpn session.
			//Notice that the current vpn session may be a geo-hopping event only if the vpn session before was also a geo-hopping event.
			geoHoppingData.curCountryTime = curVpnSession.getCreatedAt();
			if(geoHoppingData.otherOpenSessionCountryTime != null){
				if(curVpnSession.getCreatedAt().minusHours(vpnGeoHoppingOpenSessionThresholdInHours).isAfter(geoHoppingData.otherOpenSessionCountryTime)){
					geoHoppingData.otherOpenSessionCountryTime = null;
				} else{
					curVpnSession.setGeoHopping(true);
					logger.info("geo hopping due to other open session country time {}. more info: curCountry ({}). curCountryTime({})", geoHoppingData.otherOpenSessionCountryTime, geoHoppingData.curCountry, geoHoppingData.curCountryTime);
				}
			}
			if(!curVpnSession.getGeoHopping() && geoHoppingData.otherCloseSessionCountryTime != null){
				if(curVpnSession.getCreatedAt().minusHours(vpnGeoHoppingCloseSessionThresholdInHours).isAfter(geoHoppingData.otherCloseSessionCountryTime)){
					geoHoppingData.otherCloseSessionCountryTime = null;
				} else{
					curVpnSession.setGeoHopping(true);
					logger.info("geo hopping due to other close session country time {}. more info: curCountry ({}). curCountryTime({})", geoHoppingData.otherCloseSessionCountryTime, geoHoppingData.curCountry, geoHoppingData.curCountryTime);
				}
			}
		} else{
			//In this case the current vpn session is from a different country then the previous received vpn session.
			logger.info("setting the other close and open session country time to null");
			geoHoppingData.otherOpenSessionCountryTime = null;
			geoHoppingData.otherCloseSessionCountryTime = null;
			//A geo hopping event may only occur if the previous vpn session was opened at most vpnGeoHoppingOpenSessionThresholdInHours hours ago.
			if(curVpnSession.getCreatedAt().minusHours(vpnGeoHoppingOpenSessionThresholdInHours).isBefore(geoHoppingData.curCountryTime)){
				// Getting vpn sessions that are from the country of the previous vpn session and with in the open and close thresholds bounds.
				vpnSessions = getGeoHoppingVpnSessions(curVpnSession, geoHoppingData.curCountry, vpnGeoHoppingCloseSessionThresholdInHours, vpnGeoHoppingOpenSessionThresholdInHours);
				if(!vpnSessions.isEmpty()){
					curVpnSession.setGeoHopping(true);
				}
				
				//Calculating the time of the last open and close session that occurred in different country
				for(VpnSession vpnSession: vpnSessions){
					if(vpnSession.getClosedAt() != null){
						if(geoHoppingData.otherCloseSessionCountryTime == null || vpnSession.getClosedAt().isAfter(geoHoppingData.otherCloseSessionCountryTime)){
							geoHoppingData.otherCloseSessionCountryTime = vpnSession.getClosedAt();
						}
					} else if(geoHoppingData.otherOpenSessionCountryTime == null || vpnSession.getCreatedAt().isAfter(geoHoppingData.otherOpenSessionCountryTime)){
						geoHoppingData.otherOpenSessionCountryTime = vpnSession.getCreatedAt();
					}
				}
				if(geoHoppingData.otherCloseSessionCountryTime != null){
					logger.info("changing otherCloseSessionCountryTime to {}",geoHoppingData.otherCloseSessionCountryTime);
				}
				if(geoHoppingData.otherOpenSessionCountryTime != null){
					logger.info("changing otherOpenSessionCountryTime to {}",geoHoppingData.otherOpenSessionCountryTime);
				}
			}
			logger.info("changing curCountry from {} to {} and curCountryTime from {} to {}",geoHoppingData.curCountry, curVpnSession.getCountry(), geoHoppingData.curCountryTime, curVpnSession.getCreatedAt());
			geoHoppingData.curCountry = curVpnSession.getCountry();
			geoHoppingData.curCountryTime = curVpnSession.getCreatedAt();
		}
		
		return vpnSessions;
	}
	
	// Returns vpn sessions that are from the given prevCountry and with in the given thresholds bounds.
	private List<VpnSession> getGeoHoppingVpnSessions(VpnSession curVpnSession, String prevCountry, int vpnGeoHoppingCloseSessionThresholdInHours, int vpnGeoHoppingOpenSessionThresholdInHours){
		logger.info("looking for vpn sessions from {} which were created at most {} hours before {} and closed at most {} hours before that same time", prevCountry, vpnGeoHoppingOpenSessionThresholdInHours, curVpnSession.getCreatedAt(),
				vpnGeoHoppingCloseSessionThresholdInHours);
		PageRequest pageRequest = new PageRequest(0, 10, Direction.DESC, VpnSession.createdAtEpochFieldName);
		List<VpnSession> vpnSessions = vpnSessionRepository.findByNormalizeUsernameAndCreatedAtEpochGreaterThan(curVpnSession.getNormalizeUsername(), curVpnSession.getCreatedAt().minusHours(vpnGeoHoppingOpenSessionThresholdInHours).getMillis(), pageRequest);
		List<VpnSession> ret = new ArrayList<>();
		for(VpnSession vpnSession: vpnSessions){
			if(!isCountryValid(vpnSession)){
				continue;
			}
			if(!vpnSession.getCountry().equals(prevCountry)){
				logger.info("got vpn session with different country then {}, hence all the event before it got notification if there was a need. VpnSession: sessionid({}), sourceIp({}), country ({})",prevCountry, vpnSession.getSessionId(),
						vpnSession.getSourceIp(), vpnSession.getCountry());
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
							!vpnSession.getCountry().equals(ret.curCountry) && 
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