package fortscale.streaming.scorer;

import java.util.Map;


public interface Scorer {
	public void afterPropertiesSet(Map<String, Scorer> scorerMap);
	public Double calculateScore(EventMessage eventMessage) throws Exception;
}
