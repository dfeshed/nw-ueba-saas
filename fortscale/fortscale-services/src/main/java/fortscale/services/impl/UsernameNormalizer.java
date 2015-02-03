package fortscale.services.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import fortscale.utils.ConfigurationUtils;

public class UsernameNormalizer implements InitializingBean{

	private String matchersString;
	private RegexMatcher regexMatcher;
	
	private UsernameService usernameService;

	public UsernameService getUsernameService() {
		return usernameService;
	}

	public void setUsernameService(UsernameService usernameService) {
		this.usernameService = usernameService;
	}

	public void setMatchersString(String matchersString) {
		this.matchersString = matchersString;
	}
	
	public String normalize(String username){
		username = username.toLowerCase();
		String ret = null;
		if(regexMatcher != null){
			for(String normalizedUsername: regexMatcher.match(username)){
				if(usernameService.isUsernameExist(normalizedUsername)){
					ret = normalizedUsername;
					break;
				}
			}
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
	
	public static void main(String args[]) throws Exception{
		UsernameNormalizer usernameNormalizer = new UsernameNormalizer();
		String matchersString = "([\\S]+)@([^ \\t\\n\\x0B\\f\\r\\.]+)\\.([^ \\t\\n\\x0B\\f\\r\\.]+)# # #$1@$2.$3#####" +
				"([^ \\t\\n\\x0B\\f\\r\\@]+)# # #$1@fortscale.com#####" +
				"([\\S]+)@[^ \\t\\n\\x0B\\f\\r]+\\.([^ \\t\\n\\x0B\\f\\r\\.]+)\\.([^ \\t\\n\\x0B\\f\\r\\.]+)# # #$1@$2.$3";
		usernameNormalizer.setMatchersString(matchersString);
		usernameNormalizer.afterPropertiesSet();
		
		String strs[] = {"yarondl@corp.test.fortscale.com", "yarondl@fortscale.com", "yarondl", "yaron.delevie", "yarondl@com"};
		for(String s: strs){
			String s1 = usernameNormalizer.normalize(s);
			System.out.println(s1);
		}
		
	}
}
