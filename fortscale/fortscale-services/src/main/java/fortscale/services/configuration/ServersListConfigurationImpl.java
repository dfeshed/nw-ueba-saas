package fortscale.services.configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fortscale.domain.system.DcConfiguration;
import fortscale.domain.system.DcSystemConfiguraion;
import fortscale.domain.system.SystemConfiguration;
import fortscale.domain.system.SystemConfigurationEnum;
import fortscale.domain.system.dao.SystemConfigurationRepository;




@Service("ServersListConfiguration")
public class ServersListConfigurationImpl implements ServersListConfiguration {

	private static Logger logger = LoggerFactory.getLogger(ServersListConfigurationImpl.class);
	
	@Autowired
	private SystemConfigurationRepository systemConfigurationRepository;
	
	@Value("${fortscale.home.dir}/fortscale-scripts/scripts/getDCs.sh")
	private String getDCsScriptPath;
	
	@Value("${login.service.name.regex:}")
	private String loginServiceNameRegex;
	
	@Value("${login.account.name.regex:}")
	private String loginAccountNameRegex;
	
	@Override
	public List<String> getDCs(){
		ProcessBuilder processBuilder = null;
		Process pr = null;	
		BufferedReader reader = null;
		List<String> dcs = new ArrayList<>();
		String commands[] = {getDCsScriptPath, "short"};
		boolean retrieveFromDB = false;
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
			
			if(pr.waitFor() != 0){
				retrieveFromDB = true;
				try {
					BufferedReader stdError = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
					StringBuilder builder = new StringBuilder();
					String s = null;
					while ((s = stdError.readLine()) != null) {
						builder.append(s);
					}
					logger.error("got the following error while running the shell command {}. {}.",StringUtils.join(commands, " "), builder.toString());
				} catch (Exception e) {
					logger.error("got an exception while trying to read std error", e);
				}
			} else{
				if(dcs.isEmpty()){
					logger.warn("no dcs were recieved to the command: {}", StringUtils.join(commands, " "));
					retrieveFromDB = true;
				} else{
					SystemConfiguration systemConfiguration = systemConfigurationRepository.findByType(SystemConfigurationEnum.dc.getId());
					if(systemConfiguration == null){
						systemConfiguration = new SystemConfiguration();
						systemConfiguration.setType(SystemConfigurationEnum.dc.getId());
					}
					DcConfiguration dcConfiguration = new DcConfiguration();
					dcConfiguration.setDcs(dcs);
					systemConfiguration.setConf(dcConfiguration);
					systemConfigurationRepository.save(systemConfiguration);
				}
			}

		} catch (Exception e) {
			logger.error(String.format("while running the command \"%s\", got the following exception", getDCsScriptPath), e);
			retrieveFromDB = true;
		}
		
		if(retrieveFromDB){
			DcSystemConfiguraion dcSystemConfiguraion = systemConfigurationRepository.findDcConfiguration();
			if(dcSystemConfiguraion != null){
				dcs = dcSystemConfiguraion.getConf().getDcs();
			}
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
