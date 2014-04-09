package fortscale.services.event.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fortscale.domain.events.VpnSession;
import fortscale.domain.events.dao.VpnSessionRepository;
import fortscale.services.event.VpnService;
import fortscale.utils.logging.Logger;

@Service("vpnService")
public class VpnServiceImpl implements VpnService{
	private static Logger logger = Logger.getLogger(VpnServiceImpl.class);
	
	@Autowired
	private VpnSessionRepository vpnSessionRepository;

	@Override
	public void createOrUpdateOpenVpnSession(VpnSession vpnSessionUpdate) {
		VpnSession vpnSession = vpnSessionRepository.findByNormalizeUsernameAndSourceIp(vpnSessionUpdate.getNormalizeUsername(), vpnSessionUpdate.getSourceIp());
		if(vpnSession == null){
			vpnSession = vpnSessionUpdate;
		} else{
			vpnSession.setCreatedAt(vpnSessionUpdate.getCreatedAt());
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

	
	
}
