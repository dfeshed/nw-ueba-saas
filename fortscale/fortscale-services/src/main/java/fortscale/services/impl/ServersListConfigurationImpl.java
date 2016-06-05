package fortscale.services.impl;

import fortscale.domain.system.dao.SystemConfigurationRepository;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ServersListConfiguration;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("ServersListConfiguration")
public class ServersListConfigurationImpl implements ServersListConfiguration {


	@Autowired
	private SystemConfigurationRepository systemConfigurationRepository;

	@Autowired
	private ActiveDirectoryService activeDirectoryService;

	@Value("${login.service.name.regex:}")
	private String loginServiceNameRegex;

	@Value("${login.account.name.regex:}")
	private String loginAccountNameRegex;




	@Override
	public String getLoginServiceRegex(){
		StringBuilder builder = new StringBuilder(loginServiceNameRegex);
		boolean isFirst = true;
		if(!StringUtils.isEmpty(loginServiceNameRegex)){
			isFirst = false;
		}
		for (String server : activeDirectoryService.getDomainControllers()) {
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
