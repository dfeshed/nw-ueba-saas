package fortscale.services.analyst.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.Range;
import org.mortbay.log.Log;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import fortscale.services.exceptions.InvalidValueException;
import fortscale.services.fe.Classifier;
import fortscale.services.impl.SeverityElement;
import fortscale.utils.logging.Logger;


@Service("configurationService")
public class ConfigurationServiceImpl implements ConfigurationService, InitializingBean{
	private static Logger logger = Logger.getLogger(ConfigurationServiceImpl.class);
	
	private List<SeverityElement> severityOrderedList;
	
	private Map<String, Classifier> classifiersMap;
	
	@Value("${fortscale.home.dir}/fortscale-scripts/scripts/getDCs.sh")
	private String getDCsScriptPath;
	
	@Value("${score.distribution:Critical:95,High:80,Medium:50,Low:0}")
	private String scoreDistribution;
	
	@Value("${login.service.name.regex:}")
	private String loginServiceNameRegex;
	
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
	public Range getRange(String severityId){
		if(severityId == null){
			return new IntRange(0, 101);
		}
		int i = 0;
		for(SeverityElement element: getSeverityElements()){
			if(element.getName().equals(severityId)){
				break;
			}
			i++;
		}
		if(getSeverityElements().size() == i){
			throw new InvalidValueException(String.format("no such severity id: %s", severityId));
		}
		int lowestVal = getSeverityElements().get(i).getValue();
		int upperVal = 101;
		if(i > 0){
			upperVal = getSeverityElements().get(i-1).getValue();
		}
		
		return new IntRange(lowestVal, upperVal);
	}
	
	@Override
	public Map<String, Classifier> getClassifiersMap(){
		return classifiersMap;
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
	
	@Override
	public void setScoreDistribution(List<SeverityElement> severityList){
		Collections.sort(severityList, new SeverityElement.OrderByValueDesc());
		severityOrderedList = severityList;
		this.scoreDistribution = null;
	}
	
	@Override
	public List<String> getDCs(){
		ProcessBuilder processBuilder = null;
		Process pr = null;	
		BufferedReader reader = null;
		List<String> dcs = new ArrayList<>();
		String commands[] = {getDCsScriptPath, "short"};
		try {
			processBuilder = new ProcessBuilder(commands);

			pr = processBuilder.start();
			
			 reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			 
			 String line = null;
			while ((line = reader.readLine()) != null) {
				if(!StringUtils.isEmpty(line)){
					dcs.add(line);
				}
			}

		} catch (Exception e) {
			logger.error(String.format("while running the command \"%s\", got the following exception", getDCsScriptPath), e);
			return Collections.emptyList();
		}
		
		if(dcs.isEmpty()){
			Log.warn("no dcs were recieved to the command: {}", StringUtils.join(commands, " "));
		}
		
		return dcs;
	}
	
	@Override
	public String getLoginServiceRegex(){
		StringBuilder builder = new StringBuilder(loginServiceNameRegex);
		boolean isFirst = true;
		if(!StringUtils.isEmpty(loginServiceNameRegex)){
			isFirst = false;
		}
		for(String server: getDCs()){
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
