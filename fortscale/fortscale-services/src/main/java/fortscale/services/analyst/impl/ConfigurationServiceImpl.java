package fortscale.services.analyst.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import fortscale.services.impl.SeverityElement;


@Service("configurationService")
public class ConfigurationServiceImpl implements ConfigurationService, InitializingBean{
	
	private static List<SeverityElement> severityOrderedList = getSeverityList();
//	private static Map<String,SeverityElement> severityMap = null;
	
	private Map<String, Classifier> classifiersMap = createClassifiersMap();
	
	private static Map<String, Classifier> createClassifiersMap(){
		Map<String, Classifier> ret = new HashMap<String, Classifier>();
		for(Classifier classifier: Classifier.values()){
			ret.put(classifier.getId(), classifier);
		}
		return ret;
	}
		
	private static List<SeverityElement> getSeverityList(){
		List<SeverityElement> ret = new ArrayList<>();
		ret.add(new SeverityElement("Critical", 90));
		ret.add(new SeverityElement("High", 50));
		ret.add(new SeverityElement("Medium", 10));
		ret.add(new SeverityElement("Low", 0));
		return ret;
	}
	
//	private static Map<String,SeverityElement> getSeverityMap(){
//		if(severityMap == null){
//			Map<String,SeverityElement> tmp = new HashMap<>();
//			for(SeverityElement element: severityOrderedList){
//				tmp.put(element.getName(), element);
//			}
//			severityMap = tmp;
//		}
//		
//		return severityMap;
//	}
	
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
	public void setScoreConfiguration(ScoreConfiguration scoreConfiguration,
			Analyst createdBy) {
		setScoreConfiguration(scoreConfiguration, createdBy.getId(), createdBy.getUserName());
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
			scoreConfiguration.addScoreWeight(new ScoreWeight(Classifier.groups.getId(), 10));
			setScoreConfiguration(scoreConfiguration, "Server", "Server");
//			scoreConfiguration = new ScoreConfiguration();
//			scoreConfiguration.addScoreWeight(new ScoreWeight(Classifier.auth.getId(), 20));
//			scoreConfiguration.addScoreWeight(new ScoreWeight(Classifier.vpn.getId(), 10));
//			scoreConfiguration.addScoreWeight(new ScoreWeight(Classifier.groups.getId(), 10));
//			setScoreConfiguration(scoreConfiguration, "Server", "Server");
		}
		
	}

	
	@Override
	public List<SeverityElement> getSeverityElements() {
		return severityOrderedList;
	}
	
	@Override
	public Map<String, Classifier> getClassifiersMap(){
		return classifiersMap;
	}
}
