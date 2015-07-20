package fortscale.streaming.service.aggregation.entity.event;

import fortscale.utils.ConversionUtils;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.*;

public class JokerFunction {
	private static final Logger logger = Logger.getLogger(JokerFunction.class);

	private static final String PARAMS_FIELD = "params";
	private static final String CLUSTERS_PARAMS_FIELD = "clusters";
	private static final String ALPHAS_MAP_PARAMS_FIELD = "alphasMap";
	private static final String BETAS_MAP_PARAMS_FIELD = "betasMap";

	public double calculateEntityEventValue(EntityEventConf entityEventConf, Map<String, AggrFeatureEventWrapper> aggrFeatureEventsMap) {
		Assert.notNull(entityEventConf, "Must accept an entity event configuration");
		Assert.notNull(aggrFeatureEventsMap, "Must accept an aggregated feature events map");

		JSONObject entityEventFunction = entityEventConf.getEntityEventFunction();
		Assert.isInstanceOf(JSONObject.class, entityEventFunction.get(PARAMS_FIELD));
		JSONObject params = (JSONObject)entityEventFunction.get(PARAMS_FIELD);

		Map<String, Double> clusterNameToMaxScoreMap = getClusterNameToMaxScoreMap(params, aggrFeatureEventsMap);
		Map<String, Double> alphasMap = getAlphasMap(params);
		Map<String, Double> betasMap = getBetasMap(params);
		return calculateEntityEventValue(aggrFeatureEventsMap, clusterNameToMaxScoreMap, alphasMap, betasMap);
	}

	private Map<String, Set<AggrFeatureEventWrapper>> getClustersMap(JSONObject params, Map<String, AggrFeatureEventWrapper> aggrFeatureEventsMap) {
		Assert.isInstanceOf(JSONObject.class, params.get(CLUSTERS_PARAMS_FIELD));
		JSONObject clustersJson = (JSONObject)params.get(CLUSTERS_PARAMS_FIELD);

		Map<String, Set<AggrFeatureEventWrapper>> clustersMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : clustersJson.entrySet()) {
			Assert.hasText(entry.getKey());
			Assert.isInstanceOf(JSONArray.class, entry.getValue());

			Set<AggrFeatureEventWrapper> aggrFeatureEvents = new HashSet<>();
			for (Object jsonArrayObject : (JSONArray)entry.getValue()) {
				Assert.isInstanceOf(String.class, jsonArrayObject);
				String aggrFeatureEventName = (String)jsonArrayObject;
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

	private Map<String, Double> getClusterNameToMaxScoreMap(JSONObject params, Map<String, AggrFeatureEventWrapper> aggrFeatureEventsMap) {
		Map<String, Set<AggrFeatureEventWrapper>> clustersMap = getClustersMap(params, aggrFeatureEventsMap);
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

	private Map<String, Double> getAlphasMap(JSONObject params) {
		Assert.isInstanceOf(JSONObject.class, params.get(ALPHAS_MAP_PARAMS_FIELD));
		JSONObject alphasJson = (JSONObject)params.get(ALPHAS_MAP_PARAMS_FIELD);

		Map<String, Double> alphasMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : alphasJson.entrySet()) {
			Assert.hasText(entry.getKey());
			Double alpha = ConversionUtils.convertToDouble(entry.getValue());
			Assert.notNull(alpha);
			alphasMap.put(entry.getKey(), alpha);
		}

		return alphasMap;
	}

	private Map<String, Double> getBetasMap(JSONObject params) {
		Assert.isInstanceOf(JSONObject.class, params.get(BETAS_MAP_PARAMS_FIELD));
		JSONObject betasJson = (JSONObject)params.get(BETAS_MAP_PARAMS_FIELD);

		Map<String, Double> betasMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : betasJson.entrySet()) {
			Assert.hasText(entry.getKey());
			Double beta = ConversionUtils.convertToDouble(entry.getValue());
			Assert.notNull(beta);
			betasMap.put(entry.getKey(), beta);
		}

		return betasMap;
	}

	private double calculateEntityEventValue(
			Map<String, AggrFeatureEventWrapper> aggrFeatureEventsMap,
			Map<String, Double> clusterNameToMaxScoreMap,
			Map<String, Double> alphasMap,
			Map<String, Double> betasMap) {

		double maxScoresSum = 0;
		for (Map.Entry<String, Double> entry : clusterNameToMaxScoreMap.entrySet()) {
			String clusterName = entry.getKey();
			Double maxScore = entry.getValue();
			if (maxScore != null) {
				Double alpha = alphasMap.get(clusterName);
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
					Double beta = betasMap.get(pEventName);
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
