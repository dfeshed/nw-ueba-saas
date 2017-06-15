package fortscale.ml.scorer.enriched_events;

import fortscale.domain.feature.score.FeatureScore;

import fortscale.utils.logging.Logger;

import fortscale.ml.scorer.ScoringService;
import presidio.ade.domain.record.enriched.DlpFileRecord;
import presidio.ade.domain.record.enriched.EnrichedDlpFileRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.scored.enriched_scored.AdeScoredDlpFileContext;
import presidio.ade.domain.record.scored.enriched_scored.AdeScoredDlpFileRecord;
import presidio.ade.domain.record.scored.enriched_scored.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 * Created by YaronDL on 6/14/2017.
 */
public class EnrichedEventsScoringServiceImpl implements EnrichedEventsScoringService{
    private static final Logger logger = Logger.getLogger(EnrichedEventsScoringServiceImpl.class);

    private ScoringService<EnrichedRecord> scorersService;
    private ScoredEnrichedDataStore scoredEnrichedDataStore;

    public EnrichedEventsScoringServiceImpl(ScoringService<EnrichedRecord> scorersService, ScoredEnrichedDataStore scoredEnrichedDataStore) {
        this.scorersService = scorersService;
        this.scoredEnrichedDataStore = scoredEnrichedDataStore;
    }

    public List<AdeScoredEnrichedRecord> scoreAndStoreEvents(List<EnrichedRecord> enrichedRecordList) {
        List<AdeScoredEnrichedRecord> scoredRecords = new ArrayList<>();
        for (EnrichedRecord enrichedRecord : enrichedRecordList) {
            List<FeatureScore> featureScoreList = scorersService.score(enrichedRecord);
            fillAdeEnrichedScoredRecordList(scoredRecords, enrichedRecord, featureScoreList);
        }

        scoredEnrichedDataStore.store(scoredRecords);
        return scoredRecords;
    }

    public void fillAdeEnrichedScoredRecordList(List<AdeScoredEnrichedRecord> scoredRecordList, EnrichedRecord enrichedRecord, List<FeatureScore> featureScoreList){
        //expect to get as a root the feature score and inside it all the relevant features.
        if(featureScoreList.size() == 0){
            logger.warn("after calculating an enriched record we got an empty feature score list!!! the enrich record: {}", enrichedRecord);
            return;
        }

        FeatureScore eventScore = featureScoreList.get(0);
        for (FeatureScore featureScore : eventScore.getFeatureScores()) {
            AdeScoredEnrichedRecord scoredRecord = buildAdeEnrichedScoredRecord(enrichedRecord, featureScore);
            scoredRecordList.add(scoredRecord);
        }
    }

    public AdeScoredEnrichedRecord buildAdeEnrichedScoredRecord(EnrichedRecord enrichedRecord, FeatureScore featureScore) {
        //TODO: fix the implementation.
        AdeScoredDlpFileRecord ret = null;
        if(enrichedRecord.getDataSource().equals(DlpFileRecord.DLP_FILE_STR)){
            AdeScoredDlpFileContext context = new AdeScoredDlpFileContext((EnrichedDlpFileRecord) enrichedRecord);
            String featureName = featureScore.getName();
            ret = new AdeScoredDlpFileRecord(enrichedRecord.getDate_time(), featureName, featureScore.getScore(), featureScore.getFeatureScores(), context);
        }

        return ret;
    }
}
