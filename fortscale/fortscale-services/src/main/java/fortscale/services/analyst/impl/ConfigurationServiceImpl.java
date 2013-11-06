package fortscale.services.analyst.impl;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.FortscaleConfiguration;
import fortscale.domain.analyst.ScoreConfiguration;
import fortscale.domain.analyst.ScoreWeight;
import fortscale.domain.analyst.dao.FortscaleConfigurationRepository;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.fe.Classifier;


@Service("configurationService")
public class ConfigurationServiceImpl implements ConfigurationService, InitializingBean{
	
	@Autowired
	private FortscaleConfigurationRepository fortscaleConfigurationRepository;

	@Override
	public ScoreConfiguration getScoreConfiguration() {
		Pageable pageable = new PageRequest(0, 1, Direction.DESC, FortscaleConfiguration.CREATED_AT_FIELD_NAME);
		List<FortscaleConfiguration> fortscaleConfigurations = fortscaleConfigurationRepository.findByConfigId("score", pageable);
		if(fortscaleConfigurations == null || fortscaleConfigurations.size() == 0){
			return null;
		}
		return fortscaleConfigurations.get(0).getConfObj();
	}

	@Override
	public void setScoreConfiguration(ScoreConfiguration scoreConfiguration,
			Analyst createdBy) {
		setScoreConfiguration(scoreConfiguration, createdBy.getId(), createdBy.getUserName());
	}
	
	@Override
	public void setScoreConfiguration(ScoreConfiguration scoreConfiguration, String createById, String createdByUsername) {
		FortscaleConfiguration fortscaleConfiguration = new FortscaleConfiguration("score");
		fortscaleConfiguration.setConfObj(scoreConfiguration);
		fortscaleConfiguration.setCreatedById(createById);
		fortscaleConfiguration.setCreatedByUsername(createdByUsername);
		fortscaleConfigurationRepository.save(fortscaleConfiguration);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(fortscaleConfigurationRepository.count() == 0){
			ScoreConfiguration scoreConfiguration = new ScoreConfiguration();
			scoreConfiguration.addScoreWeight(new ScoreWeight(Classifier.auth.getId(), 10));
			scoreConfiguration.addScoreWeight(new ScoreWeight(Classifier.vpn.getId(), 10));
			scoreConfiguration.addScoreWeight(new ScoreWeight(Classifier.groups.getId(), 10));
			setScoreConfiguration(scoreConfiguration, null, "Server");
//			scoreConfiguration = new ScoreConfiguration();
//			scoreConfiguration.addScoreWeight(new ScoreWeight(Classifier.auth.getId(), 20));
//			scoreConfiguration.addScoreWeight(new ScoreWeight(Classifier.vpn.getId(), 10));
//			scoreConfiguration.addScoreWeight(new ScoreWeight(Classifier.groups.getId(), 10));
//			setScoreConfiguration(scoreConfiguration, null, "Server");
		}
		
	}

	

	
}
