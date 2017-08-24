package fortscale.ml.scorer.feature_aggregation_events;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.scorer.ScoringService;
import fortscale.utils.logging.Logger;
import fortscale.utils.recordreader.RecordReaderFactoryService;
import presidio.ade.domain.record.AdeAggregationReader;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;

import java.util.ArrayList;
import java.util.List;


public class FeatureAggregationScoringServiceImpl implements FeatureAggregationScoringService {
    private static final Logger logger = Logger.getLogger(FeatureAggregationScoringServiceImpl.class);

    private RecordReaderFactoryService recordReaderFactoryService;
    private ScoringService scoringService;
    private ScoredFeatureAggregatedRecordBuilder scoredFeatureAggregatedRecordBuilder;

    public FeatureAggregationScoringServiceImpl(
            RecordReaderFactoryService recordReaderFactoryService,
            ScoringService scoringService,
            ScoredFeatureAggregatedRecordBuilder scoredFeatureAggregatedRecordBuilder) {

        this.recordReaderFactoryService = recordReaderFactoryService;
        this.scoringService = scoringService;
        this.scoredFeatureAggregatedRecordBuilder = scoredFeatureAggregatedRecordBuilder;
    }

    public List<ScoredFeatureAggregationRecord>  scoreEvents(List<AdeAggregationRecord> featureAdeAggrRecords) {
        List<ScoredFeatureAggregationRecord> scoredFeatureAggregationRecords = new ArrayList<>();

        if (featureAdeAggrRecords.size() == 0) {
            logger.warn("got an empty feature aggregation record list");
        }

        for (AdeAggregationRecord featureAdeAggrRecord : featureAdeAggrRecords) {
            AdeRecordReader adeRecordReader = (AdeAggregationReader) recordReaderFactoryService.getRecordReader(featureAdeAggrRecord);
            List<FeatureScore> featureScoreList = scoringService.score(adeRecordReader);
            scoredFeatureAggregatedRecordBuilder.fill(scoredFeatureAggregationRecords, featureAdeAggrRecord, featureScoreList);
        }

        return scoredFeatureAggregationRecords;
    }
}
