package fortscale.services.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;

public class UsernameNormalizer implements InitializingBean{
	private static final String MATCHER_ARRAY_SPLIT_STRING = "#####";
	private static final String MATCHER_SPLIT_STRING = "# # #";

	private String matchersString;
	private RegexMatcher regexMatcher;
	
	private UsernameService usernameService;
	
	
	
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
				if(usernameService.isUsernameExist(normalizedUsername, true)){
					ret = normalizedUsername;
				}
			}
		}
		
		return ret;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(!StringUtils.isEmpty(matchersString)){
			String matchersStringSplit[] = matchersString.split(MATCHER_ARRAY_SPLIT_STRING);
			String[][] matchersArray = new String[matchersStringSplit.length][2];
			for(int i = 0; i < matchersStringSplit.length; i++){
				String matcherSplit[] = matchersStringSplit[i].split(MATCHER_SPLIT_STRING);
				matchersArray[i][0] = matcherSplit[0];
				matchersArray[i][1] = matcherSplit[1];
			}
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
