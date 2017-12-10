package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.cache.metrics.ModelCacheMetricsContainer;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.config.AdeEventTypeScorerConfs;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.metrics.ScoringServiceMetricsContainer;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.AdeRecordReader;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoringService {
	private static final Logger logger = Logger.getLogger(ScoringService.class);

	private ScorerConfService scorerConfService;
	private FactoryService<Scorer> scorerFactoryService;
	private Map<String, List<Scorer>> adeEventTypeToScorersMap;
	private ModelsCacheService modelCacheService;
	private ScoringServiceMetricsContainer scoringServiceMetricsContainer;
	private ModelCacheMetricsContainer modelCacheMetricsContainer;
	public ScoringService(
			ScorerConfService scorerConfService,
			FactoryService<Scorer> scorerFactoryService,
			ModelsCacheService modelCacheService, ScoringServiceMetricsContainer scoringServiceMetricsContainer) {

		this.scorerConfService = scorerConfService;
		this.scorerFactoryService = scorerFactoryService;
		this.modelCacheService = modelCacheService;
		this.adeEventTypeToScorersMap = new HashMap<>();
		this.scoringServiceMetricsContainer = scoringServiceMetricsContainer;
		loadScorers();
	}

	public List<FeatureScore> score(AdeRecordReader adeRecordReader, TimeRange timeRange) {


		String adeEventType = adeRecordReader.getAdeEventType();
		//todo: dataSourceMetrics.calculateScoreTime = adeRecordReader.getDate_time().getEpochSecond();
		List<Scorer> adeEventTypeScorers = adeEventTypeToScorersMap.get(adeEventType);
		Instant startInstant = timeRange.getStart();
		modelCacheMetricsContainer.setLogicalStartTime(startInstant);
		if (adeEventTypeScorers == null || adeEventTypeScorers.isEmpty()) {
			//todo: dataSourceMetrics.dataSourceScorerNotFound++;
			logger.error("No defined scorers for ade event type {}. ADE record reader: {}.", adeEventType, adeRecordReader);
			return null;
		}


		List<FeatureScore> list = new ArrayList<>();
		for (Scorer adeEventTypeScorer : adeEventTypeScorers) {
			FeatureScore featureScore = adeEventTypeScorer.calculateScore(adeRecordReader);
			scoringServiceMetricsContainer.updateMetric(startInstant,adeEventType,featureScore);
			list.add(featureScore);
		}
		return list;
	}

	private void loadScorers() {
		scorerConfService.getAllAdeEventTypeScorerConfs().values().forEach(adeEventTypeScorerConfs -> {
			String adeEventType = adeEventTypeScorerConfs.getAdeEventType();
			List<Scorer> adeEventTypeScorers = loadAdeEventTypeScorers(adeEventTypeScorerConfs);
			adeEventTypeToScorersMap.put(adeEventType, adeEventTypeScorers);
			//todo: dataSourceMetrics.dataSourceScorers = dataSourceScorers.size();
		});
	}

	private List<Scorer> loadAdeEventTypeScorers(AdeEventTypeScorerConfs adeEventTypeScorerConfs) {
		List<IScorerConf> scorerConfs = adeEventTypeScorerConfs.getScorerConfs();
		List<Scorer> scorers = new ArrayList<>(scorerConfs.size());
		scorerConfs.forEach(scorerConf -> scorers.add(scorerFactoryService.getProduct(scorerConf)));
		return scorers;
	}

	/**
	 * Reset model cache
	 */
	public void resetModelCache(){
		modelCacheService.resetCache();
	}
}
