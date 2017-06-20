package fortscale.ml.scorer.enriched_events;

import fortscale.domain.feature.score.FeatureScore;

import fortscale.utils.logging.Logger;

import fortscale.ml.scorer.ScoringService;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.scored.enriched_scored.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.scored.enriched_scored.DataSourceToAdeScoredEnrichedRecordClassResolver;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO
 * Created by YaronDL on 6/14/2017.
 */
public class EnrichedEventsScoringServiceImpl implements EnrichedEventsScoringService{
    private static final Logger logger = Logger.getLogger(EnrichedEventsScoringServiceImpl.class);

    private ScoringService<EnrichedRecord> scoringService;
    private ScoredEnrichedDataStore scoredEnrichedDataStore;
    private AdeEnrichedScoredRecordBuilder adeEnrichedScoredRecordBuilder;


    public EnrichedEventsScoringServiceImpl(ScoringService<EnrichedRecord> scoringService,
                                            ScoredEnrichedDataStore scoredEnrichedDataStore,
                                            AdeEnrichedScoredRecordBuilder adeEnrichedScoredRecordBuilder) {
        this.scoringService = scoringService;
        this.scoredEnrichedDataStore = scoredEnrichedDataStore;
        this.adeEnrichedScoredRecordBuilder = adeEnrichedScoredRecordBuilder;
    }

    public List<AdeScoredEnrichedRecord> scoreAndStoreEvents(List<EnrichedRecord> enrichedRecordList) {
        if(enrichedRecordList.size() == 0){
            logger.warn("got an empty enriched record list");
            return Collections.emptyList();
        }

        List<AdeScoredEnrichedRecord> scoredRecords = new ArrayList<>();
        for (EnrichedRecord enrichedRecord : enrichedRecordList) {
            List<FeatureScore> featureScoreList = scoringService.score(enrichedRecord);
            adeEnrichedScoredRecordBuilder.fill(scoredRecords, enrichedRecord, featureScoreList);
        }

        scoredEnrichedDataStore.store(scoredRecords);
        return scoredRecords;
    }


}
