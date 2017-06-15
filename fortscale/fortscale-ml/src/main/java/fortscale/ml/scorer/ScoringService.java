package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.scorer.config.DataSourceScorerConfs;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.metrics.ScoringServiceMetrics;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import presidio.ade.domain.record.AdeRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScoringService<T extends AdeRecord> {
	private static final Logger logger = Logger.getLogger(ScoringService.class);

	private ScorerConfService scorerConfService;
	private FactoryService<Scorer> scorerFactoryService;
	private StatsService statsService;
	private Map<String, List<Scorer>> dataSourceToScorersMap;
	private Map<String, ScoringServiceMetrics> dataSourceToMetricsMap;

	public ScoringService(
			ScorerConfService scorerConfService,
			FactoryService<Scorer> scorerFactoryService,
			StatsService statsService) {

		this.scorerConfService = scorerConfService;
		this.scorerFactoryService = scorerFactoryService;
		this.statsService = statsService;
		this.dataSourceToScorersMap = new HashMap<>();
		this.dataSourceToMetricsMap = new HashMap<>();
		loadScorers();
	}

	public List<FeatureScore> score(T record) {
		long dateTimeEpochSecond = record.getDate_time().getEpochSecond();
		String dataSource = record.getDataSource();
		List<Scorer> dataSourceScorers = dataSourceToScorersMap.get(dataSource);
		ScoringServiceMetrics dataSourceMetrics = getDataSourceMetrics(dataSource);
		dataSourceMetrics.calculateScoreTime = dateTimeEpochSecond;

		if (dataSourceScorers == null || dataSourceScorers.isEmpty()) {
			dataSourceMetrics.dataSourceScorerNotFound++;
			logger.error("No defined scorers for data source {}. Record: {}.", dataSource, record);
			return null;
		}

		return dataSourceScorers.stream()
				.map(dataSourceScorer -> dataSourceScorer.calculateScore(record))
				.collect(Collectors.toList());
	}

	private void loadScorers() {
		scorerConfService.getAllDataSourceScorerConfs().values().forEach(dataSourceScorerConfs -> {
			String dataSource = dataSourceScorerConfs.getDataSource();
			List<Scorer> dataSourceScorers = loadDataSourceScorers(dataSourceScorerConfs);
			dataSourceToScorersMap.put(dataSource, dataSourceScorers);
			ScoringServiceMetrics dataSourceMetrics = getDataSourceMetrics(dataSource);
			dataSourceMetrics.dataSourceScorers = dataSourceScorers.size();
		});
	}

	private List<Scorer> loadDataSourceScorers(DataSourceScorerConfs dataSourceScorerConfs) {
		List<IScorerConf> scorerConfs = dataSourceScorerConfs.getScorerConfs();
		List<Scorer> scorers = new ArrayList<>(scorerConfs.size());
		scorerConfs.forEach(scorerConf -> scorers.add(scorerFactoryService.getProduct(scorerConf)));
		return scorers;
	}

	private ScoringServiceMetrics getDataSourceMetrics(String dataSource) {
		if (!dataSourceToMetricsMap.containsKey(dataSource)) {
			dataSourceToMetricsMap.put(dataSource, new ScoringServiceMetrics(statsService, dataSource));
		}

		return dataSourceToMetricsMap.get(dataSource);
	}
}
