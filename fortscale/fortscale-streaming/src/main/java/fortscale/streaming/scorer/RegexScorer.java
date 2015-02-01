package fortscale.streaming.scorer;

import static fortscale.streaming.ConfigUtils.getConfigString;

import java.util.regex.Pattern;

import org.apache.samza.config.Config;

public abstract class RegexScorer extends AbstractScorer {

	protected Pattern regexPattern;
	private String featureFieldName;
	
	public RegexScorer(String scorerName, Config config){
		super(scorerName,config);
		featureFieldName = getConfigString(config, String.format("fortscale.score.%s.regex.fieldname", scorerName));
		String regex = getConfigString(config, String.format("fortscale.score.%s.regex", scorerName));
		this.regexPattern = Pattern.compile(regex);
	}
	
	protected boolean matches(EventMessage eventMessage){
		String value = (String) eventMessage.getJsonObject().get(featureFieldName);
		return regexPattern.matcher(value).matches();
	}

}
