package fortscale.ml.scorer;

import fortscale.common.event.DataEntitiesConfigWithBlackList;
import fortscale.common.event.Event;
import fortscale.common.feature.extraction.FeatureExtractService;
import fortscale.domain.core.FeatureScore;
import fortscale.domain.core.FeatureScoreList;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.config.DataSourceScorerConfs;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.metrics.ScorersServiceMetrics;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScorersService{
    private static final Logger logger = Logger.getLogger(ScorersService.class);
    private static final String NO_SCORERS_FOR_DATA_SOURCE_ERROR_MSG =
            "No scorers are defined for data source: {}. Processed message: {}.";

    @Autowired
    private ModelsCacheService modelsCacheService;

    @Autowired
    private FeatureExtractService featureExtractService;

    @Autowired
    private ScorerConfService scorerConfService;

    @Autowired
    private FactoryService<Scorer> scorerFactoryService;

    @Autowired
    private DataEntitiesConfigWithBlackList dataEntitiesConfigWithBlackList;

    @Autowired
    private StatsService statsService;

    private Map<String,ScorersServiceMetrics> dataSourceToMetricsMap = new HashMap<>();

    private Map<String, List<Scorer>> dataSourceToScorerListMap = new HashMap<>();

    private boolean isScorersLoaded = false;

    private List<Scorer> loadDataSourceScorers(DataSourceScorerConfs dataSourceScorerConfs) {
        Assert.notNull(dataSourceScorerConfs);
        List<IScorerConf> scorerConfs = dataSourceScorerConfs.getScorerConfs();
        List<Scorer> scorers = new ArrayList<>(scorerConfs.size());
        for(IScorerConf scorerConf: scorerConfs) {
            Scorer scorer = scorerFactoryService.getProduct(scorerConf);
            scorers.add(scorer);
        }
        return scorers;
    }

    public List<FeatureScore> calculateScores(Event event, long eventEpochTimeInSec) throws Exception {
        Assert.notNull(event);
        loadScorers();
        String dataSource = event.getDataSource();
        ScorersServiceMetrics metrics = getDataSourceMetric(dataSource);
        List<Scorer> dataSourceScorers = dataSourceToScorerListMap.get(dataSource);

        metrics.calculateScoreTime = eventEpochTimeInSec;

        if (dataSourceScorers == null || dataSourceScorers.isEmpty()) {
            metrics.dataSourceScorerNotFound++;
            logger.error(NO_SCORERS_FOR_DATA_SOURCE_ERROR_MSG, dataSource, event.getJSONObject().toJSONString());
            return null;
        }

        List<FeatureScore> featureScores = new FeatureScoreList();

        for (Scorer scorer : dataSourceScorers) {
            FeatureScore featureScore = scorer.calculateScore(event, eventEpochTimeInSec);
            featureScores.add(featureScore);
        }

        return featureScores;
    }

    public void loadScorers() throws Exception {
        if (!isScorersLoaded) {
            Map<String, DataSourceScorerConfs> dataSourceToDataSourceScorerConfsMap = scorerConfService.getAllDataSourceScorerConfs();

            for (DataSourceScorerConfs dataSourceScorerConfs : dataSourceToDataSourceScorerConfsMap.values()) {
                List<Scorer> dataSourceScorers = loadDataSourceScorers(dataSourceScorerConfs);
                String dataSource = dataSourceScorerConfs.getDataSource();
                dataSourceToScorerListMap.put(dataSource, dataSourceScorers);

                ScorersServiceMetrics metrics = getDataSourceMetric(dataSource);
                metrics.dataSourceScorers = dataSourceScorers.size();
            }

            isScorersLoaded = true;
        }
    }

    public ScorersServiceMetrics getDataSourceMetric(String dataSource) {
        // create new metric for data source, if not included in the map
        if (!dataSourceToMetricsMap.containsKey(dataSource)) {
            ScorersServiceMetrics metrics = new ScorersServiceMetrics(statsService, dataSource);
            dataSourceToMetricsMap.put(dataSource, metrics);
        }
        return dataSourceToMetricsMap.get(dataSource);
    }
}
