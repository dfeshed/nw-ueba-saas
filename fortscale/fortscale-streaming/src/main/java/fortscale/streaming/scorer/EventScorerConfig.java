package fortscale.streaming.scorer;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class EventScorerConfig {
	private String scoreName;
	ImmutableList<String> scorers;
	
	public EventScorerConfig(String scoreName, List<String> scorers){
		this.scoreName = scoreName;
		this.scorers = ImmutableList.copyOf(scorers);
	}

	public ImmutableList<String> getScorers() {
		return scorers;
	}

	public String getScoreName() {
		return scoreName;
	}
}
