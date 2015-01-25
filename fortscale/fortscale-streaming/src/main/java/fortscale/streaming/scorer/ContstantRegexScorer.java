package fortscale.streaming.scorer;

import org.apache.samza.config.Config;

public class ContstantRegexScorer extends RegexScorer{
	
	private int constantScore;

	public ContstantRegexScorer(String scorerName, Config config) {
		super(scorerName, config);
		this.constantScore = config.getInt(String.format("fortscale.score.%s.constant", scorerName));
	}

	@Override
	public Double calculateScore(EventMessage eventMessage) throws Exception {
		if(matches(eventMessage)){
			return (double) constantScore;
		}
		return null;
	}

}
