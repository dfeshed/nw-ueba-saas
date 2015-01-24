package fortscale.streaming.scorer;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.streaming.ConfigUtils.getConfigStringList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.samza.config.Config;

public class EventScorer implements Scorer {
	
	private EventScorerConfig eventScorerConfig;
	private List<Scorer> scorers = new ArrayList<>();
	
	public EventScorer(String scoreName, Config config){
		List<String> scorers = getConfigStringList(config, String.format("fortscale.score.%s.scorers",scoreName));
		eventScorerConfig = new EventScorerConfig(scoreName, scorers);
	}

	@Override
	public void afterPropertiesSet(Map<String, Scorer> scorerMap) {
		for(String scorerName: eventScorerConfig.scorers){
			Scorer scorer = scorerMap.get(scorerName);
			checkNotNull(scorer);
			scorers.add(scorer);
		}

	}

	@Override
	public Double calculateScore(EventMessage eventMessage) throws Exception {
		double eventScore = 0;
		for(Scorer scorer: scorers) {
			Double score = scorer.calculateScore(eventMessage);
			if(score != null){
				eventScore = Math.max(eventScore, score);
			}
		}
		
		eventMessage.setScore(eventScorerConfig.getScoreName(), eventScore);
		
		return eventScore;
	}

}
