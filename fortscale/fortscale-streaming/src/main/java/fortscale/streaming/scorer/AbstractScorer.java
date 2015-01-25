package fortscale.streaming.scorer;

import static fortscale.streaming.ConfigUtils.getConfigString;

import java.util.Map;

import org.apache.samza.config.Config;

public abstract class AbstractScorer implements Scorer{
	protected String scorerName;
	protected String outputFieldName;
	
	public AbstractScorer(String scorerName, Config config){
		this.scorerName = scorerName;
		outputFieldName = getConfigString(config, String.format("fortscale.score.%s.output.field.name", scorerName));
	}
	
	@Override
	public void afterPropertiesSet(Map<String, Scorer> scorerMap) {}
}
