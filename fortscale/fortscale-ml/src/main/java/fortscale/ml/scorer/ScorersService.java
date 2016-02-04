package fortscale.ml.scorer;

import fortscale.common.event.DataEntitiesConfigWithBlackList;
import fortscale.common.event.RawEvent;
import fortscale.common.feature.extraction.FeatureExtractService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.config.DataSourceScorerConfs;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.hive.com.esotericsoftware.minlog.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScorersService{
    private static final Logger logger = Logger.getLogger(ScorersService.class);
    private static final java.lang.String NO_SCORERS_FOR_DATA_SOURCE_ERROR_MSG = "No scorers are defined for data source: %s. Processed message: %s";

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


    public List<FeatureScore> calculateScores(JSONObject event, long eventEpochTimeInSec, String dataSource) throws Exception{
        Assert.notNull(dataSource);
        Assert.notNull(event);
        loadScorers();
        List<Scorer> dataSourceScorers = dataSourceToScorerListMap.get(dataSource);
        if(dataSource==null) {
            Log.error(String.format(NO_SCORERS_FOR_DATA_SOURCE_ERROR_MSG, dataSource, event.toJSONString()));
            return null;
        }

        List<FeatureScore> featureScores = new ArrayList<>();
        RawEvent eventMessage = new RawEvent(event, dataEntitiesConfigWithBlackList, dataSource);

        for(Scorer scorer: dataSourceScorers) {
            FeatureScore featureScore = scorer.calculateScore(eventMessage, eventEpochTimeInSec);
            featureScores.add(featureScore);
        }

        return featureScores;
    }

    public void loadScorers() throws Exception {
        if(!isScorersLoaded) {
            Map<String, DataSourceScorerConfs> dataSourceToDataSourceScorerConfsMap = scorerConfService.getAllDataSourceScorerConfs();
            for (DataSourceScorerConfs dataSourceScorerConfs : dataSourceToDataSourceScorerConfsMap.values()) {
                List<Scorer> dataSourceScorers = loadDataSourceScorers(dataSourceScorerConfs);
                dataSourceToScorerListMap.put(dataSourceScorerConfs.getDataSource(), dataSourceScorers);
            }
        }
    }
}
