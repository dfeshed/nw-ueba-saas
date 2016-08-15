package fortscale.services.impl;

import fortscale.domain.fetch.LogRepository;
import fortscale.domain.fetch.SIEMType;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.LogRepositoryService;
import fortscale.utils.EncryptionUtils;
import fortscale.utils.qradar.QRadarAPI;
import fortscale.utils.splunk.SplunkApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Amir Keren on 8/15/16.
 */
@Service("LogRepositoryService")
public class LogRepositoryServiceImpl implements LogRepositoryService {

	private final ApplicationConfigurationService applicationConfigurationService;

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
		SIEMType type;
		try {
			type = SIEMType.valueOf(logRepository.getType().toUpperCase());
		} catch (Exception ex) {
			return "SIEM " + logRepository.getType() + " is not supported";
		}
		try {
			switch (type) {
				case SPLUNK: {
					new SplunkApi(logRepository.getHost(), logRepository.getPort(), logRepository.getUser(),
							EncryptionUtils.decrypt(logRepository.getPassword()));
					break;
				}
				case QRADAR: {
					return new QRadarAPI(logRepository.getHost(), EncryptionUtils.decrypt(logRepository.getPassword())).
							canConnect();
				}
			}
		} catch (Exception ex) {
			return ex.getLocalizedMessage();
		}
		return "";
	}

}