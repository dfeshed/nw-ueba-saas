package fortscale.services.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatcher {
	private Pattern[] patterns;
	private String [] replacements;
	
	public RegexMatcher(String[] ... regex){
		this.patterns = new Pattern[regex.length];
		this.replacements = new String[regex.length];
		for(int i = 0; i < regex.length; i++){
			patterns[i] = Pattern.compile(regex[i][0]);
			replacements[i] = regex[i][1];
		}
	}

	public String match(String val) {
		for(int i = 0; i < patterns.length; i++){
			Pattern p = patterns[i];
			Matcher m = p.matcher(val);
			if(m.matches()){
				return m.replaceAll(replacements[i]);
			}
		}
		return null;
	}

	
	public static void main(String args[]){
		String strs[] = {"yarondl@corp.test.fortscale.com", "yarondl@fortscale.com"};
		RegexMatcher matcher = new RegexMatcher(new String[][]{
				{"([\\S]+)@([^ \\t\\n\\x0B\\f\\r\\.]+)\\.([^ \\t\\n\\x0B\\f\\r\\.]+)", "$1@$2.$3"},
				{"([\\S]+)@[^ \\t\\n\\x0B\\f\\r]+\\.([^ \\t\\n\\x0B\\f\\r\\.]+)\\.([^ \\t\\n\\x0B\\f\\r\\.]+)", "$1@$2.$3"},
			});
		for(String s: strs){
			
		
			String s1 = matcher.match(s);
			System.out.println(s1);
		}
		
	}
}
