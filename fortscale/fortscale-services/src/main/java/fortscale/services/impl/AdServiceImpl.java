package fortscale.services.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fortscale.domain.ad.AdComputer;
import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdOU;
import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.dao.AdComputerRepository;
import fortscale.domain.ad.dao.AdGroupRepository;
import fortscale.domain.ad.dao.AdOURepository;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.services.AdService;
import fortscale.utils.logging.Logger;




@Service("adService")
public class AdServiceImpl implements AdService {
	private static Logger logger = Logger.getLogger(AdServiceImpl.class);
	
	@Autowired
	private AdUserRepository adUserRepository;
	
	@Autowired
	private AdGroupRepository adGroupRepository;
	
	@Autowired
	private AdComputerRepository adComputerRepository;
	
	@Autowired
	private AdOURepository adOURepository;
	
	@Autowired
	private AdUsersFeaturesExtractionRepository adUsersFeaturesExtractionRepository;

	@Override
	public void addLastModifiedFieldToAllCollections() {
		List<AdUser> adUsers = adUserRepository.findByLastModifiedExists(false);
		for(AdUser adUser: adUsers){
			try {
				adUser.setLastModified(new Date());
			} catch (Exception e) {
				logger.error("got exception while trying to update ad user field last modified date!!! dn: {}", adUser.getDistinguishedName());
			}
			
		}
		adUserRepository.save(adUsers);
		
		List<AdGroup> adGroups = adGroupRepository.findByLastModifiedExists(false);
		for(AdGroup adGroup: adGroups){
			try {
				adGroup.setLastModified(new Date());
			} catch (Exception e) {
				logger.error("got exception while trying to update ad group field last modified date!!! dn: {}", adGroup.getDistinguishedName());
			}
			
		}
		adGroupRepository.save(adGroups);
		
		List<AdComputer> adComputers = adComputerRepository.findByLastModifiedExists(false);
		for(AdComputer adComputer: adComputers){
			try {
				adComputer.setLastModified(new Date());
			} catch (Exception e) {
				logger.error("got exception while trying to update ad computer field last modified date!!! dn: {}", adComputer.getDistinguishedName());
			}
			
		}
		adComputerRepository.save(adComputers);
		
		List<AdOU> adOUs = adOURepository.findByLastModifiedExists(false);
		for(AdOU adOU: adOUs){
			try {
				adOU.setLastModified(new Date());
			} catch (Exception e) {
				logger.error("got exception while trying to update ad OU field last modified date!!! dn: {}", adOU.getDistinguishedName());
			}
			
		}
		adOURepository.save(adOUs);
		
		List<AdUserFeaturesExtraction> adUserFeaturesExtractions = adUsersFeaturesExtractionRepository.findByLastModifiedExists(false);
		for(AdUserFeaturesExtraction adUserFeaturesExtraction: adUserFeaturesExtractions){
			try {
				adUserFeaturesExtraction.setLastModified(new Date());
			} catch (Exception e) {
				logger.error("got exception while trying to update ad UserFeaturesExtraction field last modified date!!! userId: {}", adUserFeaturesExtraction.getUserId());
			}
			
		}
		adUsersFeaturesExtractionRepository.save(adUserFeaturesExtractions);
	}

}
