package fortscale.services.impl;

import fortscale.utils.ConfigurationUtils;
import org.apache.commons.lang.StringUtils;

public class SecUsernameNormalizer extends UsernameNormalizer {

	private String matchersString;
	private RegexMatcher regexMatcher;

	public String getMatchersString() {
		return matchersString;
	}

	public void setMatchersString(String matchersString) {
		this.matchersString = matchersString;
	}

	@Override
	public String normalize(String username, String domain){
		username = username.toLowerCase();
		domain = domain.toLowerCase();
		String ret = null;
		if(regexMatcher != null){
			//get all matching regular expressions
			for(String normalizedUsername: regexMatcher.match(username)){
				//if username found, return it
				if(usernameService.isUsernameExist(normalizedUsername)){
					ret = normalizedUsername;
					break;
				}
			}
		}
		//no user was found or no matching regular expressions were found (most likely user is without @domain.com) -
		//return the user with the account_domain valuea
		if(ret == null && usernameService.isUsernameExist(username + "@" + domain)){
			ret = username + "@" + domain;
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