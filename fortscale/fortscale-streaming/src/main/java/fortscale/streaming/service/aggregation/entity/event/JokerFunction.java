package fortscale.streaming.service.aggregation.entity.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.utils.logging.Logger;
import org.springframework.util.Assert;

import java.util.*;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class JokerFunction {
	private static final Logger logger = Logger.getLogger(JokerFunction.class);

	private Map<String, List<String>> clusters;
	private Map<String, Double> alphas;
	private Map<String, Double> betas;

	public JokerFunction(
			@JsonProperty("clusters") Map<String, List<String>> clusters,
			@JsonProperty("alphas") Map<String, Double> alphas,
			@JsonProperty("betas") Map<String, Double> betas) {

		// TODO: Validate input
		this.clusters = clusters;
		this.alphas = alphas;
		this.betas = betas;
	}

	public double calculateEntityEventValue(Map<String, AggrFeatureEventWrapper> aggrFeatureEventsMap) {
		Assert.notNull(aggrFeatureEventsMap, "Must accept an aggregated feature events map");

		Map<String, Double> clusterNameToMaxScoreMap = getClusterNameToMaxScoreMap(aggrFeatureEventsMap);
		return calculateEntityEventValue(aggrFeatureEventsMap, clusterNameToMaxScoreMap);
	}

	private Map<String, Set<AggrFeatureEventWrapper>> getClustersMap(Map<String, AggrFeatureEventWrapper> aggrFeatureEventsMap) {
		Map<String, Set<AggrFeatureEventWrapper>> clustersMap = new HashMap<>();
		for (Map.Entry<String, List<String>> entry : clusters.entrySet()) {
			Set<AggrFeatureEventWrapper> aggrFeatureEvents = new HashSet<>();
			for (String aggrFeatureEventName : entry.getValue()) {
				AggrFeatureEventWrapper aggrFeatureEvent = aggrFeatureEventsMap.get(aggrFeatureEventName);
				if (aggrFeatureEvent != null) {
					if (!aggrFeatureEvent.isOfTypeF() || aggrFeatureEvent.getScore() == null) {
						String errorMsg = String.format("Event %s must be of type F and contain a score field", aggrFeatureEventName);
						logger.error(errorMsg);
						throw new IllegalArgumentException(errorMsg);
					} else {
						aggrFeatureEvents.add(aggrFeatureEvent);
					}
				}
			}

			clustersMap.put(entry.getKey(), aggrFeatureEvents);
		}

		return clustersMap;
	}

	private Map<String, Double> getClusterNameToMaxScoreMap(Map<String, AggrFeatureEventWrapper> aggrFeatureEventsMap) {
		Map<String, Set<AggrFeatureEventWrapper>> clustersMap = getClustersMap(aggrFeatureEventsMap);
		Map<String, Double> clusterNameToMaxScoreMap = new HashMap<>();

		for (Map.Entry<String, Set<AggrFeatureEventWrapper>> entry : clustersMap.entrySet()) {
			Set<AggrFeatureEventWrapper> aggrFeatureEvents = entry.getValue();
			Double maxScore = null;

			if (!aggrFeatureEvents.isEmpty()) {
				AggrFeatureEventWrapper fWithMaxScore = Collections.max(aggrFeatureEvents, new Comparator<AggrFeatureEventWrapper>() {
					@Override
					public int compare(AggrFeatureEventWrapper aggrFeatureEvent1, AggrFeatureEventWrapper aggrFeatureEvent2) {
						return Double.compare(aggrFeatureEvent1.getScore(), aggrFeatureEvent2.getScore());
					}
				});
				maxScore = fWithMaxScore.getScore();
			}

			clusterNameToMaxScoreMap.put(entry.getKey(), maxScore);
		}

		return clusterNameToMaxScoreMap;
	}

	private double calculateEntityEventValue(
			Map<String, AggrFeatureEventWrapper> aggrFeatureEventsMap,
			Map<String, Double> clusterNameToMaxScoreMap) {

		double maxScoresSum = 0;
		for (Map.Entry<String, Double> entry : clusterNameToMaxScoreMap.entrySet()) {
			String clusterName = entry.getKey();
			Double maxScore = entry.getValue();
			if (maxScore != null) {
				Double alpha = alphas.get(clusterName);
				if (alpha == null) {
					String errorMsg = String.format("Missing alpha for cluster %s", clusterName);
					logger.error(errorMsg);
					throw new IllegalArgumentException(errorMsg);
				} else {
					maxScoresSum += alpha * maxScore;
				}
			}
		}

		double pValuesSum = 0;
		for (Map.Entry<String, AggrFeatureEventWrapper> entry : aggrFeatureEventsMap.entrySet()) {
			AggrFeatureEventWrapper aggrFeatureEvent = entry.getValue();
			if (aggrFeatureEvent.isOfTypeP()) {
				String pEventName = entry.getKey();
				Double pValue = aggrFeatureEvent.getValue();
				if (pValue == null) {
					String errorMsg = String.format("Event %s of type P must have a value field", pEventName);
					logger.error(errorMsg);
					throw new IllegalArgumentException(errorMsg);
				} else {
					Double beta = betas.get(pEventName);
					if (beta == null) {
						String errorMsg = String.format("Missing beta for P event %s", pEventName);
						logger.error(errorMsg);
						throw new IllegalArgumentException(errorMsg);
					} else {
						pValuesSum += beta * pValue;
					}
				}
			}
		}

		return maxScoresSum + pValuesSum;
	}
}
