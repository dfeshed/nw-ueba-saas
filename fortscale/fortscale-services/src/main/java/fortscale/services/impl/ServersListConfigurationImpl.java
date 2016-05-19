package fortscale.services.impl;

import fortscale.domain.system.dao.SystemConfigurationRepository;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ServersListConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("ServersListConfiguration")
public class ServersListConfigurationImpl implements ServersListConfiguration {



	private static Logger logger = LoggerFactory.getLogger(ServersListConfigurationImpl.class);

	@Autowired
	private SystemConfigurationRepository systemConfigurationRepository;

	@Autowired
	private ActiveDirectoryService activeDirectoryService;

	@Value("${login.service.name.regex:}")
	private String loginServiceNameRegex;

	@Value("${login.account.name.regex:}")
	private String loginAccountNameRegex;

	@Override
	public List<String> getDomainControllers() {
		List<String> domainControllers = new ArrayList<>();
		try {
			logger.info("Trying to retrieve Domain Controllers from DB");
			domainControllers = activeDirectoryService.getDomainControllersFromDatabase();
			if(domainControllers.isEmpty()) {
				logger.warn("No Domain Controllers were found in DB. Trying to retrieve DCs from Active Directory");
				domainControllers = activeDirectoryService.getDomainControllersFromActiveDirectory();
				logger.debug("Found domain controllers in Active Directory");
				activeDirectoryService.saveDomainControllersInDatabase(domainControllers);
			}
		} catch (Exception e) {
			logger.error("Failed to retrieve domain controllers");
		}

		return domainControllers;
	}


	@Override
	public String getLoginServiceRegex(){
		StringBuilder builder = new StringBuilder(loginServiceNameRegex);
		boolean isFirst = true;
		if(!StringUtils.isEmpty(loginServiceNameRegex)){
			isFirst = false;
		}
		for(String server: getDomainControllers()){
			if(isFirst){
				isFirst = false;
			} else{
				builder.append("|");
			}
			builder.append(".*").append(server).append(".*");
		}
		return builder.toString();
	}

	@Override
	public String getLoginAccountNameRegex(){
		return loginAccountNameRegex;
	}

}
