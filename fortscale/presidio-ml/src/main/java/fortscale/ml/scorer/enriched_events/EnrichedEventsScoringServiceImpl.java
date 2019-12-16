package fortscale.ml.scorer.enriched_events;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.scorer.ScoringService;
import fortscale.utils.logging.Logger;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO
 * Created by YaronDL on 6/14/2017.
 */
public class EnrichedEventsScoringServiceImpl implements EnrichedEventsScoringService {
    private static final Logger logger = Logger.getLogger(EnrichedEventsScoringServiceImpl.class);

    private RecordReaderFactoryService recordReaderFactoryService;
    private ScoringService scoringService;
    private ScoredEnrichedDataStore scoredEnrichedDataStore;
    private AdeEnrichedScoredRecordBuilder adeEnrichedScoredRecordBuilder;
    private boolean isFilterZeroScore;

    public EnrichedEventsScoringServiceImpl(
            RecordReaderFactoryService recordReaderFactoryService,
            ScoringService scoringService,
            ScoredEnrichedDataStore scoredEnrichedDataStore,
            AdeEnrichedScoredRecordBuilder adeEnrichedScoredRecordBuilder,
            boolean isFilterZeroScore) {

        this.recordReaderFactoryService = recordReaderFactoryService;
        this.scoringService = scoringService;
        this.scoredEnrichedDataStore = scoredEnrichedDataStore;
        this.adeEnrichedScoredRecordBuilder = adeEnrichedScoredRecordBuilder;
        this.isFilterZeroScore = isFilterZeroScore;
    }

    public List<AdeScoredEnrichedRecord> scoreAndStoreEvents(List<EnrichedRecord> enrichedRecordList, boolean isStore, TimeRange timeRange, StoreMetadataProperties storeMetadataProperties) {
        if (enrichedRecordList.size() == 0) {
            logger.warn("got an empty enriched record list");
            return Collections.emptyList();
        }

        List<AdeScoredEnrichedRecord> scoredRecords = new ArrayList<>();

        for (EnrichedRecord enrichedRecord : enrichedRecordList) {
            AdeRecordReader adeRecordReader = (AdeRecordReader)recordReaderFactoryService.getRecordReader(enrichedRecord);
            List<FeatureScore> featureScoreList = scoringService.score(adeRecordReader,timeRange);
            adeEnrichedScoredRecordBuilder.fill(scoredRecords, enrichedRecord, featureScoreList, isFilterZeroScore);
        }

        if(isStore) {
            scoredEnrichedDataStore.store(scoredRecords, storeMetadataProperties);
        }
        return scoredRecords;
    }

    public void resetModelCache(){
        scoringService.resetModelCache();
    }
}
