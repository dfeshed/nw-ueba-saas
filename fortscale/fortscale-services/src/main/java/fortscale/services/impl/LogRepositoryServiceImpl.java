package fortscale.services.impl;

import fortscale.domain.fetch.LogRepository;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.LogRepositoryService;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amir Keren on 8/15/16.
 */
@Service("LogRepositoryService")
public class LogRepositoryServiceImpl implements LogRepositoryService, InitializingBean {

	private static Logger logger = Logger.getLogger(LogRepositoryServiceImpl.class);

	private final ApplicationConfigurationService applicationConfigurationService;

	@Value("${source.splunk.host:}")
	private String hostName;
	@Value("${source.splunk.port:0}")
	private int port;
	@Value("${source.splunk.user:}")
	private String username;
	@Value("${source.splunk.password:}")
	private String password;

	@Autowired
	public LogRepositoryServiceImpl(ApplicationConfigurationService applicationConfigurationService) {
		this.applicationConfigurationService = applicationConfigurationService;
	}

	@Override
	public List<LogRepository> getLogRepositoriesFromDatabase() {
		return applicationConfigurationService.getApplicationConfigurationAsObjects(LogRepository.LOG_REPOSITORY_KEY,
				LogRepository.class);
	}

	@Override
	public void saveLogRepositoriesInDatabase(List<LogRepository> logRepositories) {
		applicationConfigurationService.updateConfigItemAsObject(LogRepository.LOG_REPOSITORY_KEY, logRepositories);
	}

	@Override
	public String canConnect(LogRepository logRepository) {
		//TODO - implement
		return "";
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!applicationConfigurationService.isApplicationConfigurationExists(LogRepository.LOG_REPOSITORY_KEY)) {
			//initialize with default test values if no configuration key exists
			logger.warn("Log Repository configuration not found, trying to load configuration from properties");
			if (StringUtils.isBlank(hostName) || StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
				logger.error("Splunk default configuration not found");
				return;
			}
			List<LogRepository> logRepositories = new ArrayList<>();
			logRepositories.add(new LogRepository(LogRepository.DEFAULT_SIEM, hostName, username, password, port));
			applicationConfigurationService.insertConfigItemAsObject(LogRepository.LOG_REPOSITORY_KEY, logRepositories);
		}
	}

}