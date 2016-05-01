package fortscale.ml.scorer;

import fortscale.common.event.DataEntitiesConfigWithBlackList;
import fortscale.common.event.Event;
import fortscale.common.feature.extraction.FeatureExtractService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.config.DataSourceScorerConfs;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.logging.Logger;
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
        List<Scorer> dataSourceScorers = dataSourceToScorerListMap.get(dataSource);

        if (dataSourceScorers == null || dataSourceScorers.isEmpty()) {
            logger.error(NO_SCORERS_FOR_DATA_SOURCE_ERROR_MSG, dataSource, event.getJSONObject().toJSONString());
            return null;
        }

        List<FeatureScore> featureScores = new ArrayList<>();

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
                dataSourceToScorerListMap.put(dataSourceScorerConfs.getDataSource(), dataSourceScorers);
            }

            isScorersLoaded = true;
        }
    }
}
