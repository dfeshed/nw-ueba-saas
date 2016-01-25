package fortscale.ml.scorer;

import fortscale.common.event.EventMessage;
import fortscale.common.feature.extraction.FeatureExtractService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.config.DataSourceScorerConfs;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.factory.ScorersFactoryService;
import fortscale.utils.factory.FactoryService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScorersService{

    @Autowired
    private ModelsCacheService modelsCacheService;

    @Autowired
    private FeatureExtractService featureExtractService;

    @Autowired
    private ScorerConfService scorerConfService;

    @Autowired
    private ScorersFactoryService scorerFactoryService;

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
        List<FeatureScore> featureScores = new ArrayList<>();
        EventMessage eventMessage = new EventMessage(event);

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
