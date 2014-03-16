package fortscale.utils.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fortscale.utils.logging.Logger;

@Service("ServersListConfiguration")
public class ServersListConfigurationImpl implements ServersListConfiguration {

	private static Logger logger = Logger.getLogger(ServersListConfigurationImpl.class);
	
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
