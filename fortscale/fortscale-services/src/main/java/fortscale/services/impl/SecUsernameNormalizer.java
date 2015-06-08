package fortscale.services.impl;

import fortscale.utils.ConfigurationUtils;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import parquet.org.slf4j.Logger;
import parquet.org.slf4j.LoggerFactory;

public class SecUsernameNormalizer extends UsernameNormalizer {

	private static Logger logger = LoggerFactory.getLogger(UsernameNormalizer.class);

	private String matchersString;
	private RegexMatcher regexMatcher;

	public String getMatchersString() {
		return matchersString;
	}

	public void setMatchersString(String matchersString) {
		this.matchersString = matchersString;
	}

	@Override
	public String normalize(String username, String domain, String classifier, boolean updateOnly) {
		String ret = null;
		username = username.toLowerCase();
		domain = domain.toLowerCase();
		logger.debug("Normalizing user - {}", username);
		if(regexMatcher != null){
			logger.debug("Attempting to match regular expressions");
			//get all matching regular expressions
			for(String normalizedUsername: regexMatcher.match(username)){
				//if username found, return it
				if(usernameService.isUsernameExist(normalizedUsername)){
					ret = normalizedUsername;
					logger.debug("One user found - {}", ret);
					return ret;
				}
			}
		}
		//no user was found or no matching regular expressions were found (most likely user is without @domain.com) -
		//return the user with the account_domain value
		logger.debug("No users found, trying to match user with domain - {}", domain);
		if(usernameService.isUsernameExist(username + "@" + domain)){
			ret = username + "@" + domain;
			logger.debug("One user found - {}", ret);
		}
		return ret;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(!StringUtils.isEmpty(matchersString)){
			String[][] matchersArray = ConfigurationUtils.getStringArrays(matchersString);
			regexMatcher = new RegexMatcher(matchersArray);
		}
	}

}