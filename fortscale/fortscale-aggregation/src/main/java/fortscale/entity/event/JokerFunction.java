package fortscale.entity.event;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.tuple.ImmutablePair;
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

		Assert.notNull(clusters);
		for (Map.Entry<String, List<String>> entry : clusters.entrySet()) {
			Assert.hasText(entry.getKey());
			Assert.notNull(entry.getValue());
			for (String f : entry.getValue()) {
				Assert.hasText(f);
			}
		}

		Assert.notNull(alphas);
		for (Map.Entry<String, Double> entry : alphas.entrySet()) {
			Assert.hasText(entry.getKey());
			Double alpha = entry.getValue();
			Assert.isTrue(!alpha.isInfinite() && !alpha.isNaN());
		}

		Assert.notNull(betas);
		for (Map.Entry<String, Double> entry : betas.entrySet()) {
			Assert.hasText(entry.getKey());
			Double beta = entry.getValue();
			Assert.isTrue(!beta.isInfinite() && !beta.isNaN());
		}

		this.clusters = clusters;
		this.alphas = alphas;
		this.betas = betas;
	}

	public double calculateEntityEventValue(Map<String, JokerAggrEventData> aggrFeatureEventsMap) {
		Assert.notNull(aggrFeatureEventsMap, "Must accept an aggregated feature events map");

		Map<String, Double> clusterNameToMaxScoreMap = getClusterNameToMaxScoreMap(aggrFeatureEventsMap);
		return calculateEntityEventValue(aggrFeatureEventsMap, clusterNameToMaxScoreMap);
	}

	private Map<String, Set<JokerAggrEventData>> getClustersMap(Map<String, JokerAggrEventData> aggrFeatureEventsMap) {
		Map<String, Set<JokerAggrEventData>> clustersMap = new HashMap<>();
		for (Map.Entry<String, List<String>> entry : clusters.entrySet()) {
			Set<JokerAggrEventData> aggrFeatureEvents = new HashSet<>();
			for (String aggrFeatureEventName : entry.getValue()) {
				JokerAggrEventData aggrFeatureEvent = aggrFeatureEventsMap.get(aggrFeatureEventName);
				if (aggrFeatureEvent != null) {
					if (aggrFeatureEvent.getScore() == null) {
						String errorMsg = String.format("Event %s must contain a score field", aggrFeatureEventName);
						logger.error(errorMsg);
						throw new IllegalArgumentException(errorMsg);
					}
					aggrFeatureEvents.add(aggrFeatureEvent);
				}
			}

			clustersMap.put(entry.getKey(), aggrFeatureEvents);
		}

		return clustersMap;
	}

	private Map<String, Double> getClusterNameToMaxScoreMap(Map<String, JokerAggrEventData> aggrFeatureEventsMap) {
		Map<String, Set<JokerAggrEventData>> clustersMap = getClustersMap(aggrFeatureEventsMap);
		Map<String, Double> clusterNameToMaxScoreMap = new HashMap<>();

		for (Map.Entry<String, Set<JokerAggrEventData>> entry : clustersMap.entrySet()) {
			Set<JokerAggrEventData> aggrFeatureEvents = entry.getValue();
			Double maxScore = null;

			if (!aggrFeatureEvents.isEmpty()) {
				JokerAggrEventData fWithMaxScore = Collections.max(aggrFeatureEvents, new Comparator<JokerAggrEventData>() {
					@Override
					public int compare(JokerAggrEventData aggrFeatureEvent1, JokerAggrEventData aggrFeatureEvent2) {
						return Double.compare(aggrFeatureEvent1.getScore(), aggrFeatureEvent2.getScore());
					}
				});
				maxScore = fWithMaxScore.getScore() / 100;
			}

			clusterNameToMaxScoreMap.put(entry.getKey(), maxScore);
		}

		return clusterNameToMaxScoreMap;
	}

	private double calculateEntityEventValue(
			Map<String, JokerAggrEventData> aggrFeatureEventsMap,
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
				}
				maxScoresSum += alpha * maxScore;
			}
		}

		double pValuesSum = betas.entrySet().stream()
				// pair each P to its beta
				.map(pNameAndBeta -> new ImmutablePair<>(aggrFeatureEventsMap.get(pNameAndBeta.getKey()), pNameAndBeta.getValue()))
				// discard betas which don't have corresponding Ps
				.filter(aggrFeatureEventAndBeta -> aggrFeatureEventAndBeta.getLeft() != null)
				// multiply each P's value by the corresponding beta
				.mapToDouble(aggrFeatureEventAndBeta -> aggrFeatureEventAndBeta.getLeft().getAggregatedFeatureValue() * aggrFeatureEventAndBeta.getRight())
				.sum();

		return maxScoresSum + pValuesSum;
	}
}
