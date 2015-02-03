package fortscale.streaming.scorer;

import org.apache.samza.config.Config;

public class ContstantRegexScorer extends RegexScorer{
	
	private int constantScore;

	public ContstantRegexScorer(String scorerName, Config config) {
		super(scorerName, config);
		this.constantScore = config.getInt(String.format("fortscale.score.%s.constant", scorerName));
	}

	@Override
	public FeatureScore calculateScore(EventMessage eventMessage) throws Exception {
		FeatureScore ret = null;
		if(matches(eventMessage)){
			ret = new FeatureScore(outputFieldName, (double) constantScore);
		}
		
		return ret;
	}

}
