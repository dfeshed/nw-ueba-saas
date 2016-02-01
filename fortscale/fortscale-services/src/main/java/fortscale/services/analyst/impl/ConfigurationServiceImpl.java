package fortscale.services.analyst.impl;

import fortscale.domain.analyst.FortscaleConfiguration;
import fortscale.domain.analyst.ScoreConfiguration;
import fortscale.domain.analyst.ScoreWeight;
import fortscale.domain.analyst.dao.FortscaleConfigurationRepository;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.classifier.Classifier;
import fortscale.services.impl.SeverityElement;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("configurationService")
public class ConfigurationServiceImpl implements ConfigurationService, InitializingBean{
	
	private List<SeverityElement> severityOrderedList;
	
	private Map<String, Classifier> classifiersMap;

	@Value("${score.distribution:Critical:95,High:80,Medium:50,Low:0}")
	private String scoreDistribution;
	
	@Value("${login.account.name.regex:}")
	private String loginAccountNameRegex;
	
	@Autowired
	private FortscaleConfigurationRepository fortscaleConfigurationRepository;

	@Override
	public ScoreConfiguration getScoreConfiguration() {
		Pageable pageable = new PageRequest(0, 1, Direction.DESC, FortscaleConfiguration.LAST_MODIFIED_FIELD_NAME);
		List<FortscaleConfiguration> fortscaleConfigurations = fortscaleConfigurationRepository.findByConfigId(FortscaleConfigurationEnum.score.getId(), pageable);
		if(fortscaleConfigurations == null || fortscaleConfigurations.size() == 0){
			return null;
		}
		return fortscaleConfigurations.get(0).getConfObj();
	}
	
	@Override
	public void setScoreConfiguration(ScoreConfiguration scoreConfiguration, String createById, String createdByUsername) {
		FortscaleConfiguration fortscaleConfiguration = fortscaleConfigurationRepository.findByConfigIdAndCreatedById(FortscaleConfigurationEnum.score.getId(), createById);
		if(fortscaleConfiguration == null){
			fortscaleConfiguration = new FortscaleConfiguration(FortscaleConfigurationEnum.score.getId());
			fortscaleConfiguration.setCreatedById(createById);
		}
		fortscaleConfiguration.setCreatedByUsername(createdByUsername);
		fortscaleConfiguration.setConfObj(scoreConfiguration);
		
		fortscaleConfigurationRepository.save(fortscaleConfiguration);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(fortscaleConfigurationRepository.count() == 0){
			ScoreConfiguration scoreConfiguration = new ScoreConfiguration();
			scoreConfiguration.addScoreWeight(new ScoreWeight(Classifier.auth.getId(), 10));
			scoreConfiguration.addScoreWeight(new ScoreWeight(Classifier.vpn.getId(), 10));
			scoreConfiguration.addScoreWeight(new ScoreWeight(Classifier.ssh.getId(), 10));
			setScoreConfiguration(scoreConfiguration, "Server", "Server");
		}
		
		setScoreDistribution(scoreDistribution);

		classifiersMap = new HashMap<String, Classifier>();
		for(Classifier classifier: Classifier.values()){
			classifiersMap.put(classifier.getId(), classifier);
		}
	}

	
	@Override
	public List<SeverityElement> getSeverityElements() {
		return severityOrderedList;
	}

	@Override
	public void setScoreDistribution(String scoreDistribution) {
		List<SeverityElement> tmp = new ArrayList<>();
		for(String elem: scoreDistribution.split(",")){
			String elemSplit[] = elem.split(":");
			tmp.add(new SeverityElement(elemSplit[0], Integer.parseInt(elemSplit[1])));
		}
		Collections.sort(tmp, new SeverityElement.OrderByValueDesc());
		severityOrderedList = tmp;
		this.scoreDistribution = scoreDistribution;
	}
}
