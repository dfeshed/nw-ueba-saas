package fortscale.ml.scorer.enriched_events;

import fortscale.domain.feature.score.FeatureScore;

import fortscale.utils.logging.Logger;

import fortscale.ml.scorer.ScoringService;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.scored.AdeScoredRecord;
import presidio.ade.domain.store.scored.ScoredDataStore;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 * Created by YaronDL on 6/14/2017.
 */
public class EnrichedEventsScoringServiceImpl implements EnrichedEventsScoringService{
    private static final Logger logger = Logger.getLogger(EnrichedEventsScoringServiceImpl.class);

    private ScoringService<EnrichedRecord> scorersService;
    private ScoredDataStore scoredDataStore;

    public EnrichedEventsScoringServiceImpl(ScoringService<EnrichedRecord> scorersService, ScoredDataStore scoredDataStore) {
        this.scorersService = scorersService;
        this.scoredDataStore = scoredDataStore;
    }

    public List<AdeScoredRecord> scoreAndStoreEvents(List<EnrichedRecord> enrichedRecordList) {
        List<AdeScoredRecord> scoredRecords = new ArrayList<>();
        for (EnrichedRecord enrichedRecord : enrichedRecordList) {
            List<FeatureScore> featureScoreList = scorersService.score(enrichedRecord);
            fillAdeScoredRecordList(scoredRecords, enrichedRecord, featureScoreList);
        }

        scoredDataStore.store(scoredRecords);
        return scoredRecords;
    }

    public void fillAdeScoredRecordList(List<AdeScoredRecord> scoredRecordList, EnrichedRecord enrichedRecord, List<FeatureScore> featureScoreList){
        //expect to get as a root the feature score and inside it all the relevant features.
        if(featureScoreList.size() == 0){
            logger.warn("after calculating an enriched record we got an empty feature score list!!! the enrich record: {}", enrichedRecord);
            return;
        }

        FeatureScore eventScore = featureScoreList.get(0);
        for (FeatureScore featureScore : eventScore.getFeatureScores()) {
            AdeScoredRecord scoredRecord = buildAdeScoredRecord(enrichedRecord, featureScore);
            scoredRecordList.add(scoredRecord);
        }
    }

    public AdeScoredRecord buildAdeScoredRecord(EnrichedRecord enrichedRecord, FeatureScore featureScore) {
        //TODO:
        return null;
    }
}
