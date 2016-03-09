package fortscale.services.event.impl;

import java.io.File;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import fortscale.domain.events.VpnSession;
import fortscale.domain.events.dao.VpnSessionRepository;
import fortscale.geoip.GeoIPInfo;
import fortscale.services.event.VpnService;
import fortscale.utils.logging.Logger;

@Service("vpnService")
public class VpnServiceImpl implements VpnService,InitializingBean {
	private static Logger logger = Logger.getLogger(VpnServiceImpl.class);
	
	public static final int VPN_GEO_HOPPING_OPEN_THRESHOLD_IN_HOURS = 6;
	public static final int VPN_GEO_HOPPING_CLOSE_THRESHOLD_IN_HOURS = 1;
	
	
	@Autowired
	private VpnSessionRepository vpnSessionRepository;
	
	private HashMap<String, GeoHoppingData> userToGeoHoppingData = new HashMap<>();

	@Value("${geo-hopping.black.list.file:}")
	private String geoHoppingBlackListFilePath;

	@Value("${time.gap.for.resolve.ip.from:30}")
	Long timeGapForResolveIpFrom;
	@Value("${time.gap.for.resolve.ip.to:30}")
	Long timeGapForResolveIpTo;


	private GeoHoppingBlackListRepresentation geoHoppingBlackListRepresentation;


	@Override
	public void afterPropertiesSet(){

		//Read the blacklist from the geo hopping black list file if exist
		// and fell the list at ignoreGeoHoppingSources
		try{
			if (!StringUtils.isEmpty(geoHoppingBlackListFilePath)) {
				ObjectMapper mapper = new ObjectMapper();
				geoHoppingBlackListRepresentation = mapper.readValue(new File(geoHoppingBlackListFilePath),GeoHoppingBlackListRepresentation.class);

			}
			else {
                logger.info("There is no blacklist file for filtering GeoHoping notifications");
				geoHoppingBlackListRepresentation = new GeoHoppingBlackListRepresentation(new HashMap<String,Set<String>>(),new HashSet<String>(),new HashSet<String>());

			}

		} catch(Exception e){
			logger.warn("got the following exception while trying to read from geo hopping black list file.",e);
            geoHoppingBlackListRepresentation = new GeoHoppingBlackListRepresentation(new HashMap<String,Set<String>>(),new HashSet<String>(),new HashSet<String>());

		}


	}




	@Override
	public VpnSession findBySessionId(String sessionId){
		return vpnSessionRepository.findBySessionId(sessionId);
	}
	
	@Override
	public List<VpnSession> findByUsernameAndCreatedAtEpochBetween(String normalizeUsername, Long createdAtEpochFrom, Long createdAtEpochTo){
		PageRequest pageRequest = new PageRequest(0, 100, Direction.DESC, VpnSession.createdAtEpochFieldName);
		return vpnSessionRepository.findByUsernameAndCreatedAtEpochBetween(normalizeUsername, createdAtEpochFrom, createdAtEpochTo, pageRequest);
	}

	@Override
	public List<VpnSession> findByNormalizedUserNameAndCreatedAtEpochBetweenAndDurationExists(String normalizeUsername, Long createdAtEpochFrom, Long createdAtEpochTo){
		PageRequest pageRequest = new PageRequest(0, 100, Direction.DESC, VpnSession.createdAtEpochFieldName);
		return vpnSessionRepository.findByNormalizedUserNameAndCreatedAtEpochBetweenAndDurationExists(normalizeUsername, createdAtEpochFrom, createdAtEpochTo, true, pageRequest);
	}

	@Override
	public void saveVpnSession(VpnSession vpnSession){
		vpnSessionRepository.save(vpnSession);
	}

	@Override
	public void createOpenVpnSession(VpnSession newVpnSession) {
		newVpnSession.setCreatedAtEpoch(newVpnSession.getCreatedAt().getMillis());
		newVpnSession.setModifiedAt(new DateTime());

		vpnSessionRepository.save(newVpnSession);
	}

	@Override
	public VpnSession findOpenVpnSession(VpnSession closeVpnSession){
		VpnSession ret = null;
		if(StringUtils.isNotEmpty(closeVpnSession.getSessionId())){
			ret = vpnSessionRepository.findBySessionId(closeVpnSession.getSessionId());
		} else if(StringUtils.isNotEmpty(closeVpnSession.getUsername()) && StringUtils.isNotEmpty(closeVpnSession.getSourceIp())){
			Integer duration = closeVpnSession.getDuration();

			if (duration != null) {
				Long startSessionTime = closeVpnSession.getClosedAt().minusMillis(duration * 1000).getMillis();
				List<VpnSession> vpnOpenSessions = findByUsernameAndCreatedAtEpochBetween(closeVpnSession.getUsername(), startSessionTime - timeGapForResolveIpFrom * 1000, startSessionTime + timeGapForResolveIpTo * 1000);
				if (vpnOpenSessions != null && vpnOpenSessions.size() > 0) {
					ret = findFittestSession(vpnOpenSessions, startSessionTime);
				}
			} else { //duration do not exist so we search for the closest OPEN session
				PageRequest pageRequest = new PageRequest(0, 1, Direction.DESC, VpnSession.createdAtEpochFieldName);
				List<VpnSession> sessions = vpnSessionRepository.findByUsernameAndSourceIp(closeVpnSession.getUsername(), closeVpnSession.getSourceIp(), pageRequest);
				if (sessions.size() > 0 ) {
					ret = sessions.get(0);
				}
			}
		}

		return ret;
	}

	@Override
	public void updateCloseVpnSession(VpnSession vpnSessionUpdate) {
		VpnSession vpnSession = findOpenVpnSession(vpnSessionUpdate);
		if(vpnSession == null){
			logger.debug("got close session for non existing session! username: {}, source ip: {}", vpnSessionUpdate.getUsername(), vpnSessionUpdate.getSourceIp());
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

	public VpnSession findFittestSession(List<VpnSession> vpnOpenSessions, Long startSessionTime) {
		Long gap = null;
		VpnSession vpnSession = null;

		if (vpnOpenSessions.size() == 1){
			return vpnOpenSessions.get(0);
		}

		for (VpnSession vpnOpenSession : vpnOpenSessions){
			long localGap = Math.abs(vpnOpenSession.getCreatedAtEpoch() - startSessionTime);
			if (gap == null || localGap < gap){
				gap = localGap;
				vpnSession = vpnOpenSession;
			}
		}
		return vpnSession;
	}

	//if goe hopping exist then the given curVpnSession is updated and a list of vpn session that needed to be updated too.
	@Override
	public List<VpnSession> getGeoHoppingVpnSessions(VpnSession curVpnSession){
		return getGeoHoppingVpnSessions(curVpnSession, VPN_GEO_HOPPING_CLOSE_THRESHOLD_IN_HOURS, VPN_GEO_HOPPING_OPEN_THRESHOLD_IN_HOURS);
	}
	
	
	// Return vpn sessions that are geo hopping events with the the current vpn session.
	// in case that the source_ip or the country+city exist at the blacklist skip this event from geo - hopping process
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

		if(!isValidSessionForGeoHopping(curVpnSession)){
			return Collections.emptyList();
		}

		//Getting the user's vpn session state which help decide if there is a geo event with out querying the DB.
		GeoHoppingData geoHoppingData = getGeoHoppingData(curVpnSession, vpnGeoHoppingOpenSessionThresholdInHours);
		List<VpnSession> vpnSessions = Collections.emptyList();
		if(geoHoppingData == null){
			//This is the first vpn session ever for this user.
			addNewGeoHoppingData(curVpnSession);
		} else if(geoHoppingData.isEqualsGeoLocation(curVpnSession)){
			//In this case the current vpn session is from the country as the previous received vpn session.
			//Notice that the current vpn session may be a geo-hopping event only if the vpn session before was also a geo-hopping event.
			geoHoppingData.curCountryTime = curVpnSession.getCreatedAt();
			geoHoppingData.curCountry = curVpnSession.getCountry();
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
			logger.debug("setting the other close and open session country time to null");
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
					logger.debug("changing otherCloseSessionCountryTime to {}",geoHoppingData.otherCloseSessionCountryTime);
				}
				if(geoHoppingData.otherOpenSessionCountryTime != null){
					logger.debug("changing otherOpenSessionCountryTime to {}",geoHoppingData.otherOpenSessionCountryTime);
				}
			}
			logger.debug("changing curCountry from {} to {} and curCountryTime from {} to {}",geoHoppingData.curCountry, curVpnSession.getCountry(), geoHoppingData.curCountryTime, curVpnSession.getCreatedAt());
			updateGeoHoppingCurrentData(curVpnSession, geoHoppingData);
		}
		
		return vpnSessions;
	}

	private void updateGeoHoppingCurrentData(VpnSession vpnSession, GeoHoppingData geoHoppingData) {
		geoHoppingData.curCountry = vpnSession.getCountry();
		geoHoppingData.curCountryTime = vpnSession.getCreatedAt();
		geoHoppingData.curIsp = vpnSession.getIsp();
	}


	// Returns vpn sessions that are from the given prevCountry and with in the given thresholds bounds.
	private List<VpnSession> getGeoHoppingVpnSessions(VpnSession curVpnSession, String prevCountry, int vpnGeoHoppingCloseSessionThresholdInHours, int vpnGeoHoppingOpenSessionThresholdInHours){
		logger.debug("looking for vpn sessions from {} which were created at most {} hours before {} and closed at most {} hours before that same time", prevCountry, vpnGeoHoppingOpenSessionThresholdInHours, curVpnSession.getCreatedAt(),
				vpnGeoHoppingCloseSessionThresholdInHours);
		PageRequest pageRequest = new PageRequest(0, 10, Direction.DESC, VpnSession.createdAtEpochFieldName);
		List<VpnSession> vpnSessions = vpnSessionRepository.findByUsernameAndCreatedAtEpochGreaterThan(curVpnSession.getUsername(), curVpnSession.getCreatedAt().minusHours(vpnGeoHoppingOpenSessionThresholdInHours).getMillis(), pageRequest);
		List<VpnSession> ret = new ArrayList<>();
		for(VpnSession vpnSession: vpnSessions){
			if(!isValidSessionForGeoHopping(vpnSession)){
				continue;
			}
			if(!vpnSession.getCountry().equals(prevCountry)){
				logger.debug("got vpn session with different country then {}, hence all the event before it got notification if there was a need. VpnSession: sessionid({}), sourceIp({}), country ({})",prevCountry, vpnSession.getSessionId(),
						vpnSession.getSourceIp(), vpnSession.getCountry());
				break;
			} else if(vpnSession.getClosedAt() == null || vpnSession.getClosedAt().plusHours(vpnGeoHoppingCloseSessionThresholdInHours).isAfter(curVpnSession.getCreatedAt())){
				ret.add(vpnSession);
			}
		}
		
		return ret;
	}
	
	
	private GeoHoppingData getGeoHoppingData(VpnSession curVpnSession, int vpnGeoHoppingOpenSessionThresholdInHours){
		GeoHoppingData ret = userToGeoHoppingData.get(curVpnSession.getUsername());
		if(ret == null){
			PageRequest pageRequest = new PageRequest(0, 100, Direction.DESC, VpnSession.createdAtEpochFieldName);
			List<VpnSession> vpnSessions = vpnSessionRepository.findByUsernameAndCreatedAtEpochGreaterThan(curVpnSession.getUsername(), curVpnSession.getCreatedAt().minusHours(vpnGeoHoppingOpenSessionThresholdInHours).getMillis(), pageRequest);
			if(!vpnSessions.isEmpty()){
				for(VpnSession vpnSession: vpnSessions){
					if(!isValidSessionForGeoHopping(vpnSession)){
						continue;
					}
					if(ret == null){
						ret = new GeoHoppingData();
						updateGeoHoppingCurrentData(vpnSession, ret);
					}else if(ret.otherOpenSessionCountryTime == null && vpnSession.getClosedAt() == null && !ret.isEqualsGeoLocation(vpnSession)) {
						ret.otherOpenSessionCountryTime = vpnSession.getCreatedAt();
					}else if(vpnSession.getClosedAt() != null &&
							!ret.isEqualsGeoLocation(vpnSession) &&
							(ret.otherCloseSessionCountryTime == null || ret.otherCloseSessionCountryTime.isBefore(vpnSession.getClosedAt()))){
						ret.otherCloseSessionCountryTime = vpnSession.getClosedAt();
					}
				}
				if(ret != null){
					userToGeoHoppingData.put(curVpnSession.getUsername(), ret);
				}
			}
		}
		
		return ret;
	}
	
	private void addNewGeoHoppingData(VpnSession curVpnSession){
		GeoHoppingData geoHoppingData = new GeoHoppingData();
		updateGeoHoppingCurrentData(curVpnSession, geoHoppingData);
		userToGeoHoppingData.put(curVpnSession.getUsername(), geoHoppingData);
	}
	
	/**
	 * This method validate the current event geo hopping needed - In case that we have blacklist we want to check if this event belong to that list
	 * In case that the event is not belong we will continue to process the geo - hopping on it other case we will return empty list (filter this event from geo hopping)
	 * @param curVpnSession
	 * @return
	 */
	private boolean skipBasedOnBlackList(VpnSession curVpnSession) {

		String currCountry = curVpnSession.getCountry();
		String currCity = curVpnSession.getCity();
		String source_ip = curVpnSession.getSourceIp();


		if(geoHoppingBlackListRepresentation.getSourceIp().contains(source_ip))
			return true;
		if (geoHoppingBlackListRepresentation.getCountry().contains(currCountry))
			return true;
		if (geoHoppingBlackListRepresentation.getCountryCityMap().containsKey(currCountry) &&  geoHoppingBlackListRepresentation.getCountryCityMap().get(currCountry).contains(currCity) )
		{
			return true;
		}

		return false;
	}
	
	private boolean isValidSessionForGeoHopping(VpnSession vpnSession){
		return StringUtils.isNotEmpty(vpnSession.getCountry()) && !GeoIPInfo.RESERVED_RANGE.equalsIgnoreCase(vpnSession.getCountry()) && !skipBasedOnBlackList(vpnSession);
	}
	
	private class GeoHoppingData{
		public String curCountry = null;
		public DateTime curCountryTime = null;
		public DateTime otherOpenSessionCountryTime = null;
		public DateTime otherCloseSessionCountryTime = null;
		public String curIsp;

		/**
		 * Check if VPNSession and geoHoppingData has the same location or not.
		 * Currently the current location is compose of country and ISP.
		 * @param vpnSession

		 * @return
		 */
		public boolean isEqualsGeoLocation(VpnSession vpnSession) {
			boolean sameLocations =  this.curCountry.equals(vpnSession.getCountry());

			//If country or city are different and both vpn sessions as ISP,
			// make sure that the ISP is not the same. If the ISP is the same, this is not geo hopping
			if (!sameLocations) {
				if (StringUtils.isNotBlank(vpnSession.getIsp()) &&
						StringUtils.isNotBlank(this.curIsp) &&
						vpnSession.getIsp().equals(this.curIsp )) {
					sameLocations = true;
				}
			}
			return  sameLocations;

		}

	}


	@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
	public static class GeoHoppingBlackListRepresentation
	{



		private HashMap<String,Set<String>> countryCityMap;

		private Set<String> country;

		private Set<String> sourceIp;

        public GeoHoppingBlackListRepresentation (){}


        public GeoHoppingBlackListRepresentation ( HashMap<String,Set<String>> countryCityMap , Set<String> country , Set<String> sourceIp )
        {
            this.country = country;
            this.sourceIp = sourceIp;
            this.countryCityMap = countryCityMap;
        }


        @JsonProperty("countryCityMap")
		public HashMap<String, Set<String>> getCountryCityMap() {
			return countryCityMap;
		}

		public void setCountryCityMap(HashMap<String, Set<String>> countryCityMap) {
			this.countryCityMap = countryCityMap;
		}

        @JsonProperty("country")
		public Set<String> getCountry() {
			return country;
		}

		public void setCountry(Set<String> country) {
			this.country = country;
		}

        @JsonProperty("sourceIp")
		public Set<String> getSourceIp() {
			return sourceIp;
		}

		public void setSourceIp(Set<String> sourceIp) {
			this.sourceIp = sourceIp;
		}





	}
}
