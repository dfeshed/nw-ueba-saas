package fortscale.streaming.scorer;

import static fortscale.streaming.ConfigUtils.getConfigString;

import java.util.regex.Pattern;

import org.apache.samza.config.Config;

public abstract class RegexScorer extends AbstractScorer {

	protected Pattern regexPattern;
	private String featureFieldName;
	
	public RegexScorer(String scorerName, Config config, ScorerContext context){
		super(scorerName,config,context);
		featureFieldName = getConfigString(config, String.format("fortscale.score.%s.regex.fieldname", scorerName));
		String regex = getConfigString(config, String.format("fortscale.score.%s.regex", scorerName));
		this.regexPattern = Pattern.compile(regex);
	}
	
	protected boolean matches(EventMessage eventMessage){
		String value = (String) extractFeature(featureFieldName, eventMessage.getJsonObject());
		return regexPattern.matcher(value).matches();
	}

}
