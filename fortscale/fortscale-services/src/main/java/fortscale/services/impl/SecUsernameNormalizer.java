package fortscale.services.impl;

import fortscale.utils.ConfigurationUtils;
import org.apache.commons.lang.StringUtils;

public class SecUsernameNormalizer extends UsernameNormalizer {

	private String matchersString;
	private RegexMatcher regexMatcher;

	/*public void setMatchersString(String matchersString) {
		this.matchersString = matchersString;
	}*/

	@Override
	public String normalize(String username, String domain){
		username = username.toLowerCase();
		domain = domain.toLowerCase();
		String ret = null;
		if(regexMatcher != null){
			for(String normalizedUsername: regexMatcher.match(username)){
				if(usernameService.isUsernameExist(normalizedUsername)){
					ret = normalizedUsername;
					break;
				}
			}
		}
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